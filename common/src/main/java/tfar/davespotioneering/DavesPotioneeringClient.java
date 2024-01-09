package tfar.davespotioneering;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class DavesPotioneeringClient {

    public static final ClampedItemPropertyFunction GAUNTLET = (stack, level, entity, i) -> stack.hasTag() ? stack.getTag().getBoolean("active") ? 1 : 0 : 0;

    public static void clientSetup() {

    }


    public static void registerBlockingProperty(Item item) {
        ItemProperties.register(item, new ResourceLocation("blocking"),
                (stack, world, entity,i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

}
