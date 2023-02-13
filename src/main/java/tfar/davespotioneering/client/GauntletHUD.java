package tfar.davespotioneering.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItem;

public class GauntletHUD extends AbstractGui {
    public static final ResourceLocation GAUNTLET_ICON_LOC = new ResourceLocation(DavesPotioneering.MODID, "textures/gauntlet_icons/");
    public final static GauntletHUD hudInstance = new GauntletHUD();

    public static final int TEX_Y = 41;

    public static ResourceLocation getGauntletIconLoc(String fileName) {
        return new ResourceLocation(GAUNTLET_ICON_LOC.getNamespace(), GAUNTLET_ICON_LOC.getPath() + fileName + ".png");
    }

    static final int TEX_HEIGHT = 41;
    static final int TEX_WIDTH = 121;

    private static Potion activePotion = null;
    private static Potion prePotion = null;
    private static Potion postPotion = null;
    private final ResourceLocation hud = getGauntletIconLoc("hud");

    public static int x = ModConfig.Client.gauntlet_hud_x.get();
    public static int y = ModConfig.Client.gauntlet_hud_y.get();
    public static HudPresets preset = ModConfig.Client.gauntlet_hud_preset.get();

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

    public void render(MatrixStack matrixStack) {
        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(hud);

        int windowW = mc.getMainWindow().getScaledWidth();
        int windowH = mc.getMainWindow().getScaledHeight();

        int xFixed = MathHelper.clamp((windowW + x)/2, 0, windowW-120);
        int yFixed = MathHelper.clamp(windowH+y, 0, windowH-TEX_Y);

        if (preset == HudPresets.ABOVE_HOTBAR) {
            yFixed = windowH - Math.max(ForgeIngameGui.left_height,ForgeIngameGui.right_height) - TEX_Y;
        }

        if (forwardCycle) {
            cooldown--;
            blit(matrixStack, xFixed, yFixed, getBlitOffset(), 0, 87, 120, TEX_Y, 128, 128);
            if (cooldown <= 0) {
               mc.getSoundHandler().play(SimpleSound.master(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (backwardCycle) {
            cooldown--;
            blit(matrixStack, xFixed, yFixed, getBlitOffset(), 0, 44, 120, TEX_Y, 128, 128);
            if (cooldown <= 0) {
                mc.getSoundHandler().play(SimpleSound.master(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            blit(matrixStack, xFixed, yFixed, getBlitOffset(), 0, 1, 120, TEX_Y, 128, 128);
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getHeldItemMainhand();

        CompoundNBT info = g.getOrCreateTag().getCompound("info");
        renderPotion(prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, g), false);
        renderPotion(activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), g), true);
        renderPotion(postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItem.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, g), false);
        RenderSystem.popMatrix();
    }

    private void renderPotion(Potion potion, MatrixStack matrixStack, int x, int y, int cooldown, boolean isActivePotion) {
        if (potion == null || potion.getRegistryName() == null) return;
        if (potion.getEffects().isEmpty()) return;

        RenderSystem.pushMatrix();
        RenderSystem.color4f(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {
            if (potion.getRegistryName().toString().contains("turtle_master")) {
                mc.getTextureManager().bindTexture(getGauntletIconLoc("turtle_master"));
            } else if (mc.getResourceManager().hasResource(getGauntletIconLoc(potion.getRegistryName().toString()))) {
                mc.getTextureManager().bindTexture(getGauntletIconLoc(potion.getRegistryName().toString()));
            } else {
                mc.getTextureManager().bindTexture(getGauntletIconLoc("unknown"));
            }
            blit(matrixStack, x, y, getBlitOffset(), 0, 0, 18, 18, 18, 18);
        } else {
            Effect effect = potion.getEffects().get(0).getPotion();
            TextureAtlasSprite sprite = mc.getPotionSpriteUploader().getSprite(effect);
            mc.getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());
            blit(matrixStack, x, y, 0, 18, 18, sprite);
        }
        // render cooldown
        if (cooldown > 0) {

            if (DavesPotioneering.DEBUG)
                Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, cooldown + "", x, y - 20, 0xff0000);

            int w = 18;
            int scale = getScaledCooldown(w, cooldown);
            AbstractGui.fill(matrixStack, x, y + w - scale, x + 18, y + w, 0x7fffffff);
        }
    }

    private static void bind(ResourceLocation res) {
        Minecraft.getInstance().getTextureManager().bindTexture(res);
    }

    private int getScaledCooldown(float pixels, float cooldown) {
        float totalCooldown = ModConfig.Server.gauntlet_cooldown.get();

        if (totalCooldown != 0) {
            float result = cooldown * pixels / totalCooldown;
            return Math.round(result);
        }

        return 0;
    }

    public static void refreshPosition() {
        x = ModConfig.Client.gauntlet_hud_x.get();
        y = ModConfig.Client.gauntlet_hud_y.get();
        preset = ModConfig.Client.gauntlet_hud_preset.get();
    }

    public static void forwardCycle() {
        forwardCycle = true;
    }

    public static void backwardCycle() {
        backwardCycle = true;
    }

    public enum HudPresets {
        TOP_LEFT,
        TOP_RIGHT,
        BTM_LEFT,
        BTM_RIGHT,
        ABOVE_HOTBAR,
        FREE_MOVE
    }
}
