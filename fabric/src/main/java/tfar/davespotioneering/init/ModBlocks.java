package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModBlocks {

    private static List<Block> MOD_BLOCKS;

    public static final Block COMPOUND_BREWING_STAND = new CAdvancedBrewingStandBlock(BlockBehaviour.Properties.of()
            .requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion());

    public static final Block REINFORCED_CAULDRON = new CReinforcedCauldronBlock(BlockBehaviour.Properties.of()
            .requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion(),ModCauldronInteractions.EMPTY);

    public static final Block REINFORCED_WATER_CAULDRON = new CLayeredReinforcedCauldronBlock(BlockBehaviour.Properties.copy(REINFORCED_CAULDRON));

    public static final Block MAGIC_LECTERN = new LecternBlock(BlockBehaviour.Properties.copy(Blocks.LECTERN));

    public static final Block POTION_INJECTOR = new CPotionInjectorBlock(BlockBehaviour.Properties.copy(Blocks.FLETCHING_TABLE).noOcclusion());


    public static void register() {
        for (Field field : ModBlocks.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof Block) {
                    Registry.register(BuiltInRegistries.BLOCK,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(Block)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
