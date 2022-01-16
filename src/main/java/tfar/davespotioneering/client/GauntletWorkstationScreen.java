package tfar.davespotioneering.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.menu.PotionInjectorMenu;
import tfar.davespotioneering.net.C2SPotionInjector;
import tfar.davespotioneering.net.PacketHandler;

public class GauntletWorkstationScreen extends ContainerScreen<PotionInjectorMenu> {
    public GauntletWorkstationScreen(PotionInjectorMenu screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        ySize+=30;
        playerInventoryTitleY += 26;
    }

    private static final ResourceLocation BREWING_STAND_GUI_TEXTURES = new ResourceLocation(DavesPotioneering.MODID,"textures/gui/gauntlet_workstation.png");

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        int x = guiLeft + 47;
        int y = guiTop + 76;
        int w = 24;
        addButton(new Button(x,y,36,20,new StringTextComponent("Strip"),this::strip){
            @Override
            public void playDownSound(SoundHandler handler) {
                handler.play(SimpleSound.master(container.blazeOnly(false) ? SoundEvents.ENTITY_BLAZE_SHOOT :
                        SoundEvents.ITEM_BOTTLE_FILL, 1.0F));
            }
        });
        addButton(new Button(x + 46,y,36,20,new StringTextComponent("Inject"),this::inject){
            @Override
            public void playDownSound(SoundHandler handler) {
                handler.play(SimpleSound.master(container.blazeOnly(true) ? SoundEvents.ENTITY_BLAZE_SHOOT :
                        SoundEvents.BLOCK_BREWING_STAND_BREW, 1.0F));
            }
        });

    }

    private void inject(Button b) {
        PacketHandler.INSTANCE.sendToServer(new C2SPotionInjector(0));
    }

    private void strip(Button b) {
        PacketHandler.INSTANCE.sendToServer(new C2SPotionInjector(1));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
    }
}
