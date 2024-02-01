package tfar.davespotioneering.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModParticleTypes;
import tfar.davespotioneering.item.CGauntletItem;
import tfar.davespotioneering.mixin.ParticleManagerAccess;
import tfar.davespotioneering.platform.Services;

import java.lang.ref.WeakReference;
import java.util.List;

public class DavesPotioneeringClient {

    public static final ClampedItemPropertyFunction GAUNTLET = (stack, level, entity, i) -> stack.hasTag() ? stack.getTag().getBoolean("active") ? 1 : 0 : 0;

    public static KeyMapping CONFIG_KEY = new KeyMapping("key.davespotioneering.open_config",
            InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_3, "key.categories." + DavesPotioneering.MODID);

    public static final BlockColor CAULDRON = (state, reader, pos, index) -> {
        if (pos != null) {
            BlockEntity blockEntity = reader.getBlockEntity(pos);
            if (blockEntity instanceof CReinforcedCauldronBlockEntity reinforced)
                return reinforced.getColor();
        }
        return 0xffffff;
    };

    public static boolean IS_EMBEDDIUM_OR_SODIUM_HERE;

    public static void clientSetup() {

        IS_EMBEDDIUM_OR_SODIUM_HERE = Services.PLATFORM.isModLoaded("sodium") || Services.PLATFORM.isModLoaded("rubidium")
                || Services.PLATFORM.isModLoaded("embeddium");

        ItemProperties.register(ModItems.POTIONEER_GAUNTLET, new ResourceLocation("active"), GAUNTLET);

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


    public static final ClampedItemPropertyFunction BLOCKING = (stack, world, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;

    public static void registerBlockingProperty(Item item) {
        ItemProperties.register(item, new ResourceLocation("blocking"),BLOCKING);
    }

    public static float computeBlockingOverride() {
        return BLOCKING.unclampedCall(itemStack,(ClientLevel) level.get(), player.get(), seed);
    }

    public static void spawnFluidParticle(ClientLevel world, Vec3 blockPosIn, ParticleOptions particleDataIn, int color) {
        // world.spawnParticle(new BlockPos(blockPosIn), particleDataIn, voxelshape, blockPosIn.getY() +.5);

        Particle particle = ((ParticleManagerAccess) Minecraft.getInstance().particleEngine).$makeParticle(particleDataIn, blockPosIn.x, blockPosIn.y, blockPosIn.z, 0, -.10, 0);

        float red = (color >> 16 & 0xff) / 255f;
        float green = (color >> 8 & 0xff) / 255f;
        float blue = (color & 0xff) / 255f;

        particle.setColor(red, green, blue);

        Minecraft.getInstance().particleEngine.add(particle);

        //world.addParticle(particleDataIn,blockPosIn.x,blockPosIn.y,blockPosIn.z,0,-.10,0);
    }

    public static void clientPlayerTick(Player player) {
        if (player.level().getGameTime() % Services.PLATFORM.particleDripRate() == 0) {

            ItemStack stack = player.getMainHandItem();

            if (!PotionUtils.getMobEffects(stack).isEmpty() && DavesPotioneering.isCoatable(stack)) {


                ParticleOptions particleData = ModParticleTypes.FAST_DRIPPING_WATER;

                Vec3 vec = player.position().add(0, +player.getBbHeight() / 2, 0);

                double yaw = -Mth.wrapDegrees(player.getYRot());

                double of1 = Math.random() * .60 + .15;
                double of2 = .40 + Math.random() * .10;


                double z1 = Math.cos(yaw * Math.PI / 180) * of1;
                double x1 = Math.sin(yaw * Math.PI / 180) * of1;

                double z2 = Math.cos((yaw + 270) * Math.PI / 180) * of2;
                double x2 = Math.sin((yaw + 270) * Math.PI / 180) * of2;

                vec = vec.add(x1 + x2, 0, z1 + z2);

                int color = PotionUtils.getColor(stack);
                DavesPotioneeringClient.spawnFluidParticle(Minecraft.getInstance().level, vec, particleData, color);
            }
        }
    }

    public static void onMouseInput(int button) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof CGauntletItem && player.isShiftKeyDown()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                GauntletHUDMovementScreen.open();
            }
        }
    }

    public static boolean onMouseScroll(double scrollDelta) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return false;
        if (held.getItem() instanceof CGauntletItem && player.isShiftKeyDown()) {
            if (scrollDelta > 0) {
                Services.PLATFORM.cycleGauntlet(true);
                GauntletHUDCommon.backwardCycle();
            } else {
                Services.PLATFORM.cycleGauntlet(false);
                GauntletHUDCommon.forwardCycle();
            }
            return true;
        }
        return false;
    }

    public static void tooltips(ItemStack stack, List<Component> tooltips) {
        if (!PotionUtils.getMobEffects(stack).isEmpty()) {
            if (DavesPotioneering.isCoatable(stack)) {
                tooltips.add(Component.literal("Coated with"));
                PotionUtils.addPotionTooltip(stack, tooltips, 0.125F);
                tooltips.add(Component.literal("Uses: " + stack.getTag().getInt("uses")));
            } else if (stack.getItem().isEdible()) {
                PotionUtils.addPotionTooltip(stack, tooltips, 0.125F);
            }
        }
    }



    public static ItemStack itemStack;
    public static WeakReference<Level> level;
    public static WeakReference<LivingEntity> player;
    public static int seed;


    public static int computeinvis2Color(MobEffectInstance effectinstance) {
        int k = effectinstance.getEffect().getColor();
        int l = 1;
        float r = (float)(l * (k >> 16 & 255)) / 255.0F;
        float g = (float)(l * (k >> 8 & 255)) / 255.0F;
        float b = (float)(l * (k & 255)) / 255.0F;

        r = r * 255.0F;
        g = g * 255.0F;
        b = b * 255.0F;
        return (int)r << 16 | (int)g << 8 | (int)b;
    }
}