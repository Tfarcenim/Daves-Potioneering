package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.block.CPotionInjectorBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;

public class PotionInjectorBlockEntity extends CPotionInjectorBlockEntity{
    public PotionInjectorBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos p_155283_, BlockState p_155284_) {
        super(tileEntityTypeIn, p_155283_, p_155284_);
        handler = new PotionInjectorHandler(8) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                if (slot == PotionInjectorHandler.GAUNTLET) {
                    ItemStack stack = getStackInSlot(slot);
                    CPotionInjectorBlock.setHasGauntlet(level,worldPosition,getBlockState(),!stack.isEmpty());
                }
            }
        };
    }

    public PotionInjectorBlockEntity(BlockPos p_155283_, BlockState p_155284_) {
        this(ModBlockEntityTypes.POTION_INJECTOR,p_155283_, p_155284_);
    }
}
