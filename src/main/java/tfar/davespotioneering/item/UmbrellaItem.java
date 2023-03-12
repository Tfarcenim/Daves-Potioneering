package tfar.davespotioneering.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModSoundEvents;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class UmbrellaItem extends ShieldItem {
    private final String model;
    private final String style;

    public UmbrellaItem(Properties builder, DyeColor model, String style) {
        this(builder,model.getName(),style);
    }

    public UmbrellaItem(Properties builder, String model,String style) {
        super(builder);
        this.model = model;
        this.style = style;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TextComponent(style));

        tooltip.add(new TranslatableComponent(getDescriptionId()+".desc"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pLevel.playLocalSound(pPlayer.getX(),pPlayer.getY(),pPlayer.getZ(), ModSoundEvents.UMBRELLA_OPEN, SoundSource.BLOCKS,.5f,1,false);
        return super.use(pLevel, pPlayer, pHand);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        pLevel.playLocalSound(pLivingEntity.getX(),pLivingEntity.getY(),pLivingEntity.getZ(), ModSoundEvents.UMBRELLA_CLOSE, SoundSource.BLOCKS,.5f,1,false);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept( new IItemRenderProperties() {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> ister = NonNullLazy.of(() -> ModItems.HideISTERsFromServer.createGeoClassicUmbrellaItemStackRenderer(model));

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return ister.get();
            }
        });
    }
}
