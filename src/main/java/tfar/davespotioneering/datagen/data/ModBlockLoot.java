package tfar.davespotioneering.datagen.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModBlocks;

import java.util.stream.Collectors;

public class ModBlockLoot extends VanillaBlockLoot {

    @Override
    protected void generate() {
        dropSelf(ModBlocks.REINFORCED_CAULDRON);
        dropSelf(ModBlocks.COMPOUND_BREWING_STAND);
        dropSelf(ModBlocks.MAGIC_LECTERN);
        dropSelf(ModBlocks.POTION_INJECTOR);
        this.dropOther(ModBlocks.REINFORCED_WATER_CAULDRON, ModBlocks.REINFORCED_CAULDRON);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(DavesPotioneering.MODID))
                .collect(Collectors.toList());
    }
}
