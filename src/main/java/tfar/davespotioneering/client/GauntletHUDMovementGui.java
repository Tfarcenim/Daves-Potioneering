package tfar.davespotioneering.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import tfar.davespotioneering.ModConfig;

public class GauntletHUDMovementGui extends Screen {

    protected GauntletHUDMovementGui() {
        super(new TextComponent(""));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        minecraft.font.drawShadow(matrixStack, new TranslatableComponent("davespotioneering.gui.moveGauntletHUD"), 6, 5, ChatFormatting.WHITE.getColor());
    }

    public static final String KEY = "davespotioneering.gui.moveGauntletHUD.preset";

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new Button(5, 15, 75, 20, new TranslatableComponent(KEY + GauntletHUD.HudPresets.TOP_LEFT.ordinal()), button -> {
            GauntletHUD.x = 5;
            GauntletHUD.y = 5;
            GauntletHUD.preset = GauntletHUD.HudPresets.TOP_LEFT;
        }));
        addRenderableWidget(new Button(85, 15, 75, 20, new TranslatableComponent(KEY + GauntletHUD.HudPresets.TOP_RIGHT.ordinal()), button -> {
            GauntletHUD.x = width - GauntletHUD.TEX_WIDTH - 5;
            GauntletHUD.y = 5;
            GauntletHUD.preset = GauntletHUD.HudPresets.TOP_RIGHT;
        }));
        addRenderableWidget(new Button(165, 15, 75, 20, new TranslatableComponent(KEY + GauntletHUD.HudPresets.BTM_LEFT.ordinal()), button -> {
            GauntletHUD.x = 5;
            GauntletHUD.y = height - GauntletHUD.TEX_HEIGHT - 5;
            GauntletHUD.preset = GauntletHUD.HudPresets.BTM_LEFT;
        }));
        addRenderableWidget(new Button(245, 15, 75, 20, new TranslatableComponent(KEY + GauntletHUD.HudPresets.BTM_RIGHT.ordinal()), button -> {
            GauntletHUD.x = width - GauntletHUD.TEX_WIDTH - 5;
            GauntletHUD.y = height - GauntletHUD.TEX_HEIGHT - 5;
            GauntletHUD.preset = GauntletHUD.HudPresets.BTM_RIGHT;
        }));
        addRenderableWidget(new Button(325, 15, 75, 20, new TranslatableComponent(KEY + GauntletHUD.HudPresets.ABOVE_HOTBAR.ordinal()), button -> {
            GauntletHUD.preset = GauntletHUD.HudPresets.ABOVE_HOTBAR;
        }));
    }

    public static int getFixedPositionValue(int value, boolean isWidth) {
        return isWidth ? value*2-Minecraft.getInstance().getWindow().getGuiScaledWidth() : value-Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            GauntletHUD.x = (int)mouseX;
            GauntletHUD.y = (int) mouseY;
            GauntletHUD.preset = GauntletHUD.HudPresets.FREE_MOVE;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            ModConfig.Client.gauntlet_hud_x.set(GauntletHUD.x);
            ModConfig.Client.gauntlet_hud_y.set(GauntletHUD.y);
            ModConfig.Client.gauntlet_hud_preset.set(GauntletHUD.preset);
            GauntletHUD.refreshPosition();
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(null);
        Minecraft.getInstance().setScreen(new GauntletHUDMovementGui());
    }
}
