package tfar.davespotioneering.blovkentity;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.PotionUtils2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CReinforcedCauldronBlockEntity extends BlockEntity {

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



}
