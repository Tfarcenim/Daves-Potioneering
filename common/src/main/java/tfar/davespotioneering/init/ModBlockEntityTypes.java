package tfar.davespotioneering.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.blockentity.CPotionInjectorBlockEntity;
import tfar.davespotioneering.platform.Services;

public class ModBlockEntityTypes {

    public static final BlockEntityType<CAdvancedBrewingStandBlockEntity> COMPOUND_BREWING_STAND = BlockEntityType.Builder
            .of(Services.PLATFORM::makeAdvancedBrewingStand, ModBlocks.COMPOUND_BREWING_STAND)
            .build(null);
    public static final BlockEntityType<CReinforcedCauldronBlockEntity> REINFORCED_CAULDRON = BlockEntityType.Builder
            .of(Services.PLATFORM::makeReinforcedCauldron,ModBlocks.REINFORCED_WATER_CAULDRON).build(null);
    public static final BlockEntityType<CPotionInjectorBlockEntity> POTION_INJECTOR = BlockEntityType.Builder
            .of(Services.PLATFORM::makePotionInjector,ModBlocks.POTION_INJECTOR).build(null);
}
