package tfar.davespotioneering.item;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class UmbrellaItem extends ShieldItem {
    private final String style;

    public UmbrellaItem(Properties builder, String style) {
        super(builder);
        this.style = style;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TextComponent(style));

        tooltip.add(new TranslatableComponent(getDescriptionId()+".desc"));
    }
}
