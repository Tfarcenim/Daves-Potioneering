package tfar.davespotioneering.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModBlockEntityTypes {

    private static List<BlockEntityType<?>> MOD_BLOCK_ENTITY_TYPES;

    public static final BlockEntityType<AdvancedBrewingStandBlockEntity> COMPOUND_BREWING_STAND = BlockEntityType.Builder.of(AdvancedBrewingStandBlockEntity::new,ModBlocks.COMPOUND_BREWING_STAND).build(null);
    public static final BlockEntityType<ReinforcedCauldronBlockEntity> REINFORCED_CAULDRON = BlockEntityType.Builder.of(ReinforcedCauldronBlockEntity::new,ModBlocks.REINFORCED_WATER_CAULDRON).build(null);
    public static final BlockEntityType<PotionInjectorBlockEntity> POTION_INJECTOR = BlockEntityType.Builder.of(PotionInjectorBlockEntity::new,ModBlocks.POTION_INJECTOR).build(null);

    public static void register() {
        for (Field field : ModBlockEntityTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof BlockEntityType) {
                    Registry.register(Registry.BLOCK_ENTITY_TYPE,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(BlockEntityType<?>) o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
