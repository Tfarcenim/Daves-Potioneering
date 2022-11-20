package tfar.davespotioneering.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
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
            return PotionUtil.getColor(potion);
        }
        return BiomeColors.getWaterColor(world, pos);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        potion = Registry.POTION.get(new Identifier(nbt.getString("potion")));
        super.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        compound.putString("potion", Registry.POTION.getId(potion).toString());
    }

    @Nonnull
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }


    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    //@Override
    //public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
    //    this.load(null, packet.getTag());
    //    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    //}

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity itemEntity && getCachedState().get(LayeredReinforcedCauldronBlock.DRAGONS_BREATH)) {
            ItemStack stack =  itemEntity.getStack();
            BlockState blockState = getCachedState();
            int fluidLevel = blockState.get(LeveledCauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtil.getPotion(stack) != Potions.EMPTY) {
                LayeredReinforcedCauldronBlock.removeCoating(blockState,this.world,pos,null,stack);
            } else if (stack.getItem() == Items.ARROW && fluidLevel > 0) {
              LayeredReinforcedCauldronBlock.handleArrowCoating(blockState,this.world,pos,null, stack);
            } else if (fluidLevel == 3) {
                //burn off a layer, then schedule the rest of the ticks
                this.world.playSound(null,pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1);
                LayeredReinforcedCauldronBlock.lowerFillLevel0(blockState, this.world, pos);
                stack.getOrCreateNbt().putInt(LayeredReinforcedCauldronBlock.LAYERS,1);
                scheduleTick();
            }
        }
    }

    private void scheduleTick() {
        this.world.createAndScheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), LayeredReinforcedCauldronBlock.brew_speed);
    }

    //@Override
    public void fromClientTag(NbtCompound tag) {
        potion = Registry.POTION.get(new Identifier(tag.getString("potion")));
    }

   // @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        tag.putString("potion", Registry.POTION.getId(potion).toString());
        return tag;
    }
}
