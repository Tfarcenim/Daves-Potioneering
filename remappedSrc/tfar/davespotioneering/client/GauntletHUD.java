package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.IngameGuiAccess;

public class GauntletHUD {
    public static final ResourceLocation GAUNTLET_ICON_LOC = new ResourceLocation(DavesPotioneering.MODID, "textures/gauntlet_icons/");

    private static final int TEX_H = 41;

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

    public static void render(PoseStack matrixStack) {
        matrixStack.pushPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0,hud_texture);

        Gui hud = mc.gui;

        int fade = ((IngameGuiAccess)hud).getHeldItemTooltipFade();

        int windowW = mc.getWindow().getGuiScaledWidth();
        int windowH = mc.getWindow().getGuiScaledHeight();

        int xFixed = Mth.clamp((windowW + DavesPotioneering.CONFIG.gauntlet_hud_x)/2, 0, windowW-120);
        int yFixed = Mth.clamp(windowH+DavesPotioneering.CONFIG.gauntlet_hud_y, 0, windowH-TEX_H);


        if(DavesPotioneering.CONFIG.gauntlet_hud_preset == HudPreset.ABOVE_HOTBAR) {
            int height = TEX_H + 50;
            if (fade > 0) {
                height += 10;
            }
            yFixed = windowH - height;
        }

        if (forwardCycle) {
            cooldown--;
            DrawableHelper.drawTexture(matrixStack, xFixed, yFixed, hud.getZOffset(), 0, 87, 120, TEX_H, 128, 128);
            if (cooldown <= 0) {
               mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (backwardCycle) {
            cooldown--;
            DrawableHelper.drawTexture(matrixStack, xFixed, yFixed, hud.getZOffset(), 0, 44, 120, TEX_H, 128, 128);
            if (cooldown <= 0) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            DrawableHelper.drawTexture(matrixStack, xFixed, yFixed, hud.getZOffset(), 0, 1, 120, TEX_H, 128, 128);
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();

        CompoundTag info = g.getOrCreateTag().getCompound("info");
        renderPotion(prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, g), false);
        renderPotion(activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), g), true);
        renderPotion(postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, g), false);
        matrixStack.popPose();
    }

    private static void renderPotion(Potion potion, PoseStack matrixStack, int x, int y, int cooldown, boolean isActivePotion) {
        if (potion == null) return;

        ResourceLocation name = Registry.POTION.getId(potion);

        if (potion.getEffects().isEmpty()) return;

        matrixStack.pushPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {
            if (name.toString().contains("turtle_master")) {
                RenderSystem.setShaderTexture(0,getGauntletIconLoc("turtle_master"));
            } else if (mc.getResourceManager().getResource(getGauntletIconLoc(name.toString())).isPresent()) {
                RenderSystem.setShaderTexture(0,getGauntletIconLoc(name.toString()));
            } else {
                RenderSystem.setShaderTexture(0,getGauntletIconLoc("unknown"));
            }
            DrawableHelper.drawTexture(matrixStack, x, y, mc.gui.getZOffset(), 0, 0, 18, 18, 18, 18);
        } else {
            MobEffect effect = potion.getEffects().get(0).getEffect();
            TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effect);
            RenderSystem.setShaderTexture(0,sprite.getAtlas().getId());
            DrawableHelper.drawSprite(matrixStack, x, y, 0, 18, 18, sprite);
        }

        // render cooldown, modified from ItemRenderer
        if (cooldown > 0.0F) {
            matrixStack.pushPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            matrixStack.translate(1, 1, mc.gui.getZOffset()+1);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            if (isActivePotion) {
                int scale = getScaledCooldown(18, cooldown);
                draw(bufferbuilder, x, y + scale, 18, 18-scale, 255, 255, 255, 127);
            } else {
                int scale = getScaledCooldown(16, cooldown);
                draw(bufferbuilder, x, y + scale, 17, 16-scale, 255, 255, 255, 127);
            }
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
            matrixStack.popPose();
        }

        matrixStack.popPose();
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

    public enum HudPreset {
        TOP_LEFT,
        TOP_RIGHT,
        BTM_LEFT,
        BTM_RIGHT,
        ABOVE_HOTBAR,
        FREE_MOVE
    }
}
