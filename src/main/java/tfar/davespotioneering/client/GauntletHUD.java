package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.GuiAccess;

public class GauntletHUD implements IIngameOverlay {
    public static final String GAUNTLET_ICON_LOC = "textures/gauntlet_icons/";

    public static ResourceLocation getGauntletIconLoc(String fileName) {
        return new ResourceLocation(DavesPotioneering.MODID, GAUNTLET_ICON_LOC + fileName + ".png");
    }

    static final int TEX_HEIGHT = 41;
    static final int TEX_WIDTH = 121;

    private static Potion activePotion = null;
    private static Potion prePotion = null;
    private static Potion postPotion = null;
    private static final ResourceLocation hud = getGauntletIconLoc("hud");
    private static final ResourceLocation unknown = getGauntletIconLoc("unknown");
    private static final ResourceLocation turtle_master = getGauntletIconLoc("turtle_master");

    public static int x = ModConfig.Client.gauntlet_hud_x.get();
    public static int y = ModConfig.Client.gauntlet_hud_y.get();
    public static HudPresets preset = ModConfig.Client.gauntlet_hud_preset.get();

    public static int[] cooldowns = new int[6];

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

    private static void renderPotion(Potion potion, PoseStack matrixStack, int x, int y, int cooldown) {
        if (potion == null) return;
        if (potion.getEffects().isEmpty()) return;

        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {
            if (Registry.POTION.getKey(potion).toString().contains("turtle_master")) {
                bind(turtle_master);
            } else if (mc.getResourceManager().hasResource(getGauntletIconLoc(Registry.POTION.getKey(potion).getPath()))) {
                bind(getGauntletIconLoc(Registry.POTION.getKey(potion).toString()));
            } else {
                bind(unknown);
            }
            GuiComponent.blit(matrixStack, x, y, mc.gui.getBlitOffset(), 0, 0, 18, 18, 18, 18);
        } else {
            MobEffect effect = potion.getEffects().get(0).getEffect();
            TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effect);
            bind(sprite.atlas().location());
            GuiComponent.blit(matrixStack, x, y, 0, 18, 18, sprite);
        }

        // render cooldown
        if (cooldown > 0) {

            if (mc.options.advancedItemTooltips)
                Minecraft.getInstance().font.drawShadow(matrixStack, cooldown + "", x, y - 20, 0xff0000);

            int w = 18;
            int scale = getScaledCooldown(w, cooldown);
            GuiComponent.fill(matrixStack, x, y + w - scale, x + 18, y + w, 0x7fffffff);
        }
    }


    private static void bind(ResourceLocation res) {
        RenderSystem.setShaderTexture(0, res);
    }

    private static int getScaledCooldown(float pixels, float cooldown) {
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

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        // only renders when the hotbar renders
        //            if (Minecraft.getInstance().currentScreen != null) return;
        // get player from client
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();
        // check if holding gauntlet
        if (g.getItem() instanceof GauntletItem) {
            // get nbt
            CompoundTag info = player.getMainHandItem().getOrCreateTag().getCompound(GauntletItem.INFO);
            Potion[] potions = GauntletItem.getPotionsFromNBT(info);

            RenderSystem.setShaderColor(1, 1, 1, 1);
            bind(hud);

            if (preset == HudPresets.ABOVE_HOTBAR) {
                x = (screenWidth - TEX_WIDTH) / 2;
                y = screenHeight - Math.min(gui.left_height, gui.right_height) - TEX_HEIGHT;
                if (((GuiAccess) gui).getToolHighlightTimer() > 0) {
                    y -= 10;
                }
            }

            int yOffset;

            int xFixed = x;
            int yFixed = y;

            if (forwardCycle) {
                cooldown--;
                yOffset = 2;
                if (cooldown <= 0) {
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                    forwardCycle = false;
                    cooldown = maxCooldown;
                }
            } else if (backwardCycle) {
                cooldown--;
                yOffset = 1;
                if (cooldown <= 0) {
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                    backwardCycle = false;
                    cooldown = maxCooldown;
                }
            } else {
                yOffset = 0;
            }
            GuiComponent.blit(poseStack, xFixed, yFixed, gui.getBlitOffset(), 0, 1 + 43 * yOffset, TEX_WIDTH, TEX_HEIGHT, 128, 128);

            int active = info.getInt(GauntletItem.ACTIVE_POTION);

            int prev = active > 0 ? active - 1 : GauntletItem.SLOTS - 1;
            int next = active < GauntletItem.SLOTS - 1 ? active + 1 : 0;

            renderPotion(prePotion, poseStack, xFixed + 3, yFixed + 21, cooldowns[prev]);
            renderPotion(activePotion, poseStack, xFixed + 51, yFixed + 5, cooldowns[active]);
            renderPotion(postPotion, poseStack, xFixed + 99, yFixed + 21, cooldowns[next]);

            if (potions == null) {
                // reset
                GauntletHUD.init(null, null, null);
                return;
            }
            GauntletHUD.init(potions[0], potions[1], potions[2]);
        }
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
