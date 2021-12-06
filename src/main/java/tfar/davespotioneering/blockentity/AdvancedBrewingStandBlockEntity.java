package tfar.davespotioneering.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.Events;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

import javax.annotation.Nullable;
import java.util.Arrays;

public class AdvancedBrewingStandBlockEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, BrewingStandDuck {
    /** an array of the output slot indices */

    //potions are 0,1,2
    private static final int[] POTIONS = new int[]{0, 1, 2};
    //ingredients are 3,4,5,6,7
    public static final int[] INGREDIENTS = new int[]{3,4,5,6,7};
    //fuel is 8
    public static final int FUEL = 8;

    public static final int TIME = 200;

    protected int xp;


    /** The ItemStacks currently placed in the slots of the brewing stand */
    private BrewingHandler brewingHandler = new BrewingHandler(9);
    private int brewTime;
    /** an integer with each bit specifying whether that slot of the stand contains a potion */
    private boolean[] filledSlots;
    /** used to check if the current ingredient has been removed from the brewing stand during brewing */
    private Item ingredientID;
    private int fuel;
    protected final IIntArray data = new IIntArray() {
        public int get(int index) {
            switch(index) {
                case 0:
                    return brewTime;
                case 1:
                    return fuel;
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {
            switch(index) {
                case 0:
                    brewTime = value;
                    break;
                case 1:
                    fuel = value;
            }

        }

        public int size() {
            return 2;
        }
    };

    public AdvancedBrewingStandBlockEntity() {
        super(ModBlockEntityTypes.ADVANCED_BREWING_STAND);
    }

    protected AdvancedBrewingStandBlockEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.davespotioneering.advanced_brewing");
    }

    public void tick() {
        ItemStack fuelStack = this.brewingHandler.getStackInSlot(FUEL);
        if (this.fuel <= 0 && fuelStack.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            fuelStack.shrink(1);
            this.markDirty();
        }

        boolean canBrew = this.canBrew();
        boolean brewing = this.brewTime > 0;
        ItemStack ing = getPriorityIngredient().getRight();
        if (brewing) {
            --this.brewTime;
            boolean done = this.brewTime == 0;
            if (done && canBrew) {
                this.brewPotions();
                this.markDirty();
            } else if (!canBrew) {
                this.brewTime = 0;
                this.markDirty();
            } else if (this.ingredientID != ing.getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (canBrew && this.fuel > 0) {
            --this.fuel;
            this.brewTime = TIME;
            this.ingredientID = ing.getItem();
            this.markDirty();
        }

        if (!this.world.isRemote) {
            setBottleBlockStates();
        }
    }

    private void setBottleBlockStates() {
        boolean[] aboolean = this.createFilledSlotsArray();
        if (!Arrays.equals(aboolean, this.filledSlots)) {
            this.filledSlots = aboolean;
            BlockState blockstate = this.world.getBlockState(this.getPos());
            if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
                return;
            }

            for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
                blockstate = blockstate.with(BrewingStandBlock.HAS_BOTTLE[i], aboolean[i]);
            }

            this.world.setBlockState(this.pos, blockstate, 2);
        }
    }

    //searches 7 => 3
    public Pair<Integer,ItemStack> getPriorityIngredient() {
        for (int i = 7; i > 2;i--) {
            ItemStack stack = brewingHandler.getStackInSlot(i);
            if (!stack.isEmpty() && isThereARecipe(stack)) {
                return Pair.of(i,stack);
            }
        }
        return Pair.of(-1,ItemStack.EMPTY);
    }


    public boolean isThereARecipe(ItemStack ingredient) {

        if (!ingredient.isEmpty()) {
            return BrewingRecipeRegistry.canBrew(brewingHandler.getStacks(), ingredient, POTIONS) || ingredient.getItem() == Items.MILK_BUCKET; // divert to VanillaBrewingRegistry
        }
        if (ingredient.isEmpty()) {
            return false;
        } else if (!PotionBrewing.isReagent(ingredient)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.brewingHandler.getStackInSlot(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasConversions(itemstack1, ingredient)) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * Creates an array of boolean values, each value represents a potion input slot, value is true if the slot is not
     * null.
     */
    public boolean[] createFilledSlotsArray() {
        boolean[] aboolean = new boolean[3];

        for(int i = 0; i < 3; ++i) {
            if (!this.brewingHandler.getStackInSlot(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    private boolean canBrew() {
        ItemStack itemstack = getPriorityIngredient().getRight();
        if (!itemstack.isEmpty()) {

            if (itemstack.getItem() == Items.MILK_BUCKET) {
                if (canMilkify()) {
                    return true;
                }
            }

            return BrewingRecipeRegistry.canBrew(brewingHandler.getStacks(), itemstack, POTIONS); // divert to VanillaBrewingRegistry
        }
        if (itemstack.isEmpty()) {
            return false;
        } else if (!PotionBrewing.isReagent(itemstack)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.brewingHandler.getStackInSlot(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasConversions(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void brewPotions() {
        if (ForgeEventFactory.onPotionAttemptBrew(brewingHandler.getStacks())) return;
        Pair<Integer,ItemStack> pair = getPriorityIngredient();
        ItemStack ingredient = pair.getRight();

        boolean canMilkify = ingredient.getItem() == Items.MILK_BUCKET;

        BrewingRecipeRegistry.brewPotions(brewingHandler.getStacks(), ingredient, POTIONS);
        ForgeEventFactory.onPotionBrewed(brewingHandler.getStacks());
        Events.potionBrew(this,ingredient);

        if (canMilkify) {
            for (int i = 0; i < POTIONS.length; i++) {
                ItemStack potion = brewingHandler.getStackInSlot(i);
                Util.milkifyPotion(potion);
            }
        }

        BlockPos blockpos = this.getPos();
        if (ingredient.hasContainerItem()) {
            ItemStack ingredientContainerItem = ingredient.getContainerItem();
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                ingredient = ingredientContainerItem;
            } else if (!this.world.isRemote) {
                InventoryHelper.spawnItemStack(this.world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), ingredientContainerItem);
            }
        }
        //todo
        else ingredient.shrink(1);

        this.brewingHandler.setStackInSlot(pair.getLeft(), ingredient);
        this.world.playEvent(1035, blockpos, 0);
    }

    private boolean canMilkify() {
        for (int i : POTIONS) {
            ItemStack potionStack = brewingHandler.getStackInSlot(i);
            if (potionStack.getItem() instanceof PotionItem) {
                Potion potion = PotionUtils.getPotionFromItem(potionStack);
                String name = potion.getRegistryName().toString();
                if (name.contains("long") || name.contains("strong")) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        CompoundNBT items = nbt.getCompound("Items");
        brewingHandler.deserializeNBT(items);
        this.brewTime = nbt.getShort("BrewTime");
        this.fuel = nbt.getInt("Fuel");
        xp = nbt.getInt("xp");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putShort("BrewTime", (short)this.brewTime);
        compound.put("Items",brewingHandler.serializeNBT());
        compound.putInt("Fuel", this.fuel);
        compound.putInt("xp",xp);
        return compound;
    }

    @Override
    public ITextComponent getDisplayName() {
        return getDefaultName();
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new AdvancedBrewingStandContainer(id, playerInventory, brewingHandler, this.data,this);
    }

    @Override
    public void addXp(double xp) {
        this.xp += xp;
    }

    @Override
    public void dump(PlayerEntity player) {
        Util.splitAndSpawnExperience(world,player.getPositionVec(),xp);
        xp = 0;
        markDirty();
    }

    /*net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        for (int x = 0; x < handlers.length; x++)
            handlers[x].invalidate();
    }*/
}
