package tfar.davespotioneering.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.DyeColor;
import software.bernie.example.item.GeckoHabitatItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.davespotioneering.client.ClientEvents;
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
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UmbrellaItem extends CUmbrellaItem implements GeoItem {


    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public UmbrellaItem(Properties builder, DyeColor name, String style) {
        super(builder, name, style);
    }

    public UmbrellaItem(Properties builder, String name, String style) {
        super(builder, name, style);
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

    // Utilise our own render hook to define our custom renderer
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoItemRenderer<UmbrellaItem> renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = ClientEvents.umbrella(name);

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
