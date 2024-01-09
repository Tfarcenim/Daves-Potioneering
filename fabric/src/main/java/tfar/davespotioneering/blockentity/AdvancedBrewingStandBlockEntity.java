package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.FabricEvents;
import tfar.davespotioneering.FabricUtil;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

import javax.annotation.Nullable;
import java.util.Arrays;

public class AdvancedBrewingStandBlockEntity extends BlockEntity implements  MenuProvider, BrewingStandDuck {
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

    public AdvancedBrewingStandBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityTypes.COMPOUND_BREWING_STAND,blockPos,blockState);
    }

    protected AdvancedBrewingStandBlockEntity(BlockEntityType<?> typeIn, BlockPos blockPos, BlockState blockState) {
        super(typeIn,blockPos,blockState);
    }

    protected Component getDefaultName() {
        return Component.translatable("container.davespotioneering.compound_brewing");
    }

    public static void tick(Level world, BlockPos blockPos, BlockState blockState, AdvancedBrewingStandBlockEntity brewingStandBlockEntity) {
        ItemStack fuelStack = brewingStandBlockEntity.brewingHandler.getItem(FUEL);
        if (brewingStandBlockEntity.fuel <= 0 && fuelStack.getItem() == Items.BLAZE_POWDER) {
            brewingStandBlockEntity.fuel = 20;
            fuelStack.shrink(1);
            setChanged(world,blockPos,blockState);
        }

        boolean canBrew = brewingStandBlockEntity.canBrew();
        boolean brewing = brewingStandBlockEntity.brewTime > 0;
        ItemStack ing = brewingStandBlockEntity.getPriorityIngredient().getRight();
        if (brewing) {
            --brewingStandBlockEntity.brewTime;
            boolean done = brewingStandBlockEntity.brewTime == 0;
            if (done && canBrew) {
                brewingStandBlockEntity.brewPotions();
                setChanged(world,blockPos,blockState);
            } else if (!canBrew) {
                brewingStandBlockEntity.brewTime = 0;
                setChanged(world,blockPos,blockState);
            } else if (brewingStandBlockEntity.ingredientID != ing.getItem()) {
                brewingStandBlockEntity.brewTime = 0;
                setChanged(world,blockPos,blockState);
            }
        } else if (canBrew && brewingStandBlockEntity.fuel > 0) {
            --brewingStandBlockEntity.fuel;
            brewingStandBlockEntity.brewTime = TIME;
            brewingStandBlockEntity.ingredientID = ing.getItem();
            setChanged(world,blockPos,blockState);
        }

        if (!brewingStandBlockEntity.level.isClientSide) {
            brewingStandBlockEntity.setBottleBlockStates();
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
            ItemStack ing = brewingHandler.getItem(i);
            if (!ing.isEmpty() && isThereARecipe(ing)) {
                return Pair.of(i,ing);
            }
        }
        return Pair.of(-1,ItemStack.EMPTY);
    }


    public boolean isThereARecipe(ItemStack ingredient) {
        for (int i : POTIONS) {
            ItemStack potion = brewingHandler.getItem(i);
            if (PotionBrewing.hasMix(potion,ingredient))
                return true;
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
        ItemStack ing = getPriorityIngredient().getRight();
        if (!ing.isEmpty()) {
            return isThereARecipe(ing);
        }
        return false;
    }

    private void brewPotions() {
        Pair<Integer,ItemStack> pair = getPriorityIngredient();
        ItemStack ingredient = pair.getRight();

        //note: this is changed from the BrewingRecipeRegistry version to allow for >1 potion in a stack
        FabricUtil.brewPotions(brewingHandler.getItems(), ingredient, POTIONS);
        FabricEvents.potionBrew(this,ingredient);

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

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        ContainerHelper.loadAllItems(nbt,brewingHandler.getItems());
        this.brewTime = nbt.getShort("BrewTime");
        this.fuel = nbt.getInt("Fuel");
        xp = nbt.getInt("xp");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putShort("BrewTime", (short)this.brewTime);
        ContainerHelper.saveAllItems(compound,brewingHandler.getItems());
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

    public BrewingHandler getBrewingHandler() {
        return brewingHandler;
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> blockEntityType, BlockEntityType<E> blockEntityType2, BlockEntityTicker<? super E> blockEntityTicker) {
        return blockEntityType2 == blockEntityType ? (BlockEntityTicker<A>) blockEntityTicker : null;
    }

    protected static void setChanged(Level world, BlockPos blockPos, BlockState blockState) {
        world.blockEntityChanged(blockPos);
        if (!blockState.isAir()) {
            world.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
        }
    }
}
