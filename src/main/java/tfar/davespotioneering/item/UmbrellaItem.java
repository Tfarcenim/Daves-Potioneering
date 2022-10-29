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
import tfar.davespotioneering.init.ModItems;

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
        tooltip.add(Component.literal(style));

        tooltip.add(Component.translatable(getDescriptionId()+".desc"));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept( new IClientItemExtensions() {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> ister = NonNullLazy.of(() -> ModItems.HideISTERsFromServer.createGeoClassicUmbrellaItemStackRenderer(model));

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return ister.get();
            }
        });
    }
}
