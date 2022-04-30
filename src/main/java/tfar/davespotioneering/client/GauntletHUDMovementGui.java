package tfar.davespotioneering.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;
import tfar.davespotioneering.ModConfig;

public class GauntletHUDMovementGui extends Screen {
    private final GauntletHUD hud = new GauntletHUD();

    protected GauntletHUDMovementGui() {
        super(new StringTextComponent(""));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        minecraft.font.drawShadow(matrixStack, new TranslationTextComponent("davespotioneering.gui.moveGauntletHUD"), 6, 5, TextFormatting.WHITE.getColor());
        hud.render(matrixStack);
    }

    public static final String KEY = "davespotioneering.gui.moveGauntletHUD.preset";

    @Override
    protected void init() {
        super.init();
        addButton(new Button(5, 15, 75, 20, new TranslationTextComponent(KEY + GauntletHUD.HudPresets.TOP_LEFT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(5, true);
            hud.y = getFixedPositionValue(5, false);
            hud.preset = GauntletHUD.HudPresets.TOP_LEFT;
        }));
        addButton(new Button(85, 15, 75, 20, new TranslationTextComponent(KEY + GauntletHUD.HudPresets.TOP_RIGHT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(width - 120 - 5, true);
            hud.y = getFixedPositionValue(5, false);
            hud.preset = GauntletHUD.HudPresets.TOP_RIGHT;
        }));
        addButton(new Button(165, 15, 75, 20, new TranslationTextComponent(KEY + GauntletHUD.HudPresets.BTM_LEFT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(5, true);
            hud.y = getFixedPositionValue(height - 45 - 5, false);
            hud.preset = GauntletHUD.HudPresets.BTM_LEFT;
        }));
        addButton(new Button(245, 15, 75, 20, new TranslationTextComponent(KEY + GauntletHUD.HudPresets.BTM_RIGHT.ordinal()), (button) -> {
            hud.x = getFixedPositionValue(width - 120 - 5, true);
            hud.y = getFixedPositionValue(height - 45 - 5, false);
            hud.preset = GauntletHUD.HudPresets.BTM_RIGHT;
        }));
        addButton(new Button(325, 15, 75, 20, new TranslationTextComponent(KEY + GauntletHUD.HudPresets.ABOVE_HOTBAR.ordinal()), (button) -> {
            if (minecraft != null && minecraft.player != null && minecraft.player.isCreative()) {
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
        return isWidth ? value*2-Minecraft.getInstance().getWindow().getGuiScaledWidth() : value-Minecraft.getInstance().getWindow().getGuiScaledHeight();
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
            ModConfig.Client.gauntlet_hud_x.set(hud.x);
            ModConfig.Client.gauntlet_hud_y.set(hud.y);
            ModConfig.Client.gauntlet_hud_preset.set(hud.preset);
            hud.refreshPosition();
            GauntletHUD.hudInstance.refreshPosition();
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(null);
        Minecraft.getInstance().setScreen(new GauntletHUDMovementGui());
    }
}
