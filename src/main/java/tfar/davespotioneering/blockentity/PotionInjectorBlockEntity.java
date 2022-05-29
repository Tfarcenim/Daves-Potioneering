package tfar.davespotioneering.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import javax.annotation.Nullable;

public class PotionInjectorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    public PotionInjectorHandler handler = new PotionInjectorHandler(8) {
        @Override
        public void markDirty() {
            super.markDirty();
            PotionInjectorBlock.setHasGauntlet(world,pos,getCachedState(),!this.getStack(GAUNTLET).isEmpty());
        }
    };

    public PotionInjectorBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public PotionInjectorBlockEntity() {
        this(ModBlockEntityTypes.POTION_INJECTOR);
    }

    @Override
    public Text getDisplayName() {
        return PotionInjectorBlock.CONTAINER_NAME;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new PotionInjectorMenu(p_createMenu_1_,p_createMenu_2_,handler);
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        compound.put("inv",handler.getTags());
        return super.toTag(compound);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag nbt) {
        handler.readTags(nbt.getList("inv",10));
        super.fromTag(state, nbt);
    }
}
