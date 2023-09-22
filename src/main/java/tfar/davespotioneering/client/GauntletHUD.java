package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
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
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.IngameGuiAccess;

public class GauntletHUD {
    public static final ResourceLocation GAUNTLET_ICON_LOC = new ResourceLocation(DavesPotioneering.MODID, "textures/gauntlet_icons/");

    private static final int TEX_H = 41;

    static final int TEX_HEIGHT = 41;
    static final int TEX_WIDTH = 121;

    public static ResourceLocation getGauntletIconLoc(String fileName) {
        return new ResourceLocation(GAUNTLET_ICON_LOC.getNamespace(), GAUNTLET_ICON_LOC.getPath() + fileName + ".png");
    }

    private static Potion activePotion = null;
    private static Potion prePotion = null;
    private static Potion postPotion = null;
    private static final ResourceLocation hud_texture = getGauntletIconLoc("hud");

    public static final Minecraft mc = Minecraft.getInstance();

    private static boolean forwardCycle = false;
    private static boolean backwardCycle = false;

    private static final int maxCooldown = 40;
    private static int cooldown = maxCooldown;

    public static void init(Potion activePotion, Potion prePotion, Potion postPotion) {
        GauntletHUD.activePotion = activePotion;
        GauntletHUD.prePotion = prePotion;
        GauntletHUD.postPotion = postPotion;
    }

    public static void render(GuiGraphics matrixStack) {
//        matrixStack.pushPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
     //   RenderSystem.setShaderTexture(0,hud_texture);

        Gui hud = mc.gui;

        int fade = ((IngameGuiAccess)hud).getToolHighlightTimer();

        int windowW = mc.getWindow().getGuiScaledWidth();
        int windowH = mc.getWindow().getGuiScaledHeight();

        int xFixed = Mth.clamp((windowW + DavesPotioneering.CONFIG.gauntlet_hud_x)/2, 0, windowW-120);
        int yFixed = Mth.clamp(windowH+DavesPotioneering.CONFIG.gauntlet_hud_y, 0, windowH-TEX_H);


        if(DavesPotioneering.CONFIG.gauntlet_hud_preset == Preset.ABOVE_HOTBAR) {
            int height = TEX_H + 50;
            if (fade > 0) {
                height += 10;
            }
            yFixed = windowH - height;
        }

        if (forwardCycle) {
            cooldown--;
            matrixStack.blit(hud_texture,xFixed, yFixed, 0, 0, 87, 120, TEX_H, 128, 128);
            if (cooldown <= 0) {
               mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (backwardCycle) {
            cooldown--;
            matrixStack.blit(hud_texture,xFixed, yFixed, 0, 0, 44, 120, TEX_H, 128, 128);
            if (cooldown <= 0) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            matrixStack.blit(hud_texture,xFixed, yFixed, 0, 0, 1, 120, TEX_H, 128, 128);
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();

        CompoundTag info = g.getOrCreateTag().getCompound("info");
        renderPotion(prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, g));
        renderPotion(activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), g));
        renderPotion(postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, g));
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
                resourceLocation = getGauntletIconLoc("turtle_master");
            } else if (mc.getResourceManager().getResource(getGauntletIconLoc(name)).isPresent()) {
                resourceLocation =getGauntletIconLoc(name);
            } else {
                resourceLocation = getGauntletIconLoc("unknown");
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
        float totalCooldown = DavesPotioneering.CONFIG.gauntlet_cooldown;
        float progress = totalCooldown - cooldown;

        if (totalCooldown != 0) {
            float result = progress*pixels/totalCooldown;
            return Math.round(result);
        }

        return 0;
    }

    // copy-pasted from ItemRenderer class
    private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).endVertex();
        Tesselator.getInstance().end();
    }

    public static void forwardCycle() {
        forwardCycle = true;
    }

    public static void backwardCycle() {
        backwardCycle = true;
    }

    public enum Preset {
        TOP_LEFT,
        TOP_RIGHT,
        BTM_LEFT,
        BTM_RIGHT,
        ABOVE_HOTBAR,
        FREE_MOVE
    }
}
