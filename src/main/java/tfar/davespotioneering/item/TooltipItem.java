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

import net.minecraft.item.Item.Properties;

public class TooltipItem extends Item {

    public TooltipItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(new TranslationTextComponent(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
        tooltip.add(this.getShiftDescription().withStyle(TextFormatting.GRAY));

        tooltip.add(new TranslationTextComponent(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().withStyle(TextFormatting.GRAY));
    }

    public IFormattableTextComponent getCtrlDescription() {
        return new TranslationTextComponent(this.getDescriptionId() + ".ctrl.desc");
    }

    public IFormattableTextComponent getShiftDescription() {
        return new TranslationTextComponent(this.getDescriptionId() + ".shift.desc");
    }
}
