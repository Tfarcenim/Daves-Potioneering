package tfar.davespotioneering.blockentity;

import tfar.davespotioneering.block.CPotionInjectorBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.PotionInjectorHandlerFabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PotionInjectorBlockEntity extends CPotionInjectorBlockEntity {

    public PotionInjectorBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn,blockPos,blockState);
        handler = new PotionInjectorHandlerFabric(8) {
            @Override
            public void setChanged() {
                super.setChanged();
                CPotionInjectorBlock.setHasGauntlet(level,worldPosition,getBlockState(),!this.getItem(GAUNTLET).isEmpty());
            }
        };
    }

    public PotionInjectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityTypes.POTION_INJECTOR,blockPos,blockState);
    }
}
