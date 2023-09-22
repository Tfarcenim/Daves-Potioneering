package tfar.davespotioneering.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tfar.davespotioneering.DavesPotioneering;

public class GauntletHUDMovementScreen extends Screen {

    private int x;
    private int y;
    private GauntletHUD.Preset preset;

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
        addRenderableWidget(Button.builder(Component.translatable(KEY + GauntletHUD.Preset.TOP_LEFT.ordinal()), (button) -> {
            x = 5;
            y = 5;
            preset = GauntletHUD.Preset.TOP_LEFT;
        }).pos(5, 15).size( 75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + GauntletHUD.Preset.TOP_RIGHT.ordinal()),(button) -> {
            x = width - GauntletHUD.TEX_WIDTH - 5;
            y = 5;
            preset = GauntletHUD.Preset.TOP_RIGHT;
        }).pos(85, 15).size(75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + GauntletHUD.Preset.BTM_LEFT.ordinal()), (button) -> {
            x = 5;
            y = height - GauntletHUD.TEX_HEIGHT - 5;
            preset = GauntletHUD.Preset.BTM_LEFT;
        }).pos(165, 15).size(75, 20).build());
        addRenderableWidget(Button.builder( Component.translatable(KEY + GauntletHUD.Preset.BTM_RIGHT.ordinal()), (button) -> {
            x = width - GauntletHUD.TEX_WIDTH - 5;
            y = height - GauntletHUD.TEX_HEIGHT - 5;
            preset = GauntletHUD.Preset.BTM_RIGHT;
        }).pos(245, 15).size(75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + GauntletHUD.Preset.ABOVE_HOTBAR.ordinal()),
                (button) -> preset = GauntletHUD.Preset.ABOVE_HOTBAR).pos(325, 15).size(75, 20).build());
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && getChildAt(mouseX,mouseY).isEmpty()) {// do not attempt to drag when hovering over a button!
            x = (int) mouseX;
            y = (int) mouseY;
            preset = GauntletHUD.Preset.FREE_MOVE;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            DavesPotioneering.CONFIG.gauntlet_hud_x = x;
            DavesPotioneering.CONFIG.gauntlet_hud_y = y;
            DavesPotioneering.CONFIG.gauntlet_hud_preset = preset;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(null);
        Minecraft.getInstance().setScreen(new GauntletHUDMovementScreen());
    }
}
