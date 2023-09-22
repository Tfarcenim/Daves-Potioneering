package tfar.davespotioneering.item;

import tfar.davespotioneering.init.ModSoundEvents;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public class UmbrellaItem extends ShieldItem {
    private final String style;

    public UmbrellaItem(Properties builder, String style) {
        super(builder);
        this.style = style;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        world.playLocalSound(playerEntity.getX(),playerEntity.getY(),playerEntity.getZ(), ModSoundEvents.UMBRELLA_OPEN, SoundSource.BLOCKS,.5f,1,false);
        return super.use(world, playerEntity, hand);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level world, LivingEntity livingEntity, int i) {
        world.playLocalSound(livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(), ModSoundEvents.UMBRELLA_CLOSE, SoundSource.BLOCKS,.5f,1,false);
        super.releaseUsing(itemStack, world, livingEntity, i);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal(style));
        tooltip.add(Component.translatable(getDescriptionId()+".desc"));
    }
}
