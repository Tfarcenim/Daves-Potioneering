package tfar.davespotioneering.inv;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tfar.davespotioneering.inventory.BasicInventoryBridge;

public class BridgedSimpleContainer extends SimpleContainer implements BasicInventoryBridge {

    public BridgedSimpleContainer(int size) {
        super(size);
    }

    @Override
    public ItemStack $getStackInSlot(int slot) {
        return getItem(slot);
    }

    @Override
    public NonNullList<ItemStack> $getStacks() {
        return items;
    }

    @Override
    public int $getSlots() {
        return getContainerSize();
    }

    @Override
    public void $setStackInSlot(int slot, ItemStack stack) {
        setItem(slot,stack);
    }

    @Override
    public int $getSlotLimit(int slot) {
        return getSlotLimit(slot);
    }

    @Override
    public ItemStack $insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

     //   validateSlotIndex(slot);

        ItemStack existing = this.$getStacks().get(slot);

        int limit = getStackLimit(slot, stack);

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
                this.$getStacks().set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
          //  onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    protected boolean isItemValid(int slot,ItemStack stack) {
        return canPlaceItem(slot,stack);
    }

    @Override
    public ItemStack $extractItem(int slot, int amount, boolean simulate) {
        {
            if (amount == 0)
                return ItemStack.EMPTY;

            //validateSlotIndex(slot);

            ItemStack existing = this.$getStacks().get(slot);

            if (existing.isEmpty())
                return ItemStack.EMPTY;

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.getCount() <= toExtract) {
                if (!simulate) {
                    this.$getStacks().set(slot, ItemStack.EMPTY);
                    //  onContentsChanged(slot);
                    return existing;
                } else {
                    return existing.copy();
                }
            } else {
                if (!simulate) {
                    this.$getStacks().set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    //    onContentsChanged(slot);
                }

                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }
    }

    public int getSlotLimit(int slot) {
        return getMaxStackSize();
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public CompoundTag $save() {
        return ContainerHelper.saveAllItems(new CompoundTag(),$getStacks(),true);
    }

    @Override
    public void $load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag,$getStacks());
    }
}
