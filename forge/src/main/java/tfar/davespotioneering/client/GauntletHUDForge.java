package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
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

public class GauntletHUDForge implements IGuiOverlay {


    public static void bake(ModConfigEvent e) {
        if (e.getConfig().getModId().equals(DavesPotioneering.MODID)) {
            GauntletHUDCommon.x = ModConfig.Client.gauntlet_hud_x.get();
            GauntletHUDCommon.y = ModConfig.Client.gauntlet_hud_y.get();
            GauntletHUDCommon.preset = ModConfig.Client.gauntlet_hud_preset.get();
        }
    }

    public static void refreshPosition() {
        GauntletHUDCommon.x = ModConfig.Client.gauntlet_hud_x.get();
        GauntletHUDCommon.y = ModConfig.Client.gauntlet_hud_y.get();
        GauntletHUDCommon.preset = ModConfig.Client.gauntlet_hud_preset.get();
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
            } else if (GauntletHUDCommon.backwardCycle) {
                GauntletHUDCommon.cooldown--;
                yOffset = 1;
                if (GauntletHUDCommon.cooldown <= 0) {
                    GauntletHUDCommon.mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                    GauntletHUDCommon.backwardCycle = false;
                    GauntletHUDCommon.cooldown = GauntletHUDCommon.maxCooldown;
                }
            } else {
                yOffset = 0;
            }
            poseStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 1 + 43 * yOffset, GauntletHUDCommon.TEX_WIDTH, GauntletHUDCommon.TEX_HEIGHT, 128, 128);

            int active = info.getInt(CGauntletItem.ACTIVE_POTION);

            int prev = active > 0 ? active - 1 : CGauntletItem.SLOTS - 1;
            int next = active < CGauntletItem.SLOTS - 1 ? active + 1 : 0;

            GauntletHUDCommon.renderPotion(GauntletHUDCommon.prePotion, poseStack, xFixed + 3, yFixed + 21, GauntletHUDCommon.cooldowns[prev]);
            GauntletHUDCommon.renderPotion(GauntletHUDCommon.activePotion, poseStack, xFixed + 51, yFixed + 5, GauntletHUDCommon.cooldowns[active]);
            GauntletHUDCommon.renderPotion(GauntletHUDCommon.postPotion, poseStack, xFixed + 99, yFixed + 21, GauntletHUDCommon.cooldowns[next]);

            if (potions == null) {
                // reset
                GauntletHUDCommon.init(null, null, null);
                return;
            }
            GauntletHUDCommon.init(potions[0], potions[1], potions[2]);
        }
    }
}
