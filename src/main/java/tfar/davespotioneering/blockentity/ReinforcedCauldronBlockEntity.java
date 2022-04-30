package tfar.davespotioneering.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.BiomeColors;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;

public class ReinforcedCauldronBlockEntity extends TileEntity {

    @Nonnull protected Potion potion = Potions.EMPTY;

    public ReinforcedCauldronBlockEntity() {
        this(ModBlockEntityTypes.REINFORCED_CAULDRON);
    }

    public ReinforcedCauldronBlockEntity(TileEntityType<?> tileEntityTypeIn) {
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
    public void load(BlockState state, CompoundNBT nbt) {
        potion = Registry.POTION.get(new ResourceLocation(nbt.getString("potion")));
        super.load(state, nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putString("potion", potion.getRegistryName().toString());
        return super.save(compound);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());    // okay to send entire inventory on chunk load
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.load(null, packet.getTag());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            ItemStack stack =  ((ItemEntity) entity).getItem();
            BlockState blockState = getBlockState();
            int level = blockState.getValue(CauldronBlock.LEVEL);
            if (potion == ModPotions.MILK && PotionUtils.getPotion(stack) != Potions.EMPTY) {
                ReinforcedCauldronBlock.removeCoating(blockState,level,worldPosition,null,stack);
            } else if (stack.getItem() == Items.ARROW && level > 0) {
              ReinforcedCauldronBlock.handleArrowCoating(blockState,level,worldPosition,null,stack,level);
            } else if (level == 3) {
                //burn off a layer, then schedule the rest of the ticks
                level.playSound(null,worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1);
                ((CauldronBlock)blockState.getBlock()).setWaterLevel(level,worldPosition,blockState,2);
                scheduleTick();
            }
        }
    }

    private void scheduleTick() {
        this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), ReinforcedCauldronBlock.brew_speed);
    }
}
