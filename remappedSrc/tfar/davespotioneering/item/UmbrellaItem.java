package tfar.davespotioneering.item;

import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import java.util.List;

public class UmbrellaItem extends ShieldItem {
    private final String style;

    public UmbrellaItem(Settings builder, String style) {
        super(builder);
        this.style = style;
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        tooltip.add(new LiteralText(style));

        tooltip.add(new TranslatableText(getTranslationKey()+".desc"));
    }
}
