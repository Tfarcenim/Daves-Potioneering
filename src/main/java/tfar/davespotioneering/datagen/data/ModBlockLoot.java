package tfar.davespotioneering.datagen.data;

import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModBlocks;

import java.util.stream.Collectors;

public class ModBlockLoot extends BlockLoot {

    @Override
    protected void addTables() {
        dropSelf(ModBlocks.REINFORCED_CAULDRON);
        dropSelf(ModBlocks.COMPOUND_BREWING_STAND);
        dropSelf(ModBlocks.MAGIC_LECTERN);
        dropSelf(ModBlocks.POTION_INJECTOR);
        this.dropOther(ModBlocks.REINFORCED_WATER_CAULDRON, ModBlocks.REINFORCED_CAULDRON);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.getRegistryName().getNamespace().equals(DavesPotioneering.MODID))
                .collect(Collectors.toList());
    }
}
