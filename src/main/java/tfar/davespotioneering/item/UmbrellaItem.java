package tfar.davespotioneering.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class UmbrellaItem extends ShieldItem {
    private final String style;

    public UmbrellaItem(Properties builder, String style) {
        super(builder);
        this.style = style;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(style));

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".desc"));
    }
}
