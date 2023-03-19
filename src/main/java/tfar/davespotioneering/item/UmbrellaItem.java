package tfar.davespotioneering.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import tfar.davespotioneering.init.ModSoundEvents;

import javax.annotation.Nullable;
import java.util.List;

public class UmbrellaItem extends ShieldItem {
    private final String style;

    public UmbrellaItem(Settings builder, String style) {
        super(builder);
        this.style = style;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        world.playSound(playerEntity.getX(),playerEntity.getY(),playerEntity.getZ(), ModSoundEvents.UMBRELLA_OPEN, SoundCategory.BLOCKS,.5f,1,false);
        return super.use(world, playerEntity, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
        world.playSound(livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(), ModSoundEvents.UMBRELLA_CLOSE, SoundCategory.BLOCKS,.5f,1,false);
        super.onStoppedUsing(itemStack, world, livingEntity, i);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        tooltip.add(Text.literal(style));
        tooltip.add(Text.translatable(getTranslationKey()+".desc"));
    }
}
