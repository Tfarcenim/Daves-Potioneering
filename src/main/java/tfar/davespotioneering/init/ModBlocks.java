package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.AdvancedBrewingStandBlock;
import tfar.davespotioneering.block.MagicLecternBlock;
import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModBlocks {

    private static List<Block> MOD_BLOCKS;

    public static final Block COMPOUND_BREWING_STAND = new AdvancedBrewingStandBlock(BlockBehaviour.Properties.of(Material.METAL)
            .requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion());

    public static final Block REINFORCED_CAULDRON = new ReinforcedCauldronBlock(BlockBehaviour.Properties.of(Material.METAL)
            .requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion());

    public static final Block MAGIC_LECTERN = new MagicLecternBlock(BlockBehaviour.Properties.copy(Blocks.LECTERN));

    public static final Block POTION_INJECTOR = new PotionInjectorBlock(BlockBehaviour.Properties.copy(Blocks.FLETCHING_TABLE).noOcclusion());


    public static void register() {
        for (Field field : ModBlocks.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof Block) {
                    Registry.register(Registry.BLOCK,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(Block)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
