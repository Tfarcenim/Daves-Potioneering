package tfar.davespotioneering.client;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tfar.davespotioneering.platform.Services;

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
            setHudPos(5,5,HudPreset.TOP_LEFT);
        }).pos(5, 15).size( 75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + HudPreset.TOP_RIGHT.ordinal()),(button) -> {
            setHudPos(width - GauntletHUDCommon.TEX_WIDTH - 5,5,HudPreset.TOP_RIGHT);
        }).pos(85, 15).size(75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + HudPreset.BTM_LEFT.ordinal()), (button) -> {
            setHudPos(5,height - GauntletHUDCommon.TEX_HEIGHT - 5,HudPreset.BTM_LEFT);
        }).pos(165, 15).size(75, 20).build());
        addRenderableWidget(Button.builder( Component.translatable(KEY + HudPreset.BTM_RIGHT.ordinal()), (button) -> {
            setHudPos(width - GauntletHUDCommon.TEX_WIDTH - 5,height - GauntletHUDCommon.TEX_HEIGHT - 5,HudPreset.BTM_RIGHT);
        }).pos(245, 15).size(75, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(KEY + HudPreset.ABOVE_HOTBAR.ordinal()),
                (button) -> Services.PLATFORM.setPreset(HudPreset.ABOVE_HOTBAR)).pos(325, 15).size(75, 20).build());
    }

    protected void setHudPos(int x,int y,HudPreset preset) {
        Services.PLATFORM.setGauntletHudX(x);
        Services.PLATFORM.setGauntletHudY(y);
        Services.PLATFORM.setPreset(preset);
    }

    private long lastUpdated = Util.getEpochMillis();

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && getChildAt(mouseX,mouseY).isEmpty()) {// do not attempt to drag when hovering over a button!
            // hudX = (int) mouseX;
            // hudY = (int) mouseY;
            if (Util.getEpochMillis() - lastUpdated >= 160) {
                lastUpdated = Util.getEpochMillis();
                setHudPos((int) mouseX, (int) mouseY,HudPreset.FREE_MOVE);
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(null);
        Minecraft.getInstance().setScreen(new GauntletHUDMovementScreen());
    }
}
