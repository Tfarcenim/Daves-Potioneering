package tfar.davespotioneering.blockentity;

import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PotionInjectorBlockEntity extends BlockEntity implements MenuProvider {

    public PotionInjectorHandler handler = new PotionInjectorHandler(8) {
        @Override
        public void setChanged() {
            super.setChanged();
            PotionInjectorBlock.setHasGauntlet(level,worldPosition,getBlockState(),!this.getItem(GAUNTLET).isEmpty());
        }
    };

    public PotionInjectorBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn,blockPos,blockState);
    }

    public PotionInjectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityTypes.POTION_INJECTOR,blockPos,blockState);
    }

    @Override
    public Component getDisplayName() {
        return PotionInjectorBlock.CONTAINER_NAME;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return new PotionInjectorMenu(p_createMenu_1_,p_createMenu_2_,handler);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        ContainerHelper.saveAllItems(compound,handler.items);
    }

    @Override
    public void load(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt,handler.items);
    }
}
