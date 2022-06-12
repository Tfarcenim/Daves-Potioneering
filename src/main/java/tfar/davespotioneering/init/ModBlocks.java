package tfar.davespotioneering.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModBlocks {

    private static List<Block> MOD_BLOCKS;

    public static final Block COMPOUND_BREWING_STAND = new AdvancedBrewingStandBlock(AbstractBlock.Settings.of(Material.METAL)
            .requiresTool().strength(0.5F).luminance(state -> 1).nonOpaque());

    public static final Block REINFORCED_CAULDRON = new ReinforcedCauldronBlock(AbstractBlock.Settings.of(Material.METAL)
            .requiresTool().strength(0.5F).luminance(state -> 1).nonOpaque());

    public static final Block REINFORCED_WATER_CAULDRON = new LayeredReinforcedCauldronBlock(AbstractBlock.Settings.copy(REINFORCED_CAULDRON));

    public static final Block MAGIC_LECTERN = new MagicLecternBlock(AbstractBlock.Settings.copy(Blocks.LECTERN));

    public static final Block POTION_INJECTOR = new PotionInjectorBlock(AbstractBlock.Settings.copy(Blocks.FLETCHING_TABLE).nonOpaque());


    public static void register() {
        for (Field field : ModBlocks.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof Block) {
                    Registry.register(Registry.BLOCK,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(Block)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
