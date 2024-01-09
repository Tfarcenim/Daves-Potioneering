package tfar.davespotioneering.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.platform.Services;

public class ModBlockEntityTypes {

    public static final BlockEntityType<CAdvancedBrewingStandBlockEntity> COMPOUND_BREWING_STAND = BlockEntityType.Builder
            .of(Services.PLATFORM::makeAdvancedBrewingStand, ModBlocks.COMPOUND_BREWING_STAND)
            .build(null);
    public static final BlockEntityType<ReinforcedCauldronBlockEntity> REINFORCED_CAULDRON = BlockEntityType.Builder.of(ReinforcedCauldronBlockEntity::new,ModBlocks.REINFORCED_WATER_CAULDRON).build(null);
    public static final BlockEntityType<PotionInjectorBlockEntity> POTION_INJECTOR = BlockEntityType.Builder.of(PotionInjectorBlockEntity::new,ModBlocks.POTION_INJECTOR).build(null);
}
