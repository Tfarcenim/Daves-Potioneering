package tfar.davespotioneering.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.Events;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

import javax.annotation.Nullable;
import java.util.Arrays;

public class AdvancedBrewingStandBlockEntity extends BlockEntity implements Tickable, NamedScreenHandlerFactory, BrewingStandDuck {
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
    protected final PropertyDelegate data = new PropertyDelegate() {
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
        super(ModBlockEntityTypes.COMPOUND_BREWING_STAND);
    }

    protected AdvancedBrewingStandBlockEntity(BlockEntityType<?> typeIn) {
        super(typeIn);
    }

    protected Text getDefaultName() {
        return new TranslatableText("container.davespotioneering.compound_brewing");
    }

    public void tick() {
        ItemStack fuelStack = this.brewingHandler.getStack(FUEL);
        if (this.fuel <= 0 && fuelStack.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            fuelStack.decrement(1);
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

        if (!this.world.isClient) {
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

            for(int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; ++i) {
                blockstate = blockstate.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], aboolean[i]);
            }

            this.world.setBlockState(this.pos, blockstate, 2);
        }
    }

    //searches 7 => 3
    public Pair<Integer,ItemStack> getPriorityIngredient() {
        for (int i = 7; i > 2;i--) {
            ItemStack stack = brewingHandler.getStack(i);
            if (!stack.isEmpty() && isThereARecipe(stack)) {
                return Pair.of(i,stack);
            }
        }
        return Pair.of(-1,ItemStack.EMPTY);
    }


    public boolean isThereARecipe(ItemStack ingredient) {

        if (!ingredient.isEmpty()) {
            return BrewingRecipeRegistry.hasRecipe(ingredient,ingredient);
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
            if (!this.brewingHandler.getStack(i).isEmpty()) {
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

            return BrewingRecipeRegistry.hasRecipe(itemstack, itemstack);
        }
        return false;
    }

    private void brewPotions() {
        Pair<Integer,ItemStack> pair = getPriorityIngredient();
        ItemStack ingredient = pair.getRight();

        boolean canMilkify = ingredient.getItem() == Items.MILK_BUCKET;

        //note: this is changed from the BrewingRecipeRegistry version to allow for >1 potion in a stack
        Util.brewPotions(brewingHandler.getItems(), ingredient, POTIONS);
        Events.potionBrew(this,ingredient);

        if (canMilkify) {
            for (int i = 0; i < POTIONS.length; i++) {
                ItemStack potion = brewingHandler.getStack(i);
                Util.milkifyPotion(potion);
            }
        }

        BlockPos blockpos = this.getPos();
        if (ingredient.getItem().hasRecipeRemainder()) {
            ItemStack ingredientContainerItem = ingredient.getItem().getRecipeRemainder().getDefaultStack();
            ingredient.decrement(1);
            if (ingredient.isEmpty()) {
                ingredient = ingredientContainerItem;
            } else if (!this.world.isClient) {
                ItemScatterer.spawn(this.world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), ingredientContainerItem);
            }
        }
        //todo
        else ingredient.decrement(1);

        this.brewingHandler.setStack(pair.getLeft(), ingredient);
        //plays brewing stand block brewing finished sound
        this.world.syncWorldEvent(1035, blockpos, 0);
    }

    private boolean canMilkify() {
        for (int i : POTIONS) {
            ItemStack potionStack = brewingHandler.getStack(i);
            if (potionStack.getItem() instanceof PotionItem) {
                Potion potion = PotionUtil.getPotion(potionStack);
                String name = Registry.POTION.getId(potion).toString();
                if (name.contains("long") || name.contains("strong")) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag nbt) {
        super.fromTag(state, nbt);
        ListTag items = nbt.getList("Items",10);
        brewingHandler.readTags(items);
        this.brewTime = nbt.getShort("BrewTime");
        this.fuel = nbt.getInt("Fuel");
        xp = nbt.getInt("xp");
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        super.toTag(compound);
        compound.putShort("BrewTime", (short)this.brewTime);
        compound.put("Items",brewingHandler.getTags());
        compound.putInt("Fuel", this.fuel);
        compound.putInt("xp",xp);
        return compound;
    }

    @Override
    public Text getDisplayName() {
        return getDefaultName();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new AdvancedBrewingStandContainer(id, playerInventory, brewingHandler, this.data,this);
    }

    @Override
    public void addXp(double xp) {
        this.xp += xp;
    }

    @Override
    public void dump(PlayerEntity player) {
        if(xp > 0) {
            Util.splitAndSpawnExperience(world, player.getPos(), xp);
            xp = 0;
            markDirty();
        }
    }
}
