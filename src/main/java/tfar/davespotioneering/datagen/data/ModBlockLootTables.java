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
        registerDropSelfLootTable(ModBlocks.REINFORCED_CAULDRON);
        registerDropSelfLootTable(ModBlocks.ADVANCED_BREWING_STAND);
        registerDropSelfLootTable(ModBlocks.MAGIC_LECTERN);
        registerDropSelfLootTable(ModBlocks.POTION_INJECTOR);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.getRegistryName().getNamespace().equals(DavesPotioneering.MODID))
                .collect(Collectors.toList());
    }
}
