package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.DavesPotioneeringFabric;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModPotions;

public class ReinforcedCauldronBlockEntity extends CReinforcedCauldronBlockEntity {

    public ReinforcedCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityTypes.REINFORCED_CAULDRON,blockPos,blockState);
    }

    public ReinforcedCauldronBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn,blockPos,blockState);
    }

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            boolean dragon = getBlockState().getValue(LayeredReinforcedCauldronBlock.DRAGONS_BREATH);
            ItemStack stack =  ((ItemEntity) entity).getItem();
            Util.CoatingType coatingType = Util.CoatingType.getCoatingType(stack);

            BlockState blockState = getBlockState();
            int wLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY && !(stack.getItem() instanceof PotionItem)) {
                LayeredReinforcedCauldronBlock.removeCoating(blockState,level,worldPosition,null,stack);
            } else if (stack.getItem() == Items.ARROW && wLevel > 0) {
                if (dragon)
                    LayeredReinforcedCauldronBlock.handleArrowCoating(blockState,level,worldPosition,null,stack);
            }

            else if (coatingType == Util.CoatingType.FOOD && wLevel > 0) {
                if (DavesPotioneeringFabric.CONFIG.spike_food && stack.getCount() >= 8) {
                    LayeredReinforcedCauldronBlock.handleFoodSpiking(blockState,level,worldPosition,null,null,stack);
                }
            }

            else if (wLevel == 3 && dragon) {

                if (coatingType == Util.CoatingType.TOOL && !DavesPotioneeringFabric.CONFIG.coat_tools) return;//check if tools can be coated

                if (coatingType == Util.CoatingType.ANY && !DavesPotioneeringFabric.CONFIG.coat_anything) return;
                //check if anything can be coated AND the item is not in a whitelist


                //burn off a layer, then schedule the rest of the ticks
                level.playSound(null,worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);
                LayeredCauldronBlock.lowerFillLevel(blockState, level, worldPosition);
                scheduleTick();
            }
        }
    }

    private void scheduleTick() {
        this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), LayeredReinforcedCauldronBlock.brew_speed);
    }
}
