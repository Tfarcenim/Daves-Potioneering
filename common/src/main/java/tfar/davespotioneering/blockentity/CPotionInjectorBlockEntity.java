package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.block.CPotionInjectorBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.menu.CPotionInjectorMenu;

import javax.annotation.Nullable;

public abstract class CPotionInjectorBlockEntity extends BlockEntity implements MenuProvider {

    public static final int GAUNTLET = 6;
    public static final int BLAZE = 7;

    public BasicInventoryBridge handler;

    public CPotionInjectorBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos p_155283_, BlockState p_155284_) {
        super(tileEntityTypeIn,p_155283_,p_155284_);
    }

    public CPotionInjectorBlockEntity(BlockPos p_155283_, BlockState p_155284_) {
        this(ModBlockEntityTypes.POTION_INJECTOR,p_155283_,p_155284_);
    }

    @Override
    public Component getDisplayName() {
        return CPotionInjectorBlock.CONTAINER_NAME;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return new CPotionInjectorMenu(p_createMenu_1_,p_createMenu_2_,handler);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.put("inv",handler.$save());
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag nbt) {
        handler.$load(nbt.getCompound("inv"));
        super.load(nbt);
    }
}
