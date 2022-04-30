package tfar.davespotioneering.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class TooltipItem extends Item {

    public TooltipItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
        tooltip.add(this.getShiftDescription().withStyle(ChatFormatting.GRAY));

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().withStyle(ChatFormatting.GRAY));
    }

    public MutableComponent getCtrlDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".ctrl.desc");
    }

    public MutableComponent getShiftDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".shift.desc");
    }
}
