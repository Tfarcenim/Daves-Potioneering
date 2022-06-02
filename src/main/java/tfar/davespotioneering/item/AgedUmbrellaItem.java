package tfar.davespotioneering.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;
import tfar.davespotioneering.init.ModItems;

import java.util.function.Consumer;

public class AgedUmbrellaItem extends UmbrellaItem {
    public AgedUmbrellaItem(Properties builder, String style) {
        super(builder,"", style);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept( new IItemRenderProperties() {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> ister = NonNullLazy.of(ModItems.HideISTERsFromServer::createAgedUmbrellaItemStackRenderer);
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return ister.get();
            }
        });
    }
}
