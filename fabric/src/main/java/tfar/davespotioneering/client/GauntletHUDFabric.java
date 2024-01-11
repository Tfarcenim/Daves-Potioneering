package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import tfar.davespotioneering.DavesPotioneeringFabric;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItemFabric;
import tfar.davespotioneering.mixin.GuiAccess;

public class GauntletHUDFabric {

    private static Potion activePotion = null;
    private static Potion prePotion = null;
    private static Potion postPotion = null;
    public static final Minecraft mc = Minecraft.getInstance();

    private static boolean forwardCycle = false;
    private static boolean backwardCycle = false;

    private static final int maxCooldown = 40;
    private static int cooldown = maxCooldown;

    public static void init(Potion activePotion, Potion prePotion, Potion postPotion) {
        GauntletHUDFabric.activePotion = activePotion;
        GauntletHUDFabric.prePotion = prePotion;
        GauntletHUDFabric.postPotion = postPotion;
    }

    public static void render(GuiGraphics matrixStack) {
//        matrixStack.pushPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
     //   RenderSystem.setShaderTexture(0,hud_texture);

        Gui hud = mc.gui;

        int fade = ((GuiAccess)hud).getToolHighlightTimer();

        int windowW = mc.getWindow().getGuiScaledWidth();
        int windowH = mc.getWindow().getGuiScaledHeight();

        int xFixed = Mth.clamp((windowW + DavesPotioneeringFabric.CONFIG.gauntlet_hud_x)/2, 0, windowW-120);
        int yFixed = Mth.clamp(windowH+ DavesPotioneeringFabric.CONFIG.gauntlet_hud_y, 0, windowH-GauntletHUDCommon.TEX_HEIGHT);


        if(DavesPotioneeringFabric.CONFIG.gauntlet_hud_preset == HudPreset.ABOVE_HOTBAR) {
            int height = GauntletHUDCommon.TEX_HEIGHT + 50;
            if (fade > 0) {
                height += 10;
            }
            yFixed = windowH - height;
        }

        if (forwardCycle) {
            cooldown--;
            matrixStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 87, 120, GauntletHUDCommon.TEX_HEIGHT, 128, 128);
            if (cooldown <= 0) {
               mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (backwardCycle) {
            cooldown--;
            matrixStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 44, 120, GauntletHUDCommon.TEX_HEIGHT, 128, 128);
            if (cooldown <= 0) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            matrixStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 1, 120, GauntletHUDCommon.TEX_HEIGHT, 128, 128);
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();

        CompoundTag info = g.getOrCreateTag().getCompound("info");
        renderPotion(prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItemFabric.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, player));
        renderPotion(activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItemFabric.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"),player));
        renderPotion(postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItemFabric.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, player));
     //   matrixStack.popPose();
    }

    private static void renderPotion(Potion potion, GuiGraphics matrixStack, int x, int y, int cooldown) {
        if (potion == null) return;
        if (potion.getEffects().isEmpty()) return;

        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {

            String name = BuiltInRegistries.POTION.getKey(potion).toString();
            ResourceLocation resourceLocation;
            if (name.contains("turtle_master")) {
                resourceLocation = GauntletHUDCommon.getGauntletIconLoc("turtle_master");
            } else if (mc.getResourceManager().getResource(GauntletHUDCommon.getGauntletIconLoc(name)).isPresent()) {
                resourceLocation =GauntletHUDCommon.getGauntletIconLoc(name);
            } else {
                resourceLocation = GauntletHUDCommon.getGauntletIconLoc("unknown");
            }
            matrixStack.blit(resourceLocation, x, y, 0, 0, 0, 18, 18, 18, 18);
        } else {
            MobEffect effect = potion.getEffects().get(0).getEffect();
            TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effect);
            matrixStack.blit(x, y, 0, 18, 18, sprite);
        }


        // render cooldown
        if (cooldown > 0) {

            if (FabricLoader.getInstance().isDevelopmentEnvironment())
                matrixStack.drawString(mc.font, cooldown + "", x, y - 20, 0xff0000);

            int w = 18;
            int scale = getScaledCooldown(w, cooldown);
            matrixStack.fill(x, y + w - scale, x + 18, y + w, 0x7fffffff);
        }
    }

    private static int getScaledCooldown(float pixels, float cooldown) {
        float totalCooldown = DavesPotioneeringFabric.CONFIG.gauntlet_cooldown;
        float progress = totalCooldown - cooldown;

        if (totalCooldown != 0) {
            float result = progress*pixels/totalCooldown;
            return Math.round(result);
        }

        return 0;
    }

    public static void forwardCycle() {
        forwardCycle = true;
    }

    public static void backwardCycle() {
        backwardCycle = true;
    }
}
