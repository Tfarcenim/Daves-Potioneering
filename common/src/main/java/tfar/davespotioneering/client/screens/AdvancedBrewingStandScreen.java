package tfar.davespotioneering.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.menu.CAdvancedBrewingStandMenu;

public class AdvancedBrewingStandScreen extends AbstractContainerScreen<CAdvancedBrewingStandMenu> {

    private static final ResourceLocation BREWING_STAND_GUI_TEXTURES = new ResourceLocation(DavesPotioneering.MODID,"textures/gui/compound_brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public AdvancedBrewingStandScreen(CAdvancedBrewingStandMenu p_i51097_1_, Inventory p_i51097_2_, Component p_i51097_3_) {
        super(p_i51097_1_, p_i51097_2_, p_i51097_3_);
        imageHeight += 26;
        this.inventoryLabelY += 28;
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, menu.getFuel()+"", 20, 62, 0x404040, false);
    }

    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(BREWING_STAND_GUI_TEXTURES,i, j, 0, 0, this.imageWidth, this.imageHeight);
        int fuel = this.menu.getFuel();
        int fuelWidth = Mth.clamp((18 * fuel + 20 - 1) / CAdvancedBrewingStandBlockEntity.FUEL_USES, 0, 18);

        int y1 = 42;

        if (fuelWidth > 0) {
            matrixStack.blit(BREWING_STAND_GUI_TEXTURES, i + 60, j + 28 + y1, 176, 29, fuelWidth, 4);
        }


        int brewTime = this.menu.getBrewTime();
        if (brewTime > 0) {
            int length = (int)(28.0F * (1.0F - (float)brewTime / CAdvancedBrewingStandBlockEntity.TIME));
            if (length > 0) {
                matrixStack.blit(BREWING_STAND_GUI_TEXTURES, i + 97, j + y1, 176, 0, 9, length);
            }

            length = BUBBLELENGTHS[brewTime / 2 % 7];
            if (length > 0) {
                matrixStack.blit(BREWING_STAND_GUI_TEXTURES, i + 63, j + y1 + 27 - length, 185, 29 - length, 12, length);
            }
        }
    }
}
