package tfar.davespotioneering.init;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModBlockEntityTypes {

    private static List<BlockEntityType<?>> MOD_BLOCK_ENTITY_TYPES;

    public static final BlockEntityType<AdvancedBrewingStandBlockEntity> COMPOUND_BREWING_STAND = BlockEntityType.Builder.create(AdvancedBrewingStandBlockEntity::new,ModBlocks.COMPOUND_BREWING_STAND).build(null);
    public static final BlockEntityType<ReinforcedCauldronBlockEntity> REINFORCED_CAULDRON = BlockEntityType.Builder.create(ReinforcedCauldronBlockEntity::new,ModBlocks.REINFORCED_WATER_CAULDRON).build(null);
    public static final BlockEntityType<PotionInjectorBlockEntity> POTION_INJECTOR = BlockEntityType.Builder.create(PotionInjectorBlockEntity::new,ModBlocks.POTION_INJECTOR).build(null);

    public static void register() {
        for (Field field : ModBlockEntityTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof BlockEntityType) {
                    Registry.register(Registry.BLOCK_ENTITY_TYPE,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(BlockEntityType<?>) o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
