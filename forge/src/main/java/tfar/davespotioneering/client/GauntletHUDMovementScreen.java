package tfar.davespotioneering.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tfar.davespotioneering.ModConfig;

public class GauntletHUDMovementScreen extends Screen {

    protected GauntletHUDMovementScreen() {
        super(Component.empty());
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.drawString(font,Component.translatable("davespotioneering.gui.moveGauntletHUD"), 6, 5, ChatFormatting.WHITE.getColor());
    }

    public static final String KEY = "davespotioneering.gui.moveGauntletHUD.preset";

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable(KEY + HudPreset.TOP_LEFT.ordinal()), (button) -> {
            GauntletHUDCommon.x = 5;
            GauntletHUDCommon.y = 5;
            GauntletHUDCommon.preset = HudPreset.TOP_LEFT;
        }).pos(5, 15).size( 75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + HudPreset.TOP_RIGHT.ordinal()),(button) -> {
            GauntletHUDCommon.x = width - GauntletHUDCommon.TEX_WIDTH - 5;
            GauntletHUDCommon.y = 5;
            GauntletHUDCommon.preset = HudPreset.TOP_RIGHT;
        }).pos(85, 15).size(75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + HudPreset.BTM_LEFT.ordinal()), (button) -> {
            GauntletHUDCommon.x = 5;
            GauntletHUDCommon.y = height - GauntletHUDCommon.TEX_HEIGHT - 5;
            GauntletHUDCommon.preset = HudPreset.BTM_LEFT;
        }).pos(165, 15).size(75, 20).build());
        addRenderableWidget(Button.builder( Component.translatable(KEY + HudPreset.BTM_RIGHT.ordinal()), (button) -> {
            GauntletHUDCommon.x = width - GauntletHUDCommon.TEX_WIDTH - 5;
            GauntletHUDCommon.y = height - GauntletHUDCommon.TEX_HEIGHT - 5;
            GauntletHUDCommon.preset = HudPreset.BTM_RIGHT;
        }).pos(245, 15).size(75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + HudPreset.ABOVE_HOTBAR.ordinal()),
                (button) -> GauntletHUDCommon.preset = HudPreset.ABOVE_HOTBAR).pos(325, 15).size(75, 20).build());
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && getChildAt(mouseX,mouseY).isEmpty()) {// do not attempt to drag when hovering over a button!
            GauntletHUDCommon.x = (int) mouseX;
            GauntletHUDCommon.y = (int) mouseY;
            GauntletHUDCommon.preset = HudPreset.FREE_MOVE;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            ModConfig.Client.gauntlet_hud_x.set(GauntletHUDCommon.x);
            ModConfig.Client.gauntlet_hud_y.set(GauntletHUDCommon.y);
            ModConfig.Client.gauntlet_hud_preset.set(GauntletHUDCommon.preset);
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(null);
        Minecraft.getInstance().setScreen(new GauntletHUDMovementScreen());
    }
}
