package tfar.davespotioneering.init;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import tfar.davespotioneering.DavesPotioneering;

public class ModCreativeTab {

    public static CreativeModeTab DAVESPOTIONEERING = CreativeModeTab.builder()
            .title(Component.translatable("itemGroup."+ DavesPotioneering.MODID)).icon(() -> new ItemStack(ModItems.POTIONEER_GAUNTLET))
            .displayItems(((pParameters, pOutput) -> pOutput.acceptAll(ModItems.getAllItems().stream().map(ItemStack::new).toList()))).build();

}
