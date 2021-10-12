package tfar.davespotioneering.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.TieredItem;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModContainerTypes;

public class ClientEvents {

    public static void playSound(PlaySoundEvent event) {
        if (event.getName().equals(SoundEvents.BLOCK_BREWING_STAND_BREW.getName().getPath()) && !ModConfig.Client.play_block_brewing_stand_brew.get()) {
            event.setResultSound(null);
        }
    }

    public static void onKeyPress(InputEvent.KeyInputEvent e) {
    }

    public static void tooltips(ItemTooltipEvent e) {
        ItemStack stack = e.getItemStack();
        if (stack.getItem() instanceof PotionItem) {
            if (Util.isMilkified(stack)) {
                e.getToolTip().add(new StringTextComponent("Milkified"));
            }
        }

        if (stack.getItem() instanceof TieredItem && PotionUtils.getPotionFromItem(stack) != Potions.EMPTY) {
            e.getToolTip().add(new StringTextComponent("Coated with"));
            PotionUtils.addPotionTooltip(stack, e.getToolTip(), 0.125F);
            e.getToolTip().add(new StringTextComponent("Uses: "+stack.getTag().getInt("uses")));
        }
    }

    public static void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::playSound);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::tooltips);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onKeyPress);
        RenderTypeLookup.setRenderLayer(ModBlocks.ADVANCED_BREWING_STAND, RenderType.getCutoutMipped());
        ScreenManager.registerFactory(ModContainerTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.ALCHEMICAL_GAUNTLET, AlchemicalGauntletScreen::new);

        Minecraft.getInstance().getBlockColors().register((state, reader, pos, index) -> {
            if (pos != null) {
                TileEntity blockEntity = reader.getTileEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity) {
                    return ((ReinforcedCauldronBlockEntity)blockEntity).getColor();
                }
            }
            return 0xffffff;
        },ModBlocks.REINFORCED_CAULDRON);
    }
}
