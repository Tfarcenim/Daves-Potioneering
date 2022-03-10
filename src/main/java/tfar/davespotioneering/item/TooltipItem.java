package tfar.davespotioneering.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TooltipItem extends Item {

    public TooltipItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
        tooltip.add(this.getShiftDescription().mergeStyle(TextFormatting.GRAY));

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().mergeStyle(TextFormatting.GRAY));
    }

    public IFormattableTextComponent getCtrlDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".ctrl.desc");
    }

    public IFormattableTextComponent getShiftDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".shift.desc");
    }
}
