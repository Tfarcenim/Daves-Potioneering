package tfar.davespotioneering.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItem;

public class GauntletHUD extends GuiComponent {
    public static final ResourceLocation GAUNTLET_ICON_LOC = new ResourceLocation(DavesPotioneering.MODID, "textures/gauntlet_icons/");
    public final static GauntletHUD hudInstance = new GauntletHUD();

    public static ResourceLocation getGauntletIconLoc(String fileName) {
        return new ResourceLocation(GAUNTLET_ICON_LOC.getNamespace(), GAUNTLET_ICON_LOC.getPath() + fileName + ".png");
    }

    private Potion activePotion = null;
    private Potion prePotion = null;
    private Potion postPotion = null;
    private final ResourceLocation hud = getGauntletIconLoc("hud");

    public int x = ModConfig.Client.gauntlet_hud_x;
    public int y = ModConfig.Client.gauntlet_hud_y;
    public HudPresets preset = ModConfig.Client.gauntlet_hud_preset;

    public static final Minecraft mc = Minecraft.getInstance();

    private static boolean forwardCycle = false;
    private static boolean backwardCycle = false;

    private static final int maxCooldown = 40;
    private static int cooldown = maxCooldown;

    public void init(Potion activePotion, Potion prePotion, Potion postPotion) {
        this.activePotion = activePotion;
        this.prePotion = prePotion;
        this.postPotion = postPotion;
    }

    public void render(PoseStack matrixStack) {
        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);
        mc.getTextureManager().bind(hud);

        int windowW = mc.getWindow().getGuiScaledWidth();
        int windowH = mc.getWindow().getGuiScaledHeight();

        int xFixed = Mth.clamp((windowW + x)/2, 0, windowW-120);
        int yFixed = Mth.clamp(windowH+y, 0, windowH-41);

        if (forwardCycle) {
            cooldown--;
            blit(matrixStack, xFixed, yFixed, getBlitOffset(), 0, 87, 120, 41, 128, 128);
            if (cooldown <= 0) {
               mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (backwardCycle) {
            cooldown--;
            blit(matrixStack, xFixed, yFixed, getBlitOffset(), 0, 44, 120, 41, 128, 128);
            if (cooldown <= 0) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            blit(matrixStack, xFixed, yFixed, getBlitOffset(), 0, 1, 120, 41, 128, 128);
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();

        CompoundTag info = g.getOrCreateTag().getCompound("info");
        renderPotion(prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, g), false);
        renderPotion(activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), g), true);
        renderPotion(postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, g), false);
        RenderSystem.popMatrix();
    }

    private void renderPotion(Potion potion, PoseStack matrixStack, int x, int y, int cooldown, boolean isActivePotion) {
        if (potion == null) return;

        ResourceLocation name = Registry.POTION.getKey(potion);

        if (potion.getEffects().isEmpty()) return;

        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {
            if (name.toString().contains("turtle_master")) {
                mc.getTextureManager().bind(getGauntletIconLoc("turtle_master"));
            } else if (mc.getResourceManager().hasResource(getGauntletIconLoc(name.toString()))) {
                mc.getTextureManager().bind(getGauntletIconLoc(name.toString()));
            } else {
                mc.getTextureManager().bind(getGauntletIconLoc("unknown"));
            }
            blit(matrixStack, x, y, getBlitOffset(), 0, 0, 18, 18, 18, 18);
        } else {
            MobEffect effect = potion.getEffects().get(0).getEffect();
            TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effect);
            mc.getTextureManager().bind(sprite.atlas().location());
            blit(matrixStack, x, y, 0, 18, 18, sprite);
        }

        // render cooldown, modified from ItemRenderer
        if (cooldown > 0.0F) {
            RenderSystem.pushMatrix();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.translatef(1, 1, getBlitOffset()+1);
            Tesselator tessellator1 = Tesselator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuilder();
            if (isActivePotion) {
                int scale = getScaledCooldown(18, cooldown);
                this.draw(bufferbuilder1, x, y + scale, 18, 18-scale, 255, 255, 255, 127);
            } else {
                int scale = getScaledCooldown(16, cooldown);
                this.draw(bufferbuilder1, x, y + scale, 17, 16-scale, 255, 255, 255, 127);
            }
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
            RenderSystem.popMatrix();
        }

        RenderSystem.popMatrix();
    }

    private int getScaledCooldown(float pixels, float cooldown) {
        float totalCooldown = ModConfig.Server.gauntlet_cooldown;
        float progress = totalCooldown - cooldown;

        if (totalCooldown != 0) {
            float result = progress*pixels/totalCooldown;
            return Math.round(result);
        }

        return 0;
    }

    // copy-pasted from ItemRenderer class
    private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(7, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).endVertex();
        Tesselator.getInstance().end();
    }

    public void refreshPosition() {
    //    x = ModConfig.Client.client.get();
    //    y = ModConfig.Client.gauntlet_hud_y.get();
    //    preset = ModConfig.Client.gauntlet_hud_preset.get();
    }

    public static void forwardCycle() {
        forwardCycle = true;
    }

    public static void backwardCycle() {
        backwardCycle = true;
    }

    public enum HudPresets{
        TOP_LEFT,
        TOP_RIGHT,
        BTM_LEFT,
        BTM_RIGHT,
        ABOVE_HOTBAR,
        FREE_MOVE
    }
}
