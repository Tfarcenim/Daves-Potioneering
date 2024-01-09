package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.button.InjectButton;
import tfar.davespotioneering.client.button.StripButton;
import tfar.davespotioneering.menu.CPotionInjectorMenu;
import tfar.davespotioneering.net.C2SPotionInjector;
import tfar.davespotioneering.net.PacketHandler;

public class PotionInjectorScreen extends AbstractContainerScreen<CPotionInjectorMenu> {
    public PotionInjectorScreen(CPotionInjectorMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        imageHeight+=30;
        inventoryLabelY += 26;
    }

    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(DavesPotioneering.MODID,"textures/gui/potion_injector.png");

    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos + 47;
        int y = topPos + 76;
        int w = 24;
        addRenderableWidget(new StripButton(x,y,36,20,Component.literal("Strip"),this::strip){
            @Override
            public void playDownSound(SoundManager handler) {

                CPotionInjectorMenu.SoundTy soundTy = menu.getSound(false);
                SoundEvent soundEvent;

                switch (soundTy) {
                    case BOTH:soundEvent = SoundEvents.BOTTLE_FILL;break;
                    case BLAZE:soundEvent = SoundEvents.BLAZE_SHOOT;break;
                    case NONE: default:
                        soundEvent = SoundEvents.UI_BUTTON_CLICK.get();
                }

                handler.play(SimpleSoundInstance.forUI(soundEvent, 1.0F));
            }
        });
        addRenderableWidget(new InjectButton(x + 46,y,36,20,Component.literal("Inject"),this::inject){
            @Override
            public void playDownSound(SoundManager handler) {

                CPotionInjectorMenu.SoundTy soundTy = menu.getSound(true);
                SoundEvent soundEvent = switch (soundTy) {
                    case BOTH -> SoundEvents.BREWING_STAND_BREW;
                    case BLAZE -> SoundEvents.BLAZE_SHOOT;
                    case NONE  -> SoundEvents.UI_BUTTON_CLICK.get();
                };

                handler.play(SimpleSoundInstance.forUI(soundEvent, 1.0F));
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
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI_TEXTURES, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
