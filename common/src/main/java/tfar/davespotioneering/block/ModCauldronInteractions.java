package tfar.davespotioneering.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import tfar.davespotioneering.PotionUtils2;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.platform.Services;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Copied from @CauldronInteraction
 */
public class ModCauldronInteractions {

    public static final Map<Item,CauldronInteraction> WATER = CauldronInteraction.newInteractionMap();

     public static final Map<Item, CauldronInteraction> EMPTY = CauldronInteraction.newInteractionMap();
    static final Map<Item, CauldronInteraction> LAVA = CauldronInteraction.newInteractionMap();
    static final Map<Item, CauldronInteraction> POWDER_SNOW = CauldronInteraction.newInteractionMap();
    static final CauldronInteraction FILL_WATER = (state, level, pos, player, p_175687_, p_175688_) -> {
        InteractionResult interactionResult = CauldronInteraction.emptyBucket(level, pos, player, p_175687_, p_175688_, ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CReinforcedCauldronBlockEntity reinforcedCauldronBlock) {
            reinforcedCauldronBlock.setPotion(Potions.WATER);
        }
        return interactionResult;
    };

    static final CauldronInteraction FILL_MILK = (state, level, pos, player, hand, itemStack) -> {
        InteractionResult interaction  = CauldronInteraction.emptyBucket(level, pos, player, hand, itemStack, ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
        if (interaction == InteractionResult.CONSUME) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CReinforcedCauldronBlockEntity reinforcedCauldronBlock) {
                reinforcedCauldronBlock.setPotion(ModPotions.MILK);
            }
        }
        return interaction;
    };

    static final CauldronInteraction FILL_LAVA = (p_175676_, p_175677_, p_175678_, p_175679_, p_175680_, p_175681_) -> CauldronInteraction.emptyBucket(p_175677_, p_175678_, p_175679_, p_175680_, p_175681_, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA);
    static final CauldronInteraction FILL_POWDER_SNOW = (p_175669_, p_175670_, p_175671_, p_175672_, p_175673_, p_175674_) -> CauldronInteraction.emptyBucket(p_175670_, p_175671_, p_175672_, p_175673_, p_175674_, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);

    public static void reload() {
        clearAndDefault(WATER);
        clearAndDefault(EMPTY);
        clearAndDefault(LAVA);
        clearAndDefault(POWDER_SNOW);

        addDefaultInteractions(EMPTY);
        EMPTY.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState());

                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof CReinforcedCauldronBlockEntity reinforced) {
                    reinforced.setPotion(PotionUtils.getPotion(stack));
                    reinforced.setCustomEffects(PotionUtils.getCustomEffects(stack));
                    if (stack.hasTag() && stack.getTag().contains(PotionUtils.TAG_CUSTOM_POTION_COLOR)) {
                        reinforced.setCustomPotionColor(PotionUtils.getColor(stack));
                    }
                }

                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        });
        addDefaultInteractions(WATER);
        WATER.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> fillWaterBucket(state, level, pos, player, hand, stack, (p_175660_) -> p_175660_.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
        WATER.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide) {
                Item item = stack.getItem();

                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof CReinforcedCauldronBlockEntity reinforced) {
                    Potion potion = reinforced.getPotion();
                    List<MobEffectInstance> customEffects = reinforced.getCustomEffects();
                    Integer color = reinforced.getCustomPotionColor();
                    ItemStack potionItem = Items.POTION.getDefaultInstance();
                    PotionUtils.setPotion(potionItem, potion);
                    PotionUtils.setCustomEffects(potionItem,customEffects);
                    if (color != null) {
                        PotionUtils2.setCustomColor(potionItem,color);
                    }
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, potionItem));
                    if (potion != Potions.WATER || !reinforced.getCustomEffects().isEmpty())
                        CLayeredReinforcedCauldronBlock.lowerFillLevel0(state, level, pos);
                }

                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        });
        WATER.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
            if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) {
                if (!level.isClientSide) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof CReinforcedCauldronBlockEntity reinforced) {

                        if (reinforced.getPotion() != PotionUtils.getPotion(stack)) {
                            CLayeredReinforcedCauldronBlock.boom(level,pos);
                        } else {
                            level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
                            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                        }
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        });
        WATER.put(Items.LEATHER_BOOTS, CauldronInteraction.DYED_ITEM);
        WATER.put(Items.LEATHER_LEGGINGS, CauldronInteraction.DYED_ITEM);
        WATER.put(Items.LEATHER_CHESTPLATE, CauldronInteraction.DYED_ITEM);
        WATER.put(Items.LEATHER_HELMET, CauldronInteraction.DYED_ITEM);
        WATER.put(Items.LEATHER_HORSE_ARMOR, CauldronInteraction.DYED_ITEM);
        WATER.put(Items.WHITE_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.GRAY_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.BLACK_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.BLUE_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.BROWN_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.CYAN_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.GREEN_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.LIGHT_BLUE_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.LIGHT_GRAY_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.LIME_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.MAGENTA_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.ORANGE_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.PINK_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.PURPLE_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.RED_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.YELLOW_BANNER, CauldronInteraction.BANNER);
        WATER.put(Items.WHITE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.GRAY_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.BLACK_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.BLUE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.BROWN_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.CYAN_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.GREEN_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.LIME_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.MAGENTA_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.ORANGE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.PINK_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.PURPLE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.RED_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        WATER.put(Items.YELLOW_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);

        //custom//
        WATER.put(Items.DRAGON_BREATH,ModCauldronInteractions::dragonsBreath);
        WATER.put(Items.ARROW, (state, level, pos, player, stack, stack2) -> arrowCoating(state, level, pos, player, stack2));

        for (Item item : BuiltInRegistries.ITEM) {
            if (item.builtInRegistryHolder().is(ModItems.BLACKLISTED)) continue;//do not allow blacklisted items under any circumstances
            else if (item.builtInRegistryHolder().is(ModItems.WHITELISTED)) {
                WATER.put(item, (state, level, pos, player, stack, stack2) -> weaponCoating(state, level, pos, player, stack2));
            } else if (item.isEdible()) {
                WATER.put(item,ModCauldronInteractions::spikedFood);
            }
        }

        EMPTY.put(Items.MILK_BUCKET, FILL_MILK);
        //end//

        LAVA.put(Items.BUCKET, (p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_) -> fillBucket(p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_, new ItemStack(Items.LAVA_BUCKET), (p_175651_) -> true, SoundEvents.BUCKET_FILL_LAVA));
        addDefaultInteractions(LAVA);
        POWDER_SNOW.put(Items.BUCKET, (p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_) -> fillBucket(p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_, new ItemStack(Items.POWDER_SNOW_BUCKET), (p_175627_) -> p_175627_.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
        addDefaultInteractions(POWDER_SNOW);
    }

    static void clearAndDefault(Map<Item, CauldronInteraction> map) {
        map.clear();
        for (Item item : BuiltInRegistries.ITEM) {
            map.put(item,(blockState, level, blockPos, player, interactionHand, itemStack) -> InteractionResult.PASS);
        }
    }

    static void addDefaultInteractions(Map<Item, CauldronInteraction> map) {
    //    map.put(Items.LAVA_BUCKET, FILL_LAVA);
        map.put(Items.WATER_BUCKET, FILL_WATER);
 //       map.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
    }


    static InteractionResult fillBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack p_175642_, Predicate<BlockState> statePredicate, SoundEvent soundEvent) {
        if (!statePredicate.test(state)) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, p_175642_));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_CAULDRON.defaultBlockState());//patch
                level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    //milk exists

    static InteractionResult fillWaterBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, Predicate<BlockState> statePredicate, SoundEvent soundEvent) {
        if (!statePredicate.test(state)) {
            return InteractionResult.PASS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CReinforcedCauldronBlockEntity cauldronBlockEntity) {
                Potion potion = cauldronBlockEntity.getPotion();

                if (!canBucket(potion)) {
                    return InteractionResult.PASS;
                }

                if (!level.isClientSide) {
                    Item item = stack.getItem();
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, getBucket(potion)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_CAULDRON.defaultBlockState());//patch
                    level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    static boolean canBucket(Potion potion) {
        return potion == Potions.WATER || potion == ModPotions.MILK;
    }

    public static ItemStack getBucket(Potion potion) {
        if (potion == Potions.WATER) {
            return new ItemStack(Items.WATER_BUCKET);
        } else if (potion == ModPotions.MILK) {
            return new ItemStack(Items.MILK_BUCKET);
        }
        System.out.println("No bucket found for: "+potion);
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Nonnull
    static InteractionResult dragonsBreath(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_175715_, ItemStack stack) {
        if (state.getValue(CLayeredReinforcedCauldronBlock.DRAGONS_BREATH)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) {
                player.awardStat(Stats.USE_CAULDRON);
                stack.shrink(1);

                ItemStack stack1 = new ItemStack(Items.GLASS_BOTTLE);

                if (!player.getInventory().add(stack1)) {
                    player.drop(stack1, false);
                }
            }
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos,state.setValue(CLayeredReinforcedCauldronBlock.DRAGONS_BREATH,true));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nonnull
    static InteractionResult arrowCoating(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack) {
        CLayeredReinforcedCauldronBlock.handleArrowCoating(state,level,pos,player, stack);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nonnull
    static InteractionResult spikedFood(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_175715_, ItemStack stack) {
        if (!Services.PLATFORM.spikeFood()) return InteractionResult.PASS;
        CLayeredReinforcedCauldronBlock.handleFoodSpiking(state,level,pos,player,p_175715_,stack);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nonnull
    static InteractionResult weaponCoating(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack) {
        CLayeredReinforcedCauldronBlock.handleWeaponCoating(state,level,pos,player, stack);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
