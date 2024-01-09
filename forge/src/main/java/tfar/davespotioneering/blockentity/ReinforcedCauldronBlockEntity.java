package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.init.ModBlockEntityTypes;

public class ReinforcedCauldronBlockEntity extends CReinforcedCauldronBlockEntity {



    public ReinforcedCauldronBlockEntity(BlockPos p_155283_, BlockState p_155284_) {
        this(ModBlockEntityTypes.REINFORCED_CAULDRON, p_155283_, p_155284_);
    }

    public ReinforcedCauldronBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos p_155283_, BlockState p_155284_) {
        super(tileEntityTypeIn, p_155283_, p_155284_);
    }



    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net,packet);
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
}
