package tfar.davespotioneering.blockentity;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
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
import tfar.davespotioneering.PotionUtils2;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReinforcedCauldronBlockEntity extends BlockEntity {

    @Nonnull protected Potion potion = Potions.EMPTY;
    protected List<MobEffectInstance> customEffects = new ArrayList<>();
    @Nullable Integer customPotionColor;

    public ReinforcedCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityTypes.REINFORCED_CAULDRON,blockPos,blockState);
    }

    public ReinforcedCauldronBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn,blockPos,blockState);
    }

    public List<MobEffectInstance> getCustomEffects() {
        return customEffects;
    }

    public void setCustomEffects(List<MobEffectInstance> customEffects) {
        this.customEffects = customEffects;
    }

    @Nonnull
    public Potion getPotion() {
        return potion;
    }

    public void setPotion(@Nonnull Potion potion) {
        this.potion = potion;
    }

    public int getColor() {
        if (potion == Potions.WATER) {
            return BiomeColors.getAverageWaterColor(level, worldPosition);
        } else {
            return Objects.requireNonNullElseGet(customPotionColor, () -> PotionUtils.getColor(potion));
        }
    }

    @Nullable
    public Integer getCustomPotionColor() {
        return customPotionColor;
    }

    public void setCustomPotionColor(@Nullable Integer customPotionColor) {
        this.customPotionColor = customPotionColor;
    }

    @Override
    public void load(CompoundTag nbt) {
        potion = PotionUtils.getPotion(nbt);
        customEffects = PotionUtils2.getCustomEffects(nbt);
        if (nbt.contains(PotionUtils.TAG_CUSTOM_POTION_COLOR)) {
            customPotionColor = nbt.getInt(PotionUtils.TAG_CUSTOM_POTION_COLOR);
        }
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        PotionUtils2.saveAllEffects(compound,potion,customEffects, customPotionColor);
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
            int wLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY && !(stack.getItem() instanceof PotionItem)) {
                LayeredReinforcedCauldronBlock.removeCoating(blockState,level,worldPosition,null,stack);
            } else if (stack.getItem() == Items.ARROW && wLevel > 0) {
                if (dragon)
                    LayeredReinforcedCauldronBlock.handleArrowCoating(blockState,level,worldPosition,null,stack);
            }

            else if (coatingType == Util.CoatingType.FOOD && wLevel > 0) {
                if (DavesPotioneering.CONFIG.spike_food && stack.getCount() >= 8) {
                    LayeredReinforcedCauldronBlock.handleFoodSpiking(blockState,level,worldPosition,null,null,stack);
                }
            }

            else if (wLevel == 3 && dragon) {

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
        this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), LayeredReinforcedCauldronBlock.brew_speed);
    }
}
