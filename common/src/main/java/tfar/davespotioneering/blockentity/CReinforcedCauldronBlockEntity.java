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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.PotionUtils2;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.block.CLayeredReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.platform.Services;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class CReinforcedCauldronBlockEntity extends BlockEntity {

    @Nonnull
    protected Potion potion = Potions.EMPTY;
    protected List<MobEffectInstance> customEffects = new ArrayList<>();
    @Nullable
    protected Integer customPotionColor;

    public CReinforcedCauldronBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }


    @Nonnull
    public Potion getPotion() {
        return potion;
    }

    public List<MobEffectInstance> getCustomEffects() {
        return customEffects;
    }

    public void setPotion(@Nonnull Potion potion) {
        this.potion = potion;
        setChanged();
    }

    public void setCustomEffects(List<MobEffectInstance> customEffects) {
        this.customEffects = customEffects;
        setChanged();
    }

    @Override
    public void load(CompoundTag nbt) {
        potion = PotionUtils.getPotion(nbt);
        customEffects = PotionUtils.getCustomEffects(nbt);
        if (nbt.contains(PotionUtils.TAG_CUSTOM_POTION_COLOR)) {
            customPotionColor = nbt.getInt(PotionUtils.TAG_CUSTOM_POTION_COLOR);
        }
        super.load(nbt);
    }

    public int getColor() {
        if (potion == Potions.WATER) {
            return BiomeColors.getAverageWaterColor(level, worldPosition);
        } else {
            if (customPotionColor != null) {
                return customPotionColor;
            } else {
                return PotionUtils.getColor(potion);
            }
        }
    }


    @Override
    public void saveAdditional(CompoundTag compound) {
        PotionUtils2.saveAllEffects(compound, potion, customEffects,customPotionColor);
        super.saveAdditional(compound);
    }

    @Nullable
    public Integer getCustomPotionColor() {
        return customPotionColor;
    }

    public void setCustomPotionColor(@Nullable Integer customPotionColor) {
        this.customPotionColor = customPotionColor;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            ItemStack stack = ((ItemEntity) entity).getItem();
            if (stack.is(ModItems.BLACKLISTED)) return;

            boolean dragon = getBlockState().getValue(CLayeredReinforcedCauldronBlock.DRAGONS_BREATH);
            Util.CoatingType coatingType = Util.CoatingType.getCoatingType(stack);

            BlockState blockState = getBlockState();
            int cLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY && !Util.isPotion(stack)) {
                CLayeredReinforcedCauldronBlock.removeCoating(blockState, level, worldPosition, null, stack);
            } else if (coatingType == Util.CoatingType.FOOD) {
                if (Services.PLATFORM.spikeFood() && stack.getCount() >= 8) {//check if food can be coated
                    CLayeredReinforcedCauldronBlock.handleFoodSpiking(blockState, level, worldPosition, null, null, stack);
                }
            } else if (stack.getItem() == Items.ARROW && cLevel > 0) {
                if (dragon)
                    CLayeredReinforcedCauldronBlock.handleArrowCoating(blockState, level, worldPosition, null, stack);
            } else if (cLevel == 3 && dragon) {

                //burn off a layer, then schedule the rest of the ticks
                entity.level().playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);
                CLayeredReinforcedCauldronBlock.setWaterLevel(level, worldPosition, blockState, 2);
                scheduleTick();
            }
        }
    }


    private void scheduleTick() {
        this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), CLayeredReinforcedCauldronBlock.brew_speed);
    }

}
