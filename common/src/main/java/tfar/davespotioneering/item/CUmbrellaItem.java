package tfar.davespotioneering.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class CUmbrellaItem extends ShieldItem {

    protected final String name;
    protected final String style;

    public CUmbrellaItem(Properties builder, DyeColor name, String style) {
        this(builder,name.getName().toLowerCase(Locale.ROOT),style);
    }

    public CUmbrellaItem(Properties builder, String name, String style) {
        super(builder);
        this.name = name;
        this.style = style;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal(style));

        tooltip.add(Component.translatable(getDescriptionId()+".desc"));
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pLevel.playLocalSound(pPlayer.getX(),pPlayer.getY(),pPlayer.getZ(), ModSoundEvents.UMBRELLA_OPEN, SoundSource.BLOCKS,.5f,1,false);
        return super.use(pLevel, pPlayer, pHand);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        pLevel.playLocalSound(pLivingEntity.getX(),pLivingEntity.getY(),pLivingEntity.getZ(), ModSoundEvents.UMBRELLA_CLOSE, SoundSource.BLOCKS,.5f,1,false);
    }


}
