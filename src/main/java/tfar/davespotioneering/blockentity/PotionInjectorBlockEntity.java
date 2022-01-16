package tfar.davespotioneering.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;
import tfar.davespotioneering.block.GauntletWorkstationBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import javax.annotation.Nullable;

public class PotionInjectorBlockEntity extends TileEntity implements INamedContainerProvider {

    public ItemStackHandler handler = new PotionInjectorHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == PotionInjectorHandler.GAUNTLET) {
                ItemStack stack = getStackInSlot(slot);
                GauntletWorkstationBlock.setHasGauntlet(world,pos,getBlockState(),!stack.isEmpty());
            }
        }
    };

    public PotionInjectorBlockEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public PotionInjectorBlockEntity() {
        this(ModBlockEntityTypes.POTION_INJECTOR);
    }

    @Override
    public ITextComponent getDisplayName() {
        return GauntletWorkstationBlock.CONTAINER_NAME;
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new PotionInjectorMenu(p_createMenu_1_,p_createMenu_2_,handler);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("inv",handler.serializeNBT());
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        handler.deserializeNBT(nbt.getCompound("inv"));
        super.read(state, nbt);
    }
}
