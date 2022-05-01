package tfar.davespotioneering.blockentity;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;

public class ReinforcedCauldronBlockEntity extends BlockEntity {

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
        setChanged();
    }

    public int getColor() {
        if (!potion.getEffects().isEmpty()) {
            return PotionUtils.getColor(potion);
        }
        return BiomeColors.getAverageWaterColor(level, worldPosition);
    }

    @Override
    public void load(BlockState state, CompoundTag nbt) {
        potion = Registry.POTION.get(new ResourceLocation(nbt.getString("potion")));
        super.load(state, nbt);
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putString("potion", Registry.POTION.getKey(potion).toString());
        return super.save(compound);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());    // okay to send entire inventory on chunk load
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(getBlockPos(), 1, getUpdateTag());
    }

    //@Override
    //public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
    //    this.load(null, packet.getTag());
    //    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    //}

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            ItemStack stack =  ((ItemEntity) entity).getItem();
            BlockState blockState = getBlockState();
            int fluidLevel = blockState.getValue(CauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY) {
                ReinforcedCauldronBlock.removeCoating(blockState,this.level,worldPosition,null,stack);
            } else if (stack.getItem() == Items.ARROW && fluidLevel > 0) {
              ReinforcedCauldronBlock.handleArrowCoating(blockState,this.level,worldPosition,null,stack,fluidLevel);
            } else if (fluidLevel == 3) {
                //burn off a layer, then schedule the rest of the ticks
                this.level.playSound(null,worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);
                ((CauldronBlock)blockState.getBlock()).setWaterLevel(this.level,worldPosition,blockState,2);
                scheduleTick();
            }
        }
    }

    private void scheduleTick() {
        this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), ReinforcedCauldronBlock.brew_speed);
    }
}
