package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.Events;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.inv.SidedItemHandler;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

public class AdvancedBrewingStandBlockEntity extends BlockEntity implements MenuProvider, BrewingStandDuck {
    /** an array of the output slot indices */

    //potions are 0,1,2
    public static final int[] POTIONS = new int[]{0, 1, 2};
    //ingredients are 3,4,5,6,7
    public static final int[] INGREDIENTS = new int[]{3,4,5,6,7};
    //fuel is 8
    public static final int FUEL = 8;

    public static final int TIME = 200;

    protected int xp;

    public static final int SLOTS = POTIONS.length + INGREDIENTS.length + 1;

    /** The ItemStacks currently placed in the slots of the brewing stand */
    private final BrewingHandler brewingHandler = new BrewingHandler(SLOTS);
    private int brewTime;
    /** an integer with each bit specifying whether that slot of the stand contains a potion */
    private boolean[] filledSlots;
    /** used to check if the current ingredient has been removed from the brewing stand during brewing */
    private Item ingredientID;
    private int fuel;
    protected final ContainerData data = new ContainerData() {
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

        public int getCount() {
            return 2;
        }
    };

    public AdvancedBrewingStandBlockEntity(BlockPos p_155283_, BlockState p_155284_) {
        this(ModBlockEntityTypes.COMPOUND_BREWING_STAND,p_155283_,p_155284_);
    }

    protected AdvancedBrewingStandBlockEntity(BlockEntityType<?> typeIn,BlockPos p_155283_, BlockState p_155284_) {
        super(typeIn,p_155283_,p_155284_);
    }

    protected Component getDefaultName() {
        return new TranslatableComponent("container.davespotioneering.compound_brewing");
    }

    public static void serverTick(Level p_155286_, BlockPos p_155287_, BlockState p_155288_, AdvancedBrewingStandBlockEntity p_155289_) {
        ItemStack fuelStack = p_155289_.brewingHandler.getStackInSlot(FUEL);
        if (p_155289_.fuel <= 0 && fuelStack.getItem() == Items.BLAZE_POWDER) {
            p_155289_.fuel = 20;
            fuelStack.shrink(1);
            p_155289_.setChanged();
        }

        boolean canBrew = p_155289_.canBrew();
        boolean brewing = p_155289_.brewTime > 0;
        ItemStack ing = p_155289_.getPriorityIngredient().getRight();
        if (brewing) {
            --p_155289_.brewTime;
            boolean done = p_155289_.brewTime == 0;
            if (done && canBrew) {
                p_155289_.brewPotions();
                p_155289_.setChanged();
            } else if (!canBrew) {
                p_155289_.brewTime = 0;
                p_155289_.setChanged();
            } else if (p_155289_.ingredientID != ing.getItem()) {
                p_155289_.brewTime = 0;
                p_155289_.setChanged();
            }
        } else if (canBrew && p_155289_.fuel > 0) {
            --p_155289_.fuel;
            p_155289_.brewTime = TIME;
            p_155289_.ingredientID = ing.getItem();
            p_155289_.setChanged();
        }

        if (!p_155289_.level.isClientSide) {
            p_155289_.setBottleBlockStates();
        }
    }

    private void setBottleBlockStates() {
        boolean[] aboolean = this.createFilledSlotsArray();
        if (!Arrays.equals(aboolean, this.filledSlots)) {
            this.filledSlots = aboolean;
            BlockState blockstate = this.level.getBlockState(this.getBlockPos());
            if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
                return;
            }

            for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
                blockstate = blockstate.setValue(BrewingStandBlock.HAS_BOTTLE[i], aboolean[i]);
            }

            this.level.setBlock(this.worldPosition, blockstate, 2);
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
        } else if (!PotionBrewing.isIngredient(ingredient)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.brewingHandler.getStackInSlot(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasMix(itemstack1, ingredient)) {
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
        } else if (!PotionBrewing.isIngredient(itemstack)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.brewingHandler.getStackInSlot(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasMix(itemstack1, itemstack)) {
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

        //note: this is changed from the BrewingRecipeRegistry version to allow for >1 potion in a stack
        Util.brewPotions(brewingHandler.getStacks(), ingredient, POTIONS);
        ForgeEventFactory.onPotionBrewed(brewingHandler.getStacks());
        Events.potionBrew(this,ingredient);

        BlockPos blockpos = this.getBlockPos();
        if (ingredient.hasContainerItem()) {
            ItemStack ingredientContainerItem = ingredient.getContainerItem();
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                ingredient = ingredientContainerItem;
            } else if (!this.level.isClientSide) {
                Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), ingredientContainerItem);
            }
        }
        //todo
        else ingredient.shrink(1);

        this.brewingHandler.setStackInSlot(pair.getLeft(), ingredient);
        //plays brewing stand block brewing finished sound
        this.level.levelEvent(1035, blockpos, 0);
    }

    private boolean canMilkify() {
        for (int i : POTIONS) {
            ItemStack potionStack = brewingHandler.getStackInSlot(i);
            if (potionStack.getItem() instanceof PotionItem) {
                Potion potion = PotionUtils.getPotion(potionStack);
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
    public void load(CompoundTag nbt) {
        super.load(nbt);
        CompoundTag items = nbt.getCompound("Items");
        brewingHandler.deserializeNBT(items);
        this.brewTime = nbt.getShort("BrewTime");
        this.fuel = nbt.getInt("Fuel");
        xp = nbt.getInt("xp");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putShort("BrewTime", (short)this.brewTime);
        compound.put("Items",brewingHandler.serializeNBT());
        compound.putInt("Fuel", this.fuel);
        compound.putInt("xp",xp);
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new AdvancedBrewingStandContainer(id, playerInventory, brewingHandler, this.data,this);
    }

    @Override
    public void addXp(double xp) {
        this.xp += xp;
    }

    @Override
    public void dump(Player player) {
        if(xp > 0) {
            Util.splitAndSpawnExperience(level, player.position(), xp);
            xp = 0;
            setChanged();
        }
    }

    Map<Direction,LazyOptional<? extends IItemHandler>> handlers = SidedItemHandler.create(brewingHandler);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handlers.get(facing).cast();
        }
        return super.getCapability(capability, facing);
    }

    public BrewingHandler getBrewingHandler() {
        return brewingHandler;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (Map.Entry<Direction, LazyOptional<? extends IItemHandler>> entry : handlers.entrySet()) {
            entry.getValue().invalidate();
        }
    }
}
