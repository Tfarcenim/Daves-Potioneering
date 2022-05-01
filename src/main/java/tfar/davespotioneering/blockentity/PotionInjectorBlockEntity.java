package tfar.davespotioneering.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import javax.annotation.Nullable;

public class PotionInjectorBlockEntity extends BlockEntity implements MenuProvider {

    public SimpleContainer handler = new PotionInjectorHandler(8) {
        @Override
        public void setChanged() {
            super.setChanged();
            PotionInjectorBlock.setHasGauntlet(level,worldPosition,getBlockState(),!this.getItem(GAUNTLET).isEmpty());
        }
    };

    public PotionInjectorBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public PotionInjectorBlockEntity() {
        this(ModBlockEntityTypes.POTION_INJECTOR);
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
    public CompoundTag save(CompoundTag compound) {
        compound.put("inv",handler.createTag());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundTag nbt) {
        handler.fromTag(nbt.getList("inv",10));
        super.load(state, nbt);
    }
}
