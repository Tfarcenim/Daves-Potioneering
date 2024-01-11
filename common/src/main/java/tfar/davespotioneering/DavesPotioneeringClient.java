package tfar.davespotioneering;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModItems;

public class DavesPotioneeringClient {

    public static final ClampedItemPropertyFunction GAUNTLET = (stack, level, entity, i) -> stack.hasTag() ? stack.getTag().getBoolean("active") ? 1 : 0 : 0;

    public static KeyMapping CONFIG_KEY = new KeyMapping("key.davespotioneering.open_config",
            InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_3,"key.categories."+ DavesPotioneering.MODID);

    public static void clientSetup() {

        Minecraft.getInstance().getBlockColors().register((state, reader, pos, index) -> {
            if (pos != null) {
                BlockEntity blockEntity = reader.getBlockEntity(pos);
                if (blockEntity instanceof CReinforcedCauldronBlockEntity reinforced)
                    return reinforced.getColor();
            }
            return 0xffffff;
        }, ModBlocks.REINFORCED_WATER_CAULDRON);

        ItemProperties.register(ModItems.POTIONEER_GAUNTLET, new ResourceLocation("active"),GAUNTLET);

        registerBlockingProperty(ModItems.WHITE_UMBRELLA);
        registerBlockingProperty(ModItems.ORANGE_UMBRELLA);
        registerBlockingProperty(ModItems.MAGENTA_UMBRELLA);
        registerBlockingProperty(ModItems.LIGHT_BLUE_UMBRELLA);
        registerBlockingProperty(ModItems.YELLOW_UMBRELLA);
        registerBlockingProperty(ModItems.LIME_UMBRELLA);
        registerBlockingProperty(ModItems.PINK_UMBRELLA);
        registerBlockingProperty(ModItems.GRAY_UMBRELLA);
        registerBlockingProperty(ModItems.LIGHT_GRAY_UMBRELLA);
        registerBlockingProperty(ModItems.CYAN_UMBRELLA);
        registerBlockingProperty(ModItems.PURPLE_UMBRELLA);
        registerBlockingProperty(ModItems.BLUE_UMBRELLA);
        registerBlockingProperty(ModItems.BROWN_UMBRELLA);
        registerBlockingProperty(ModItems.GREEN_UMBRELLA);
        registerBlockingProperty(ModItems.RED_UMBRELLA);
        registerBlockingProperty(ModItems.BLACK_UMBRELLA);

        registerBlockingProperty(ModItems.AGED_UMBRELLA);
        registerBlockingProperty(ModItems.GILDED_UMBRELLA);

    }


    public static void registerBlockingProperty(Item item) {
        ItemProperties.register(item, new ResourceLocation("blocking"),
                (stack, world, entity,i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

}
