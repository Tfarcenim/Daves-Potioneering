package tfar.davespotioneering.blockentity;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
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
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReinforcedCauldronBlockEntity extends BlockEntity {

    @Nonnull protected Potion potion = Potions.EMPTY;
    protected List<MobEffectInstance> customEffects = new ArrayList<>();

    public ReinforcedCauldronBlockEntity( BlockPos p_155283_, BlockState p_155284_) {
        this(ModBlockEntityTypes.REINFORCED_CAULDRON,p_155283_,p_155284_);
    }

    public ReinforcedCauldronBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos p_155283_, BlockState p_155284_) {
        super(tileEntityTypeIn,p_155283_,p_155284_);
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

    public int getColor() {
        if (!potion.getEffects().isEmpty()) {
            return PotionUtils.getColor(potion);
        }
        return BiomeColors.getAverageWaterColor(level, worldPosition);
    }

    @Override
    public void load(CompoundTag nbt) {
        potion = PotionUtils.getPotion(nbt);
        customEffects = PotionUtils.getCustomEffects(nbt);
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        Util.saveAllEffects(compound,potion,customEffects);
        super.saveAdditional(compound);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;    // okay to send entire inventory on chunk load
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        this.load(packet.getTag());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            ItemStack stack = ((ItemEntity) entity).getItem();
            if (stack.is(ModItems.BLACKLISTED)) return;

            boolean dragon = getBlockState().getValue(LayeredReinforcedCauldronBlock.DRAGONS_BREATH);
            Util.CoatingType coatingType = Util.CoatingType.getCoatingType(stack);

            BlockState blockState = getBlockState();
            int cLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY && !Util.isPotion(stack)) {
                LayeredReinforcedCauldronBlock.removeCoating(blockState,level,worldPosition,null,stack);
            } else if (coatingType == Util.CoatingType.FOOD) {
                if (ModConfig.Server.spike_food.get() && stack.getCount()>=8) {//check if food can be coated
                    LayeredReinforcedCauldronBlock.handleFoodSpiking(blockState,level,worldPosition,null,null,stack);
                }
            } else if (stack.getItem() == Items.ARROW && cLevel > 0) {
                if (dragon)
                    LayeredReinforcedCauldronBlock.handleArrowCoating(blockState,level,worldPosition,null,null,stack);
            } else if (cLevel == 3 && dragon) {
                if (coatingType == Util.CoatingType.TOOL && !ModConfig.Server.coat_tools.get()) return;//check if tools can be coated


                if (coatingType == Util.CoatingType.ANY && !ModConfig.Server.coat_all.get() && !stack.is(ModItems.WHITELISTED)) return;
                //check if anything can be coated AND the item is not in a whitelist

                //burn off a layer, then schedule the rest of the ticks
                entity.level.playSound(null,worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);
                LayeredReinforcedCauldronBlock.setWaterLevel(level,worldPosition,blockState,2);
                scheduleTick();
            }
        }
    }

    private void scheduleTick() {
        this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), LayeredReinforcedCauldronBlock.brew_speed);
    }
}
