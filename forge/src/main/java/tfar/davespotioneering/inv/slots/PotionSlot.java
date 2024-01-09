package tfar.davespotioneering.inv.slots;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.davespotioneering.ForgeUtil;

public class PotionSlot extends SlotItemHandler {
    public PotionSlot(IItemHandler iItemHandler, int index, int x, int y) {
        super(iItemHandler, index, x, y);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack stack) {
        return ForgeUtil.isValidInputCountInsensitive(stack);
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
     * case of armor slots)
     */
    public int getMaxStackSize() {
        return 2;
    }

    public void onTake(Player thePlayer, ItemStack stack) {
        Potion potion = PotionUtils.getPotion(stack);
        if (thePlayer instanceof ServerPlayer) {
            ForgeEventFactory.onPlayerBrewedPotion(thePlayer, stack);
            CriteriaTriggers.BREWED_POTION.trigger((ServerPlayer) thePlayer, potion);
        }

        super.onTake(thePlayer, stack);
    }
}
