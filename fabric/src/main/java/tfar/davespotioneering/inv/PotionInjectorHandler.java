package tfar.davespotioneering.inv;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.GauntletItem;

import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PotionInjectorHandler extends SimpleContainer implements BasicInventoryBridge {

    public static final int GAUNTLET = 6;
    public static final int BLAZE = 7;

    public PotionInjectorHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        switch (slot) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return stack.getItem() == Items.LINGERING_POTION;
            case GAUNTLET:
                return stack.getItem() instanceof GauntletItem;
            case BLAZE:
                return stack.getItem() == Items.BLAZE_POWDER;
        }
        return super.canPlaceItem(slot, stack);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!canPlaceItem(slot, stack))
            return stack;

        ItemStack existing = getItem(slot);

        int limit = getStackLimit(stack);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                this.setItem(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
           // onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    protected int getStackLimit(@Nonnull ItemStack stack)
    {
        return Math.min(getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public ItemStack $getStackInSlot(int slot) {
        return getItem(slot);
    }

    @Override
    public void $setStackInSlot(int slot, ItemStack stack) {
        setItem(slot,stack);
    }

    @Override
    public NonNullList<ItemStack> $getStacks() {
        return items;
    }

    @Override
    public CompoundTag $save() {
        return ContainerHelper.saveAllItems(new CompoundTag(),items,true);
    }

    @Override
    public void $load(CompoundTag tag) {
        ContainerHelper.loadAllItems(new CompoundTag(),items);
    }
}
