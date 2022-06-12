package tfar.davespotioneering.inv;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.SimpleContainerAccess;

import javax.annotation.Nonnull;

public class PotionInjectorHandler extends SimpleInventory {

    public static final int GAUNTLET = 6;
    public static final int BLAZE = 7;

    public PotionInjectorHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean isValid(int slot, @Nonnull ItemStack stack) {
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
        return super.isValid(slot, stack);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isValid(slot, stack))
            return stack;

        ItemStack existing = getStack(slot);

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
                this.setStack(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.increment(reachedLimit ? limit : stack.getCount());
            }
           // onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    protected int getStackLimit(@Nonnull ItemStack stack)
    {
        return Math.min(getMaxCountPerStack(), stack.getMaxCount());
    }

    //needed to prevent markDirty updates when loading from save
    public void readTags(NbtList tags) {
        for (int i = 0; i < tags.size(); i++)
        {
            NbtCompound itemTags = tags.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < ((SimpleContainerAccess)this).getStacks().size())
            {
                ((SimpleContainerAccess)this).getStacks().set(i,ItemStack.fromNbt(itemTags));
            }
        }
    }

    public NbtList getTags() {
        NbtList nbtTagList = new NbtList();
        for (int i = 0; i < this.size(); i++)
        {
            if (!((SimpleContainerAccess)this).getStacks().get(i).isEmpty())
            {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                ((SimpleContainerAccess)this).getStacks().get(i).writeNbt(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        return nbtTagList;
    }
}
