package tfar.davespotioneering.datagen.data;

import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraftforge.registries.ForgeRegistries;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModBlocks;

import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables {

    @Override
    protected void addTables() {
        dropSelf(ModBlocks.REINFORCED_CAULDRON);
        dropSelf(ModBlocks.COMPOUND_BREWING_STAND);
        dropSelf(ModBlocks.MAGIC_LECTERN);
        dropSelf(ModBlocks.POTION_INJECTOR);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.getRegistryName().getNamespace().equals(DavesPotioneering.MODID))
                .collect(Collectors.toList());
    }
}
