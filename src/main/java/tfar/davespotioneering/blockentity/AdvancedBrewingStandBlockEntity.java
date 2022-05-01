package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.Events;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

import javax.annotation.Nullable;
import java.util.Arrays;

public class AdvancedBrewingStandBlockEntity extends BlockEntity implements TickableBlockEntity, MenuProvider, BrewingStandDuck {
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

    public AdvancedBrewingStandBlockEntity() {
        super(ModBlockEntityTypes.COMPOUND_BREWING_STAND);
    }

    protected AdvancedBrewingStandBlockEntity(BlockEntityType<?> typeIn) {
        super(typeIn);
    }

    protected Component getDefaultName() {
        return new TranslatableComponent("container.davespotioneering.compound_brewing");
    }

    public void tick() {
        ItemStack fuelStack = this.brewingHandler.getItem(FUEL);
        if (this.fuel <= 0 && fuelStack.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            fuelStack.shrink(1);
            this.setChanged();
        }

        boolean canBrew = this.canBrew();
        boolean brewing = this.brewTime > 0;
        ItemStack ing = getPriorityIngredient().getRight();
        if (brewing) {
            --this.brewTime;
            boolean done = this.brewTime == 0;
            if (done && canBrew) {
                this.brewPotions();
                this.setChanged();
            } else if (!canBrew) {
                this.brewTime = 0;
                this.setChanged();
            } else if (this.ingredientID != ing.getItem()) {
                this.brewTime = 0;
                this.setChanged();
            }
        } else if (canBrew && this.fuel > 0) {
            --this.fuel;
            this.brewTime = TIME;
            this.ingredientID = ing.getItem();
            this.setChanged();
        }

        if (!this.level.isClientSide) {
            setBottleBlockStates();
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
            ItemStack stack = brewingHandler.getItem(i);
            if (!stack.isEmpty() && isThereARecipe(stack)) {
                return Pair.of(i,stack);
            }
        }
        return Pair.of(-1,ItemStack.EMPTY);
    }


    public boolean isThereARecipe(ItemStack ingredient) {

        if (!ingredient.isEmpty()) {
            return PotionBrewing.hasMix(ingredient,ingredient);
        }
        return false;

    }

    /**
     * Creates an array of boolean values, each value represents a potion input slot, value is true if the slot is not
     * null.
     */
    public boolean[] createFilledSlotsArray() {
        boolean[] aboolean = new boolean[3];

        for(int i = 0; i < 3; ++i) {
            if (!this.brewingHandler.getItem(i).isEmpty()) {
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

            return PotionBrewing.hasMix(itemstack, itemstack);
        }
        return false;
    }

    private void brewPotions() {
        Pair<Integer,ItemStack> pair = getPriorityIngredient();
        ItemStack ingredient = pair.getRight();

        boolean canMilkify = ingredient.getItem() == Items.MILK_BUCKET;

        //note: this is changed from the BrewingRecipeRegistry version to allow for >1 potion in a stack
        Util.brewPotions(brewingHandler.getStacks(), ingredient, POTIONS);
        Events.potionBrew(this,ingredient);

        if (canMilkify) {
            for (int i = 0; i < POTIONS.length; i++) {
                ItemStack potion = brewingHandler.getItem(i);
                Util.milkifyPotion(potion);
            }
        }

        BlockPos blockpos = this.getBlockPos();
        if (ingredient.getItem().hasCraftingRemainingItem()) {
            ItemStack ingredientContainerItem = ingredient.getItem().getCraftingRemainingItem().getDefaultInstance();
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                ingredient = ingredientContainerItem;
            } else if (!this.level.isClientSide) {
                Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), ingredientContainerItem);
            }
        }
        //todo
        else ingredient.shrink(1);

        this.brewingHandler.setItem(pair.getLeft(), ingredient);
        //plays brewing stand block brewing finished sound
        this.level.levelEvent(1035, blockpos, 0);
    }

    private boolean canMilkify() {
        for (int i : POTIONS) {
            ItemStack potionStack = brewingHandler.getItem(i);
            if (potionStack.getItem() instanceof PotionItem) {
                Potion potion = PotionUtils.getPotion(potionStack);
                String name = Registry.POTION.getKey(potion).toString();
                if (name.contains("long") || name.contains("strong")) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void load(BlockState state, CompoundTag nbt) {
        super.load(state, nbt);
        ListTag items = nbt.getList("Items",10);
        brewingHandler.fromTag(items);
        this.brewTime = nbt.getShort("BrewTime");
        this.fuel = nbt.getInt("Fuel");
        xp = nbt.getInt("xp");
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.putShort("BrewTime", (short)this.brewTime);
        compound.put("Items",brewingHandler.serializeNBT());
        compound.putInt("Fuel", this.fuel);
        compound.putInt("xp",xp);
        return compound;
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
}
