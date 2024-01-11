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
    private static final int maxCooldown = 40;
    private static int cooldown = maxCooldown;

    public static void render(GuiGraphics matrixStack) {
//        matrixStack.pushPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
     //   RenderSystem.setShaderTexture(0,hud_texture);

        Gui hud = GauntletHUDCommon.mc.gui;

        int fade = ((GuiAccess)hud).getToolHighlightTimer();

        int windowW = GauntletHUDCommon.mc.getWindow().getGuiScaledWidth();
        int windowH = GauntletHUDCommon.mc.getWindow().getGuiScaledHeight();

        int xFixed = Mth.clamp((windowW + DavesPotioneeringFabric.CONFIG.gauntlet_hud_x)/2, 0, windowW-120);
        int yFixed = Mth.clamp(windowH+ DavesPotioneeringFabric.CONFIG.gauntlet_hud_y, 0, windowH-GauntletHUDCommon.TEX_HEIGHT);


        if(DavesPotioneeringFabric.CONFIG.gauntlet_hud_preset == HudPreset.ABOVE_HOTBAR) {
            int height = GauntletHUDCommon.TEX_HEIGHT + 50;
            if (fade > 0) {
                height += 10;
            }
            yFixed = windowH - height;
        }

        if (GauntletHUDCommon.forwardCycle) {
            cooldown--;
            matrixStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 87, 120, GauntletHUDCommon.TEX_HEIGHT, 128, 128);
            if (cooldown <= 0) {
               GauntletHUDCommon.mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                GauntletHUDCommon.forwardCycle = false;
                cooldown = maxCooldown;
            }
        } else if (GauntletHUDCommon.backwardCycle) {
            cooldown--;
            matrixStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 44, 120, GauntletHUDCommon.TEX_HEIGHT, 128, 128);
            if (cooldown <= 0) {
                GauntletHUDCommon.mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GAUNTLET_SCROLL, 1.0F));
                GauntletHUDCommon.backwardCycle = false;
                cooldown = maxCooldown;
            }
        } else {
            matrixStack.blit(GauntletHUDCommon.hud,xFixed, yFixed, 0, 0, 1, 120, GauntletHUDCommon.TEX_HEIGHT, 128, 128);
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();

        CompoundTag info = g.getOrCreateTag().getCompound("info");
        GauntletHUDCommon.renderPotion(GauntletHUDCommon.prePotion, matrixStack, xFixed + 3, yFixed + 21, GauntletItemFabric.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")-1, player));
        GauntletHUDCommon.renderPotion(GauntletHUDCommon.activePotion, matrixStack, xFixed + 51, yFixed + 5, GauntletItemFabric.getCooldownFromPotionByIndex(info.getInt("activePotionIndex"),player));
        GauntletHUDCommon.renderPotion(GauntletHUDCommon.postPotion, matrixStack, xFixed + 99, yFixed + 21, GauntletItemFabric.getCooldownFromPotionByIndex(info.getInt("activePotionIndex")+1, player));
     //   matrixStack.popPose();
    }
}
