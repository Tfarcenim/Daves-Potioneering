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
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;

public class ReinforcedCauldronBlockEntity extends BlockEntity {

    @Nonnull protected Potion potion = Potions.EMPTY;

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

    public void setPotion(@Nonnull Potion potion) {
        this.potion = potion;
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
        potion = Registry.POTION.get(new ResourceLocation(nbt.getString("potion")));
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putString("potion", potion.getRegistryName().toString());
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
        if (entity instanceof ItemEntity && getBlockState().hasProperty(LayeredReinforcedCauldronBlock.DRAGONS_BREATH)) {
            ItemStack stack =  ((ItemEntity) entity).getItem();
            BlockState blockState = getBlockState();
            int waterLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY) {
                LayeredReinforcedCauldronBlock.removeCoating(blockState,level,worldPosition,null,stack);
            } else if (stack.getItem() == Items.ARROW && waterLevel > 0) {
              LayeredReinforcedCauldronBlock.handleArrowCoating(blockState,level,worldPosition,null,null,stack);
            } else if (waterLevel == 3) {
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
