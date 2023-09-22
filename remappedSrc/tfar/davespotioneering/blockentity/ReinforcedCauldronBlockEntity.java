package tfar.davespotioneering.blockentity;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;

public class ReinforcedCauldronBlockEntity extends BlockEntity {

    @Nonnull protected Potion potion = Potions.EMPTY;

    public ReinforcedCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityTypes.REINFORCED_CAULDRON,blockPos,blockState);
    }

    public ReinforcedCauldronBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn,blockPos,blockState);
    }

    @Nonnull
    public Potion getPotion() {
        return potion;
    }

    public void setPotion(@Nonnull Potion potion) {
        this.potion = potion;
    }

    public int getColor() {
        if (!potion.getEffects().isEmpty()) {
            return PotionUtils.getColor(potion);
        }
        return BiomeColors.getAverageWaterColor(level, worldPosition);
    }

    @Override
    public void load(CompoundTag nbt) {
        potion = Registry.POTION.get(new ResourceLocation(nbt.getString("potion")));
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putString("potion", Registry.POTION.getId(potion).toString());
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }


    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //@Override
    //public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
    //    this.load(null, packet.getTag());
    //    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    //}

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            boolean dragon = getBlockState().getValue(LayeredReinforcedCauldronBlock.DRAGONS_BREATH);
            ItemStack stack =  ((ItemEntity) entity).getItem();
            Util.CoatingType coatingType = Util.CoatingType.getCoatingType(stack);

            BlockState blockState = getBlockState();
            int level = blockState.getValue(LayeredCauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY && !(stack.getItem() instanceof PotionItem)) {
                LayeredReinforcedCauldronBlock.removeCoating(blockState,level,worldPosition,null,stack);
            } else if (stack.getItem() == Items.ARROW && level > 0) {
                if (dragon)
                    LayeredReinforcedCauldronBlock.handleArrowCoating(blockState,level,worldPosition,null,stack);
            }

            else if (coatingType == Util.CoatingType.FOOD && level > 0) {
                if (DavesPotioneering.CONFIG.spike_food && stack.getCount() >= 8) {
                    LayeredReinforcedCauldronBlock.handleFoodSpiking(blockState,level,worldPosition,null,null,stack);
                }
            }

            else if (level == 3 && dragon) {

                if (coatingType == Util.CoatingType.TOOL && !DavesPotioneering.CONFIG.coat_tools) return;//check if tools can be coated

                if (coatingType == Util.CoatingType.ANY && !DavesPotioneering.CONFIG.coat_anything) return;
                //check if anything can be coated AND the item is not in a whitelist


                //burn off a layer, then schedule the rest of the ticks
                level.playSound(null,worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);
                LayeredCauldronBlock.lowerFillLevel(blockState, level, worldPosition);
                scheduleTick();
            }
        }
    }

    private void scheduleTick() {
        this.level.createAndScheduleBlockTick(this.getBlockPos(), this.getBlockState().getBlock(), LayeredReinforcedCauldronBlock.brew_speed);
    }

    //@Override
    public void fromClientTag(CompoundTag tag) {
        potion = Registry.POTION.get(new ResourceLocation(tag.getString("potion")));
    }

   // @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putString("potion", Registry.POTION.getId(potion).toString());
        return tag;
    }
}
