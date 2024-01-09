package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.FabricEvents;
import tfar.davespotioneering.FabricUtil;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

import javax.annotation.Nullable;

public class AdvancedBrewingStandBlockEntity extends CAdvancedBrewingStandBlockEntity {
    
    public AdvancedBrewingStandBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityTypes.COMPOUND_BREWING_STAND,blockPos,blockState);
    }

    protected AdvancedBrewingStandBlockEntity(BlockEntityType<?> typeIn, BlockPos blockPos, BlockState blockState) {
        super(typeIn,blockPos,blockState);
        handler = new BrewingHandler(SLOTS);
    }

    protected boolean canBrew() {
        ItemStack ing = getPriorityIngredient().getRight();
        if (!ing.isEmpty()) {
            return isThereARecipe(ing);
        }
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new AdvancedBrewingStandContainer(id, playerInventory,(SimpleContainer) handler, this.data,this);
    }

    protected void brewPotions() {
        Pair<Integer,ItemStack> pair = getPriorityIngredient();
        ItemStack ingredient = pair.getRight();

        //note: this is changed from the BrewingRecipeRegistry version to allow for >1 potion in a stack
        FabricUtil.brewPotions(handler.$getStacks(), ingredient, POTIONS);
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

        this.handler.$setStackInSlot(pair.getLeft(), ingredient);
        super.brewPotions();
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
