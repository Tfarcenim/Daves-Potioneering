package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.menu.PotionInjectorMenu;
import tfar.davespotioneering.net.C2SPotionInjector;

public class GauntletWorkstationScreen extends HandledScreen<PotionInjectorMenu> {
    public GauntletWorkstationScreen(PotionInjectorMenu screenContainer, PlayerInventory inv, Text titleIn) {
        super(screenContainer, inv, titleIn);
        backgroundHeight+=30;
        playerInventoryTitleY += 26;
    }

    private static final Identifier BREWING_STAND_GUI_TEXTURES = new Identifier(DavesPotioneering.MODID,"textures/gui/gauntlet_workstation.png");

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        int x = this.x + 47;
        int y = this.y + 76;
        int w = 24;
        addButton(new ButtonWidget(x,y,36,20,new LiteralText("Strip"),this::strip){
            @Override
            public void playDownSound(SoundManager handler) {

                PotionInjectorMenu.SoundTy soundTy = PotionInjectorMenu.SoundTy.BOTH;//handler.getSound(false);
                SoundEvent soundEvent;

                switch (soundTy) {
                    case BOTH:soundEvent = SoundEvents.ITEM_BOTTLE_FILL;break;
                    case BLAZE:soundEvent = SoundEvents.ENTITY_BLAZE_SHOOT;break;
                    case NONE: default:
                        soundEvent = SoundEvents.UI_BUTTON_CLICK;
                }

                handler.play(PositionedSoundInstance.master(soundEvent, 1.0F));
            }
        });
        addButton(new ButtonWidget(x + 46,y,36,20,new LiteralText("Inject"),this::inject){
            @Override
            public void playDownSound(SoundManager handler) {

                PotionInjectorMenu.SoundTy soundTy = PotionInjectorMenu.SoundTy.BOTH;//handler.getSound(true);
                SoundEvent soundEvent;

                switch (soundTy) {
                    case BOTH: soundEvent = SoundEvents.BLOCK_BREWING_STAND_BREW;break;
                    case BLAZE:soundEvent = SoundEvents.ENTITY_BLAZE_SHOOT;break;
                    case NONE: default:
                        soundEvent = SoundEvents.UI_BUTTON_CLICK;
                }

                handler.play(PositionedSoundInstance.master(soundEvent, 1.0F));
            }
        });

    }

    private void inject(ButtonWidget b) {
        C2SPotionInjector.encode(0);
    }

    private void strip(ButtonWidget b) {
        C2SPotionInjector.encode(1);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrixStack, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
