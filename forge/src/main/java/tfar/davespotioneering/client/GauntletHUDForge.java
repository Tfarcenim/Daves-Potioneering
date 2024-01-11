package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.item.CGauntletItem;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.GuiAccess;
import tfar.davespotioneering.platform.Services;

public class GauntletHUDForge implements IGuiOverlay {

    private Potion activePotion = null;
    private Potion prePotion = null;
    private Potion postPotion = null;

    private static boolean backwardCycle = false;

    public void init(Potion activePotion, Potion prePotion, Potion postPotion) {
        this.activePotion = activePotion;
        this.prePotion = prePotion;
        this.postPotion = postPotion;
    }

    public static void bake(ModConfigEvent e) {
        if (e.getConfig().getModId().equals(DavesPotioneering.MODID)) {
            GauntletHUDCommon.x = ModConfig.Client.gauntlet_hud_x.get();
            GauntletHUDCommon.y = ModConfig.Client.gauntlet_hud_y.get();
            GauntletHUDCommon.preset = ModConfig.Client.gauntlet_hud_preset.get();
        }
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
            } else if (GauntletHUDCommon.mc.getResourceManager().getResource(GauntletHUDCommon.getGauntletIconLoc(name)).isPresent()) {
                resourceLocation = GauntletHUDCommon.getGauntletIconLoc(name);
            } else {
                resourceLocation = GauntletHUDCommon.getGauntletIconLoc("unknown");
            }
            matrixStack.blit(resourceLocation, x, y, 0, 0, 0, 18, 18, 18, 18);
        } else {
            MobEffect effect = potion.getEffects().get(0).getEffect();
            TextureAtlasSprite sprite = GauntletHUDCommon.mc.getMobEffectTextures().get(effect);
            matrixStack.blit(x, y, 0, 18, 18, sprite);
        }


        // render cooldown
        if (cooldown > 0) {

            if (Services.PLATFORM.isDevelopmentEnvironment())
                matrixStack.drawString(GauntletHUDCommon.mc.font, cooldown + "", x, y - 20, 0xff0000);

            int w = 18;
            int scale = getScaledCooldown(w, cooldown);
            matrixStack.fill(x, y + w - scale, x + 18, y + w, 0x7fffffff);
        }
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
        GauntletHUDCommon.x = ModConfig.Client.gauntlet_hud_x.get();
        GauntletHUDCommon.y = ModConfig.Client.gauntlet_hud_y.get();
        GauntletHUDCommon.preset = ModConfig.Client.gauntlet_hud_preset.get();
    }

    public static void forwardCycle() {
        GauntletHUDCommon.forwardCycle = true;
    }

    public static void backwardCycle() {
        backwardCycle = true;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics poseStack, float partialTick, int screenWidth, int screenHeight) {
        // only renders when the hotbar renders
        //            if (Minecraft.getInstance().currentScreen != null) return;
        // get player from client
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();
        // check if holding gauntlet
        if (g.getItem() instanceof GauntletItem) {
            // get nbt
            CompoundTag info = player.getMainHandItem().getOrCreateTag().getCompound(CGauntletItem.INFO);
            Potion[] potions = GauntletItem.getVisibleEffects(info);


            RenderSystem.setShaderColor(1, 1, 1, 1);

            if (GauntletHUDCommon.preset == HudPreset.ABOVE_HOTBAR) {
                GauntletHUDCommon.x = (screenWidth- GauntletHUDCommon.TEX_WIDTH) / 2;
                GauntletHUDCommon.y = screenHeight - Math.min(gui.leftHeight,gui.rightHeight) - GauntletHUDCommon.TEX_HEIGHT;
                if (((GuiAccess)gui).getToolHighlightTimer() > 0) {

                    GauntletHUDCommon.y -= 10;
                }
            }

            int yOffset;

            int xFixed = GauntletHUDCommon.x;
            int yFixed = GauntletHUDCommon.y;

            if (GauntletHUDCommon.forwardCycle) {
                GauntletHUDCommon.cooldown--;
                yOffset = 2;
                if (GauntletHUDCommon.cooldown <= 0) {
                    GauntletHUDCommon.mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                    GauntletHUDCommon.forwardCycle = false;
                    GauntletHUDCommon.cooldown = GauntletHUDCommon.maxCooldown;
                }
            } else if (backwardCycle) {
                GauntletHUDCommon.cooldown--;
                yOffset = 1;
                if (GauntletHUDCommon.cooldown <= 0) {
                    GauntletHUDCommon.mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                    backwardCycle = false;
                    GauntletHUDCommon.cooldown = GauntletHUDCommon.maxCooldown;
                }
            } else {
                yOffset = 0;
            }
            poseStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 1 + 43 * yOffset, GauntletHUDCommon.TEX_WIDTH, GauntletHUDCommon.TEX_HEIGHT, 128, 128);

            int active = info.getInt(CGauntletItem.ACTIVE_POTION);

            int prev = active > 0 ? active - 1 : CGauntletItem.SLOTS - 1;
            int next = active < CGauntletItem.SLOTS - 1 ? active + 1 : 0;

            renderPotion(prePotion, poseStack, xFixed + 3, yFixed + 21, GauntletHUDCommon.cooldowns[prev]);
            renderPotion(activePotion, poseStack, xFixed + 51, yFixed + 5, GauntletHUDCommon.cooldowns[active]);
            renderPotion(postPotion, poseStack, xFixed + 99, yFixed + 21, GauntletHUDCommon.cooldowns[next]);

            if (potions == null) {
                // reset
                init(null, null, null);
                return;
            }
            init(potions[0], potions[1], potions[2]);
        }
    }


}
