package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItem;

public class GauntletHUD {
    public static final Identifier GAUNTLET_ICON_LOC = new Identifier(DavesPotioneering.MODID, "textures/gauntlet_icons/");

    public static Identifier getGauntletIconLoc(String fileName) {
        return new Identifier(GAUNTLET_ICON_LOC.getNamespace(), GAUNTLET_ICON_LOC.getPath() + fileName + ".png");
    }

    private static Potion activePotion = null;
    private static Potion prePotion = null;
    private static Potion postPotion = null;
    private static final Identifier hud_texture = getGauntletIconLoc("hud");

    public static final MinecraftClient mc = MinecraftClient.getInstance();

    private static boolean forwardCycle = false;
    private static boolean backwardCycle = false;

    private static final int maxCooldown = 40;
    private static int cooldown = maxCooldown;

    public static void init(Potion activePotion, Potion prePotion, Potion postPotion) {
        GauntletHUD.activePotion = activePotion;
        GauntletHUD.prePotion = prePotion;
        GauntletHUD.postPotion = postPotion;
    }

    public static void render(MatrixStack matrixStack) {
        matrixStack.push();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(hud_texture);

        InGameHud hud = mc.inGameHud;

        int windowW = mc.getWindow().getScaledWidth();
        int windowH = mc.getWindow().getScaledHeight();

        int xFixed = MathHelper.clamp((windowW + ClothConfig.gauntlet_hud_x)/2, 0, windowW-120);
        int yFixed = MathHelper.clamp(windowH+ClothConfig.gauntlet_hud_y, 0, windowH-41);

        if (forwardCycle) {
            cooldown--;
            DrawableHelper.drawTexture(matrixStack, xFixed, yFixed, hud.getZOffset(), 0, 87, 120, 41, 128, 128);
            if (cooldown <= 0) {
               mc.getSoundManager().play(PositionedSoundInstance.master(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (backwardCycle) {
            cooldown--;
            DrawableHelper.drawTexture(matrixStack, xFixed, yFixed, hud.getZOffset(), 0, 44, 120, 41, 128, 128);
            if (cooldown <= 0) {
                mc.getSoundManager().play(PositionedSoundInstance.master(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            DrawableHelper.drawTexture(matrixStack, xFixed, yFixed, hud.getZOffset(), 0, 1, 120, 41, 128, 128);
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandStack();

        NbtCompound info = g.getOrCreateNbt().getCompound("info");
        renderPotion(prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, g), false);
        renderPotion(activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), g), true);
        renderPotion(postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, g), false);
        matrixStack.pop();
    }

    private static void renderPotion(Potion potion, MatrixStack matrixStack, int x, int y, int cooldown, boolean isActivePotion) {
        if (potion == null) return;

        Identifier name = Registry.POTION.getId(potion);

        if (potion.getEffects().isEmpty()) return;

        matrixStack.push();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {
            if (name.toString().contains("turtle_master")) {
                mc.getTextureManager().bindTexture(getGauntletIconLoc("turtle_master"));
            } else if (mc.getResourceManager().containsResource(getGauntletIconLoc(name.toString()))) {
                mc.getTextureManager().bindTexture(getGauntletIconLoc(name.toString()));
            } else {
                mc.getTextureManager().bindTexture(getGauntletIconLoc("unknown"));
            }
            DrawableHelper.drawTexture(matrixStack, x, y, mc.inGameHud.getZOffset(), 0, 0, 18, 18, 18, 18);
        } else {
            StatusEffect effect = potion.getEffects().get(0).getEffectType();
            Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(effect);
            mc.getTextureManager().bindTexture(sprite.getAtlas().getId());
            DrawableHelper.drawSprite(matrixStack, x, y, 0, 18, 18, sprite);
        }

        // render cooldown, modified from ItemRenderer
        if (cooldown > 0.0F) {
            matrixStack.push();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            matrixStack.translate(1, 1, mc.inGameHud.getZOffset()+1);
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            if (isActivePotion) {
                int scale = getScaledCooldown(18, cooldown);
                draw(bufferbuilder1, x, y + scale, 18, 18-scale, 255, 255, 255, 127);
            } else {
                int scale = getScaledCooldown(16, cooldown);
                draw(bufferbuilder1, x, y + scale, 17, 16-scale, 255, 255, 255, 127);
            }
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
            matrixStack.pop();
        }

        matrixStack.pop();
    }

    private static int getScaledCooldown(float pixels, float cooldown) {
        float totalCooldown = ClothConfig.gauntlet_cooldown;
        float progress = totalCooldown - cooldown;

        if (totalCooldown != 0) {
            float result = progress*pixels/totalCooldown;
            return Math.round(result);
        }

        return 0;
    }

    // copy-pasted from ItemRenderer class
    private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        renderer.vertex(x, y, 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();
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
