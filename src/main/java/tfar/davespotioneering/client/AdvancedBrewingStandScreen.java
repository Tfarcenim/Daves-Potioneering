package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

public class AdvancedBrewingStandScreen extends HandledScreen<AdvancedBrewingStandContainer> {

    private static final Identifier BREWING_STAND_GUI_TEXTURES = new Identifier(DavesPotioneering.MODID,"textures/gui/compound_brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public AdvancedBrewingStandScreen(AdvancedBrewingStandContainer p_i51097_1_, PlayerInventory p_i51097_2_, Text p_i51097_3_) {
        super(p_i51097_1_, p_i51097_2_, p_i51097_3_);
        backgroundHeight += 26;
        this.playerInventoryTitleY += 28;
    }

    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrixStack, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int fuel = this.handler.getFuel();
        int l = MathHelper.clamp((18 * fuel + 20 - 1) / 20, 0, 18);

        int y1 = 42;

        if (l > 0) {
            this.drawTexture(matrixStack, i + 60, j + 28 + y1, 176, 29, l, 4);
        }


        int brewTime = this.handler.getBrewTime();
        if (brewTime > 0) {
            int length = (int)(28.0F * (1.0F - (float)brewTime / AdvancedBrewingStandBlockEntity.TIME));
            if (length > 0) {
                this.drawTexture(matrixStack, i + 97, j + y1, 176, 0, 9, length);
            }

            length = BUBBLELENGTHS[brewTime / 2 % 7];
            if (length > 0) {
                this.drawTexture(matrixStack, i + 63, j + y1 + 27 - length, 185, 29 - length, 12, length);
            }
        }

    }
}
