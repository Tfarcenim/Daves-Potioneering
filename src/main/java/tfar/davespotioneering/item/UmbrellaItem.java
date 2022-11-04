package tfar.davespotioneering.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import tfar.davespotioneering.client.HideISTERsFromServer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class UmbrellaItem extends ShieldItem implements IAnimatable {
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
        consumer.accept( new IClientItemExtensions() {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> ister = NonNullLazy.of(() -> HideISTERsFromServer.createGeoClassicUmbrellaItemStackRenderer(model));

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return ister.get();
            }
        });
    }

    AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(
                new AnimationController<>(this, "null", 0, this::predicate)
        );
    }

    protected <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.STOP;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
