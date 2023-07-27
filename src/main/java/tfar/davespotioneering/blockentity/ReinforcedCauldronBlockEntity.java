package tfar.davespotioneering.blockentity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;

public class ReinforcedCauldronBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    @Nonnull protected Potion potion = Potions.EMPTY;

    public ReinforcedCauldronBlockEntity() {
        this(ModBlockEntityTypes.REINFORCED_CAULDRON);
    }

    public ReinforcedCauldronBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
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
            return PotionUtil.getColor(potion);
        }
        return BiomeColors.getWaterColor(world, pos);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag nbt) {
        potion = Registry.POTION.get(new Identifier(nbt.getString("potion")));
        super.fromTag(state, nbt);
    }

    @Nonnull
    @Override
    public CompoundTag toTag(CompoundTag compound) {
        compound.putString("potion", Registry.POTION.getId(potion).toString());
        return super.toTag(compound);
    }

    @Nonnull
    @Override
    public CompoundTag toInitialChunkDataTag() {
        CompoundTag tag = super.toInitialChunkDataTag();
        return toTag(tag);    // okay to send entire inventory on chunk load
    }

    //Do not use
   // @Override
   // public BlockEntityUpdateS2CPacket toUpdatePacket() {
   //     return new BlockEntityUpdateS2CPacket(getPos(), 1, toInitialChunkDataTag());
   // }

    //@Override
    //public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
    //    this.load(null, packet.getTag());
    //    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    //}

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            boolean dragon = getCachedState().get(ReinforcedCauldronBlock.DRAGONS_BREATH);
            ItemStack stack =  ((ItemEntity) entity).getStack();
            Util.CoatingType coatingType = Util.CoatingType.getCoatingType(stack);

            BlockState blockState = getCachedState();
            int level = blockState.get(CauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtil.getPotion(stack) != Potions.EMPTY && !(stack.getItem() instanceof PotionItem)) {
                ReinforcedCauldronBlock.removeCoating(blockState,world,pos,null,stack);
            } else if (stack.getItem() == Items.ARROW && level > 0) {
                if (dragon)
                    ReinforcedCauldronBlock.handleArrowCoating(blockState,world,pos,null,stack,level);
            }

            else if (coatingType == Util.CoatingType.FOOD && level > 0) {
                if (ClothConfig.spike_food && stack.getCount() >= 8) {
                    ReinforcedCauldronBlock.spikedFood(blockState,world,pos,null,stack,level);
                }
            }

            else if (level == 3 && dragon) {

                if (coatingType == Util.CoatingType.TOOL && !ClothConfig.coat_tools) return;//check if tools can be coated

                if (coatingType == Util.CoatingType.ANY && !ClothConfig.coat_anything) return;
                //check if anything can be coated AND the item is not in a whitelist


                //burn off a layer, then schedule the rest of the ticks
                world.playSound(null,pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1);
                ((CauldronBlock)blockState.getBlock()).setLevel(world,pos,blockState,2);
                scheduleTick();
            }
        }
    }

    private void scheduleTick() {
        this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), ReinforcedCauldronBlock.brew_speed);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        potion = Registry.POTION.get(new Identifier(tag.getString("potion")));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putString("potion", Registry.POTION.getId(potion).toString());
        return tag;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sync();
    }
}
