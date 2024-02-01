package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ForgeUtil;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.inv.SidedItemHandler;

import javax.annotation.Nullable;
import java.util.Map;

public class AdvancedBrewingStandBlockEntity extends CAdvancedBrewingStandBlockEntity {



    public AdvancedBrewingStandBlockEntity(BlockPos p_155283_, BlockState p_155284_) {
        this(ModBlockEntityTypes.COMPOUND_BREWING_STAND,p_155283_,p_155284_);
    }

    protected AdvancedBrewingStandBlockEntity(BlockEntityType<?> typeIn,BlockPos p_155283_, BlockState p_155284_) {
        super(typeIn,p_155283_,p_155284_);
        this.handler = new BrewingHandler(SLOTS);
    }

    public boolean isThereARecipe(ItemStack ingredient) {

        if (!ingredient.isEmpty()) {
            return BrewingRecipeRegistry.canBrew(handler.$getStacks(), ingredient, POTIONS) || ingredient.getItem() == Items.MILK_BUCKET; // divert to VanillaBrewingRegistry
        }
        if (ingredient.isEmpty()) {
            return false;
        } else if (!PotionBrewing.isIngredient(ingredient)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.handler.$getStackInSlot(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasMix(itemstack1, ingredient)) {
                    return true;
                }
            }
            return false;
        }

    }

    protected boolean canBrew() {
        ItemStack itemstack = getPriorityIngredient().getRight();
        if (!itemstack.isEmpty()) {
            return BrewingRecipeRegistry.canBrew(handler.$getStacks(), itemstack, POTIONS); // divert to VanillaBrewingRegistry
        }
        if (itemstack.isEmpty()) {
            return false;
        } else if (!PotionBrewing.isIngredient(itemstack)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.handler.$getStackInSlot(i);
                if (!itemstack1.isEmpty() && PotionBrewing.hasMix(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    protected void brewPotions() {
        if (ForgeEventFactory.onPotionAttemptBrew(handler.$getStacks())) return;
        Pair<Integer,ItemStack> pair = getPriorityIngredient();
        ItemStack ingredient = pair.getRight();

        //note: this is changed from the BrewingRecipeRegistry version to allow for >1 potion in a stack
        ForgeUtil.brewPotions(handler.$getStacks(), ingredient, POTIONS);
        ForgeEventFactory.onPotionBrewed(handler.$getStacks());
        DavesPotioneering.potionBrew(this,ingredient);

        BlockPos blockpos = this.getBlockPos();
        if (ingredient.hasCraftingRemainingItem()) {
            ItemStack ingredientContainerItem = ingredient.getCraftingRemainingItem();
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                ingredient = ingredientContainerItem;
            } else if (!this.level.isClientSide) {
                Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), ingredientContainerItem);
            }
        }
        //todo
        else ingredient.shrink(1);
        this.handler.$setStackInSlot(pair.getLeft(), ingredient);
        super.brewPotions();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        CompoundTag items = nbt.getCompound("Items");
       // handler.deserializeNBT(items);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
      //  compound.put("Items",handler.serializeNBT());
    }


    //forge specific stuff

    Map<Direction,LazyOptional<? extends IItemHandler>> handlers = SidedItemHandler.create(() -> (BrewingHandler) handler);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            return handlers.get(facing).cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (Map.Entry<Direction, LazyOptional<? extends IItemHandler>> entry : handlers.entrySet()) {
            entry.getValue().invalidate();
        }
    }
}
