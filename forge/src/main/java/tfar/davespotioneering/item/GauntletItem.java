package tfar.davespotioneering.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.event.TickEvent;

public class GauntletItem extends CGauntletItem {

    public GauntletItem(Properties properties) {
        super(Tiers.NETHERITE, 4, -2.8f, properties);
    }

    public static void tickCooldowns(TickEvent.PlayerTickEvent event) {
        tickCooldownsCommon(event.player);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}