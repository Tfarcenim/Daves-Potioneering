package tfar.davespotioneering.inv;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.GauntletItem;

import javax.annotation.Nonnull;

import static tfar.davespotioneering.blockentity.CPotionInjectorBlockEntity.BLAZE;
import static tfar.davespotioneering.blockentity.CPotionInjectorBlockEntity.GAUNTLET;

public class PotionInjectorHandler extends ItemStackHandler implements BasicInventoryBridge {

    public PotionInjectorHandler(int slots) {
        super(slots);
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return switch (slot) {
            case 0, 1, 2, 3, 4, 5 -> stack.getItem() == Items.LINGERING_POTION;
            case GAUNTLET -> stack.getItem() instanceof GauntletItem;
            case BLAZE -> stack.getItem() == Items.BLAZE_POWDER;
            default -> super.isItemValid(slot, stack);
        };
    }

    @Override
    public ItemStack $getStackInSlot(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public void $setStackInSlot(int slot, ItemStack stack) {
        setStackInSlot(slot, stack);
    }

    @Override
    public NonNullList<ItemStack> $getStacks() {
        return getStacks();
    }

    @Override
    public CompoundTag $save() {
        return serializeNBT();
    }

    @Override
    public void $load(CompoundTag tag) {
        deserializeNBT(tag);
    }

    @Override
    public int $getSlots() {
        return getSlots();
    }
}
