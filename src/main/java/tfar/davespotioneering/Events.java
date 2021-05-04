package tfar.davespotioneering;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class Events {

    public static void potionCooldown(PlayerInteractEvent.RightClickItem e) {
        ItemStack stack = e.getItemStack();
        PlayerEntity player = e.getPlayer();
        if (!player.world.isRemote && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldownTracker().setCooldown(stack.getItem(),20);
        }
    }
}
