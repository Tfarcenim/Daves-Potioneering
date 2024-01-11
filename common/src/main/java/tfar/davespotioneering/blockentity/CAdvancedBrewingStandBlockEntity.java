package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
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
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.menu.CAdvancedBrewingStandMenu;

import java.util.Arrays;

public abstract class CAdvancedBrewingStandBlockEntity extends BlockEntity implements MenuProvider, BrewingStandDuck {

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
    protected BasicInventoryBridge handler;

    public CAdvancedBrewingStandBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    protected int brewTime;
    /** an integer with each bit specifying whether that slot of the stand contains a potion */
    protected boolean[] filledSlots;
    /** used to check if the current ingredient has been removed from the brewing stand during brewing */
    protected Item ingredientID;
    protected int fuel;
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

    protected Component getDefaultName() {
        return Component.translatable("container.davespotioneering.compound_brewing");
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    public BasicInventoryBridge getBrewingHandler() {
        return handler;
    }


    public static void serverTick(Level p_155286_, BlockPos p_155287_, BlockState p_155288_, CAdvancedBrewingStandBlockEntity brewingStand) {
        ItemStack fuelStack = brewingStand.handler.$getStackInSlot(FUEL);
        if (brewingStand.fuel <= 0 && fuelStack.getItem() == Items.BLAZE_POWDER) {
            brewingStand.fuel = 20;
            fuelStack.shrink(1);
            brewingStand.setChanged();
        }

        boolean canBrew = brewingStand.canBrew();
        boolean brewing = brewingStand.brewTime > 0;
        ItemStack ing = brewingStand.getPriorityIngredient().getRight();
        if (brewing) {
            --brewingStand.brewTime;
            boolean done = brewingStand.brewTime == 0;
            if (done && canBrew) {
                brewingStand.brewPotions();
                brewingStand.setChanged();
            } else if (!canBrew) {
                brewingStand.brewTime = 0;
                brewingStand.setChanged();
            } else if (brewingStand.ingredientID != ing.getItem()) {
                brewingStand.brewTime = 0;
                brewingStand.setChanged();
            }
        } else if (canBrew && brewingStand.fuel > 0) {
            --brewingStand.fuel;
            brewingStand.brewTime = TIME;
            brewingStand.ingredientID = ing.getItem();
            brewingStand.setChanged();
        }

        if (!brewingStand.level.isClientSide) {
            brewingStand.setBottleBlockStates();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new CAdvancedBrewingStandMenu(id, playerInventory, handler, this.data,this);
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

    /**
     * Creates an array of boolean values, each value represents a potion input slot, value is true if the slot is not
     * null.
     */
    public boolean[] createFilledSlotsArray() {
        boolean[] aboolean = new boolean[3];

        for(int i = 0; i < 3; ++i) {
            if (!this.handler.$getStackInSlot(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    protected void setBottleBlockStates() {
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

    protected abstract boolean canBrew();
    protected void brewPotions() {
        //plays brewing stand block brewing finished sound
        this.level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, worldPosition, 0);
    }

    //searches 7 => 3
    public Pair<Integer,ItemStack> getPriorityIngredient() {
        for (int i = 7; i > 2;i--) {
            ItemStack ing = handler.$getStackInSlot(i);
            if (!ing.isEmpty() && isThereARecipe(ing)) {
                return Pair.of(i,ing);
            }
        }
        return Pair.of(-1,ItemStack.EMPTY);
    }

    public boolean isThereARecipe(ItemStack ingredient) {
        for (int i : POTIONS) {
            ItemStack potion = handler.$getStackInSlot(i);
            if (PotionBrewing.hasMix(potion,ingredient))
                return true;
        }
        return false;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.brewTime = nbt.getShort("BrewTime");
        this.fuel = nbt.getInt("Fuel");
        xp = nbt.getInt("xp");
        ContainerHelper.loadAllItems(nbt,handler.$getStacks());
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putShort("BrewTime", (short)this.brewTime);
        compound.putInt("Fuel", this.fuel);
        compound.putInt("xp",xp);
        ContainerHelper.saveAllItems(compound,handler.$getStacks());
    }
}
