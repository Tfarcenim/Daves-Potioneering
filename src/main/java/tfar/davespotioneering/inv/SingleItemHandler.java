package tfar.davespotioneering.inv;

import net.minecraftforge.items.ItemStackHandler;

public class SingleItemHandler extends ItemStackHandler {

    public SingleItemHandler(int slots) {
        super(slots);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
