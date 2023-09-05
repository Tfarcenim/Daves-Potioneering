package tfar.davespotioneering.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.config.ClothConfig;

public class GauntletHUDMovementScreen extends Screen {

    private int x;
    private int y;
    private GauntletHUD.HudPreset preset;

    protected GauntletHUDMovementScreen() {
        super(Text.empty());
    }

    private static final Text info =Text.translatable("davespotioneering.gui.moveGauntletHUD");

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        GauntletHUD.render(matrixStack);
        client.textRenderer.drawWithShadow(matrixStack, info, 6, 5, Formatting.WHITE.getColorValue());
    }

    public static final String KEY = "davespotioneering.gui.moveGauntletHUD.preset";

    @Override
    protected void init() {
        super.init();

        int y1 = 20;

        int x1 = width / 2 - 40;

        int dist = 80;

        addDrawableChild(new ButtonWidget(x1 - 2 * dist, y1, 75, 20, Text.translatable(KEY + GauntletHUD.HudPreset.TOP_LEFT.ordinal()), (button) -> {
            x = getFixedPositionValue(5, true);
            y = getFixedPositionValue(5, false);
            preset = GauntletHUD.HudPreset.TOP_LEFT;
        }));
        addDrawableChild(new ButtonWidget(x1 - dist, y1, 75, 20, Text.translatable(KEY + GauntletHUD.HudPreset.TOP_RIGHT.ordinal()), (button) -> {
            x = getFixedPositionValue(width - 120 - 5, true);
            y = getFixedPositionValue(5, false);
            preset = GauntletHUD.HudPreset.TOP_RIGHT;
        }));
        addDrawableChild(new ButtonWidget(x1, y1, 75, 20, Text.translatable(KEY + GauntletHUD.HudPreset.BTM_LEFT.ordinal()), (button) -> {
            x = getFixedPositionValue(5, true);
            y = getFixedPositionValue(height - 45 - 5, false);
            preset = GauntletHUD.HudPreset.BTM_LEFT;
        }));

        addDrawableChild(new ButtonWidget(x1 + dist, y1, 75, 20, Text.translatable(KEY + GauntletHUD.HudPreset.BTM_RIGHT.ordinal()), (button) -> {
            x = getFixedPositionValue(width - 120 - 5, true);
            y = getFixedPositionValue(height - 45 - 5, false);
            preset = GauntletHUD.HudPreset.BTM_RIGHT;
        }));

        addDrawableChild(new ButtonWidget(x1 + 2 * dist, y1, 75, 20, Text.translatable(KEY + GauntletHUD.HudPreset.ABOVE_HOTBAR.ordinal()), (button) -> {
            if (client != null && client.player != null && client.player.isCreative()) {
                x = getFixedPositionValue((width - 120) / 2, true);
                y = getFixedPositionValue(height - 42 - 25, false);
            } else {
                x = getFixedPositionValue((width - 120) / 2, true);
                y = getFixedPositionValue(height - 42 - 40, false);
            }
            preset = GauntletHUD.HudPreset.ABOVE_HOTBAR;
        }));
    }

    public static int getFixedPositionValue(int value, boolean isWidth) {
        return isWidth ? value*2-MinecraftClient.getInstance().getWindow().getScaledWidth() : value-MinecraftClient.getInstance().getWindow().getScaledHeight();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            x = getFixedPositionValue((int) mouseX, true);
            y = getFixedPositionValue((int) mouseY, false);
            preset = GauntletHUD.HudPreset.FREE_MOVE;
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
        MinecraftClient.getInstance().setScreen(null);
        MinecraftClient.getInstance().setScreen(new GauntletHUDMovementScreen());
    }
}
