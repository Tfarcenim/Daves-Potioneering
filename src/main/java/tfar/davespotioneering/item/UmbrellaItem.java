package tfar.davespotioneering.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class UmbrellaItem extends ShieldItem {
    private final String style;

    public UmbrellaItem(Settings builder, String style) {
        super(builder);
        this.style = style;
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        tooltip.add(Text.literal(style));

        tooltip.add(Text.translatable(getTranslationKey()+".desc"));
    }
}
