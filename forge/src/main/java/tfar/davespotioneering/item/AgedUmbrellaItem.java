package tfar.davespotioneering.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;
import tfar.davespotioneering.client.HideISTERsFromServer;

import java.util.function.Consumer;

public class AgedUmbrellaItem extends UmbrellaItem {
    public AgedUmbrellaItem(Properties builder, String style) {
        super(builder,"", style);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept( new IClientItemExtensions() {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> ister = NonNullLazy.of(HideISTERsFromServer::createAgedUmbrellaItemStackRenderer);
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return ister.get();
            }
        });
    }
}
