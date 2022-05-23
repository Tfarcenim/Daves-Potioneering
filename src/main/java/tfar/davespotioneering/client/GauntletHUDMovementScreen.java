package tfar.davespotioneering.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import tfar.davespotioneering.config.ClothConfig;

public class GauntletHUDMovementScreen extends Screen {
    private final GauntletHUD hud = new GauntletHUD();

    private int x;
    private int y;
    private GauntletHUD.HudPreset preset;

    protected GauntletHUDMovementScreen() {
        super(new LiteralText(""));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        client.textRenderer.drawWithShadow(matrixStack, new TranslatableText("davespotioneering.gui.moveGauntletHUD"), 6, 5, Formatting.WHITE.getColorValue());
        GauntletHUD.render(matrixStack);
    }

    public static final String KEY = "davespotioneering.gui.moveGauntletHUD.preset";

    @Override
    protected void init() {
        super.init();
        addButton(new ButtonWidget(5, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPreset.TOP_LEFT.ordinal()), (button) -> {
            x = getFixedPositionValue(5, true);
            y = getFixedPositionValue(5, false);
            preset = GauntletHUD.HudPreset.TOP_LEFT;
        }));
        addButton(new ButtonWidget(85, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPreset.TOP_RIGHT.ordinal()), (button) -> {
            x = getFixedPositionValue(width - 120 - 5, true);
            y = getFixedPositionValue(5, false);
            preset = GauntletHUD.HudPreset.TOP_RIGHT;
        }));
        addButton(new ButtonWidget(165, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPreset.BTM_LEFT.ordinal()), (button) -> {
            x = getFixedPositionValue(5, true);
            y = getFixedPositionValue(height - 45 - 5, false);
            preset = GauntletHUD.HudPreset.BTM_LEFT;
        }));
        addButton(new ButtonWidget(245, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPreset.BTM_RIGHT.ordinal()), (button) -> {
            x = getFixedPositionValue(width - 120 - 5, true);
            y = getFixedPositionValue(height - 45 - 5, false);
            preset = GauntletHUD.HudPreset.BTM_RIGHT;
        }));
        addButton(new ButtonWidget(325, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPreset.ABOVE_HOTBAR.ordinal()), (button) -> {
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
            ClothConfig.gauntlet_hud_x = x;
            ClothConfig.gauntlet_hud_y = y;
            ClothConfig.gauntlet_hud_preset = preset;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public static void open() {
        MinecraftClient.getInstance().openScreen(null);
        MinecraftClient.getInstance().openScreen(new GauntletHUDMovementScreen());
    }
}
