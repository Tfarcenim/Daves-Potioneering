package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItem;

public class GauntletHUD extends DrawableHelper {
    public static final Identifier GAUNTLET_ICON_LOC = new Identifier(DavesPotioneering.MODID, "textures/gauntlet_icons/");
    public final static GauntletHUD hudInstance = new GauntletHUD();

    public static Identifier getGauntletIconLoc(String fileName) {
        return new Identifier(GAUNTLET_ICON_LOC.getNamespace(), GAUNTLET_ICON_LOC.getPath() + fileName + ".png");
    }

    private Potion activePotion = null;
    private Potion prePotion = null;
    private Potion postPotion = null;
    private final Identifier hud = getGauntletIconLoc("hud");

    public int x = ModConfig.Client.gauntlet_hud_x;
    public int y = ModConfig.Client.gauntlet_hud_y;
    public HudPresets preset = ModConfig.Client.gauntlet_hud_preset;

    public static final MinecraftClient mc = MinecraftClient.getInstance();

    private static boolean forwardCycle = false;
    private static boolean backwardCycle = false;

    private static final int maxCooldown = 40;
    private static int cooldown = maxCooldown;

    public void init(Potion activePotion, Potion prePotion, Potion postPotion) {
        this.activePotion = activePotion;
        this.prePotion = prePotion;
        this.postPotion = postPotion;
    }

    public void render(MatrixStack matrixStack) {
        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(hud);

        int windowW = mc.getWindow().getScaledWidth();
        int windowH = mc.getWindow().getScaledHeight();

        int xFixed = MathHelper.clamp((windowW + x)/2, 0, windowW-120);
        int yFixed = MathHelper.clamp(windowH+y, 0, windowH-41);

        if (forwardCycle) {
            cooldown--;
            drawTexture(matrixStack, xFixed, yFixed, getZOffset(), 0, 87, 120, 41, 128, 128);
            if (cooldown <= 0) {
               mc.getSoundManager().play(PositionedSoundInstance.master(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (backwardCycle) {
            cooldown--;
            drawTexture(matrixStack, xFixed, yFixed, getZOffset(), 0, 44, 120, 41, 128, 128);
            if (cooldown <= 0) {
                mc.getSoundManager().play(PositionedSoundInstance.master(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            drawTexture(matrixStack, xFixed, yFixed, getZOffset(), 0, 1, 120, 41, 128, 128);
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandStack();

        CompoundTag info = g.getOrCreateTag().getCompound("info");
        renderPotion(prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, g), false);
        renderPotion(activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), g), true);
        renderPotion(postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, g), false);
        RenderSystem.popMatrix();
    }

    private void renderPotion(Potion potion, MatrixStack matrixStack, int x, int y, int cooldown, boolean isActivePotion) {
        if (potion == null) return;

        Identifier name = Registry.POTION.getId(potion);

        if (potion.getEffects().isEmpty()) return;

        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {
            if (name.toString().contains("turtle_master")) {
                mc.getTextureManager().bindTexture(getGauntletIconLoc("turtle_master"));
            } else if (mc.getResourceManager().containsResource(getGauntletIconLoc(name.toString()))) {
                mc.getTextureManager().bindTexture(getGauntletIconLoc(name.toString()));
            } else {
                mc.getTextureManager().bindTexture(getGauntletIconLoc("unknown"));
            }
            drawTexture(matrixStack, x, y, getZOffset(), 0, 0, 18, 18, 18, 18);
        } else {
            StatusEffect effect = potion.getEffects().get(0).getEffectType();
            Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(effect);
            mc.getTextureManager().bindTexture(sprite.getAtlas().getId());
            drawSprite(matrixStack, x, y, 0, 18, 18, sprite);
        }

        // render cooldown, modified from ItemRenderer
        if (cooldown > 0.0F) {
            RenderSystem.pushMatrix();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.translatef(1, 1, getZOffset()+1);
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
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
        renderer.begin(7, VertexFormats.POSITION_COLOR);
        renderer.vertex(x, y, 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();
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
