package tfar.davespotioneering.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TooltipItem extends Item {

    public TooltipItem(Settings properties) {
        super(properties);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {

        tooltip.add(Text.translatable(getTranslationKey()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
        tooltip.add(this.getShiftDescription().formatted(Formatting.GRAY));

        tooltip.add(Text.translatable(getTranslationKey()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().formatted(Formatting.GRAY));
    }

    public MutableText getCtrlDescription() {
        return Text.translatable(this.getTranslationKey() + ".ctrl.desc");
    }

    public MutableText getShiftDescription() {
        return Text.translatable(this.getTranslationKey() + ".shift.desc");
    }
}
