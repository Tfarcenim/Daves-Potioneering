package tfar.davespotioneering.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.davespotioneering.client.HideISTERsFromServer;
import tfar.davespotioneering.init.ModSoundEvents;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class UmbrellaItem extends CUmbrellaItem implements GeoItem {
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
        tooltip.add(Component.literal(style));

        tooltip.add(Component.translatable(getDescriptionId()+".desc"));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> ister = NonNullLazy.of(() -> HideISTERsFromServer.createGeoClassicUmbrellaItemStackRenderer(model));

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ister.get();
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(
                new AnimationController<>(this, "null", 0, this::predicate)
        );
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected <P extends GeoAnimatable> PlayState predicate(AnimationState<P> event) {
        return PlayState.STOP;
    }

}
