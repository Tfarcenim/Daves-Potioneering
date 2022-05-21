package tfar.davespotioneering.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class GauntletHUDMovementGui extends Screen {
    private final GauntletHUD hud = new GauntletHUD();

    protected GauntletHUDMovementGui() {
        super(new LiteralText(""));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        client.textRenderer.drawWithShadow(matrixStack, new TranslatableText("davespotioneering.gui.moveGauntletHUD"), 6, 5, Formatting.WHITE.getColorValue());
        hud.render(matrixStack);
    }

    public static final String KEY = "davespotioneering.gui.moveGauntletHUD.preset";

    @Override
    protected void init() {
        super.init();
        addButton(new ButtonWidget(5, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPresets.TOP_LEFT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(5, true);
            hud.y = getFixedPositionValue(5, false);
            hud.preset = GauntletHUD.HudPresets.TOP_LEFT;
        }));
        addButton(new ButtonWidget(85, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPresets.TOP_RIGHT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(width - 120 - 5, true);
            hud.y = getFixedPositionValue(5, false);
            hud.preset = GauntletHUD.HudPresets.TOP_RIGHT;
        }));
        addButton(new ButtonWidget(165, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPresets.BTM_LEFT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(5, true);
            hud.y = getFixedPositionValue(height - 45 - 5, false);
            hud.preset = GauntletHUD.HudPresets.BTM_LEFT;
        }));
        addButton(new ButtonWidget(245, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPresets.BTM_RIGHT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(width - 120 - 5, true);
            hud.y = getFixedPositionValue(height - 45 - 5, false);
            hud.preset = GauntletHUD.HudPresets.BTM_RIGHT;
        }));
        addButton(new ButtonWidget(325, 15, 75, 20, new TranslatableText(KEY + GauntletHUD.HudPresets.ABOVE_HOTBAR.ordinal()), (button) -> {
            if (client != null && client.player != null && client.player.isCreative()) {
                hud.x = getFixedPositionValue((width - 120) / 2, true);
                hud.y = getFixedPositionValue(height - 42 - 25, false);
            } else {
                hud.x = getFixedPositionValue((width - 120) / 2, true);
                hud.y = getFixedPositionValue(height - 42 - 40, false);
            }
            hud.preset = GauntletHUD.HudPresets.ABOVE_HOTBAR;
        }));
    }

    public static int getFixedPositionValue(int value, boolean isWidth) {
        return isWidth ? value*2-MinecraftClient.getInstance().getWindow().getScaledWidth() : value-MinecraftClient.getInstance().getWindow().getScaledHeight();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            hud.x = getFixedPositionValue((int) mouseX, true);
            hud.y = getFixedPositionValue((int) mouseY, false);
            hud.preset = GauntletHUD.HudPresets.FREE_MOVE;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
      //      ModConfig.Client.client.set(hud.x);
      //      ModConfig.Client.gauntlet_hud_y.set(hud.y);
      //      ModConfig.Client.gauntlet_hud_preset.set(hud.preset);
            hud.refreshPosition();
            GauntletHUD.hudInstance.refreshPosition();
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public static void open() {
        MinecraftClient.getInstance().openScreen(null);
        MinecraftClient.getInstance().openScreen(new GauntletHUDMovementGui());
    }
}
