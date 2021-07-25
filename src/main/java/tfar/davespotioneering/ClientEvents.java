package tfar.davespotioneering;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ClientEvents {

    public static void playSound(PlaySoundEvent event) {
        if (event.getName().equals(SoundEvents.BLOCK_BREWING_STAND_BREW.getName().getPath()) && !ModConfig.Client.play_block_brewing_stand_brew.get()) {
            event.setResultSound(null);
        }
    }

    public static void tooltips(ItemTooltipEvent e) {
        ItemStack stack = e.getItemStack();
        if (stack.getItem() instanceof PotionItem) {
            if (Util.isMilkified(stack)) {
                e.getToolTip().add(new StringTextComponent("Milkified"));
            }
        }
    }
}
