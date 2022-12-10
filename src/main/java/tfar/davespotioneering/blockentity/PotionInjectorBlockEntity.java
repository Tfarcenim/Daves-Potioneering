package tfar.davespotioneering.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import javax.annotation.Nullable;

public class PotionInjectorBlockEntity extends BlockEntity implements MenuProvider {

    public PotionInjectorHandler handler = new PotionInjectorHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == PotionInjectorHandler.GAUNTLET) {
                ItemStack stack = getStackInSlot(slot);
                PotionInjectorBlock.setHasGauntlet(level,worldPosition,getBlockState(),!stack.isEmpty());
            }
        }
    };

    public PotionInjectorBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos p_155283_, BlockState p_155284_) {
        super(tileEntityTypeIn,p_155283_,p_155284_);
    }

    public PotionInjectorBlockEntity( BlockPos p_155283_, BlockState p_155284_) {
        this(ModBlockEntityTypes.POTION_INJECTOR,p_155283_,p_155284_);
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
        compound.put("inv",handler.serializeNBT());
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag nbt) {
        handler.deserializeNBT(nbt.getCompound("inv"));
        super.load(nbt);
    }
}
