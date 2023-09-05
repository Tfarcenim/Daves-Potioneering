package tfar.davespotioneering.block;

import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Predicate;

import static tfar.davespotioneering.block.LayeredReinforcedCauldronBlock.DRAGONS_BREATH;
/**
 * Copied from @CauldronBehavior
 */
public class ModCauldronInteractions {

        public static final Map<Item, CauldronBehavior> WATER = CauldronBehavior.createMap();

        public static final Map<Item, CauldronBehavior> EMPTY = CauldronBehavior.createMap();
        static final Map<Item, CauldronBehavior> LAVA = CauldronBehavior.createMap();
        static final Map<Item, CauldronBehavior> POWDER_SNOW = CauldronBehavior.createMap();
        static final CauldronBehavior FILL_WATER = (state, world, pos, player, p_175687_, p_175688_) -> {
            ActionResult actionResult = CauldronBehavior.fillCauldron(world, pos, player, p_175687_, p_175688_, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforcedCauldronBlock) {
                reinforcedCauldronBlock.setPotion(Potions.WATER);
            }
            return actionResult;
        };

    static final CauldronBehavior FILL_MILK = (state, level, pos, player, hand, itemStack) -> {
        ActionResult interaction  = CauldronBehavior.fillCauldron(level, pos, player, hand, itemStack, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);
        if (interaction == ActionResult.CONSUME) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforcedCauldronBlock) {
                reinforcedCauldronBlock.setPotion(ModPotions.MILK);
            }
        }
        return interaction;
    };

        static final CauldronBehavior FILL_LAVA = (p_175676_, p_175677_, p_175678_, p_175679_, p_175680_, p_175681_) -> CauldronBehavior.fillCauldron(p_175677_, p_175678_, p_175679_, p_175680_, p_175681_, Blocks.LAVA_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
        static final CauldronBehavior FILL_POWDER_SNOW = (p_175669_, p_175670_, p_175671_, p_175672_, p_175673_, p_175674_) -> CauldronBehavior.fillCauldron(p_175670_, p_175671_, p_175672_, p_175673_, p_175674_, Blocks.POWDER_SNOW_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW);

        public static void bootStrap() {
            addDefaultInteractions(EMPTY);
            EMPTY.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
                if (!level.isClient) {
                    Item item = stack.getItem();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.incrementStat(Stats.USE_CAULDRON);
                    player.incrementStat(Stats.USED.getOrCreateStat(item));
                    level.setBlockState(pos, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState());

                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforced) {
                        reinforced.setPotion(PotionUtil.getPotion(stack));
                    }

                    level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    level.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                }

                return ActionResult.success(level.isClient);
            });
            addDefaultInteractions(WATER);
            //this needs to be changed because there can be things other than water
            WATER.put(Items.BUCKET, (p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_) -> fillWaterBucket(p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_, (p_175660_) -> p_175660_.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
            WATER.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
                if (!level.isClient) {
                    Item item = stack.getItem();

                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforced) {
                        Potion potion = reinforced.getPotion();
                        player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionUtil.setPotion(new ItemStack(Items.POTION), potion)));
                        if (potion != Potions.WATER) {
                            LayeredReinforcedCauldronBlock.lowerFillLevel0(state, level, pos);
                        }
                    }

                    player.incrementStat(Stats.USE_CAULDRON);
                    player.incrementStat(Stats.USED.getOrCreateStat(item));
                    level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    level.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
                }

                return ActionResult.success(level.isClient);
            });
            WATER.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
                if (state.get(LeveledCauldronBlock.LEVEL) != 3) {
                    if (!level.isClient) {
                        player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                        player.incrementStat(Stats.USE_CAULDRON);
                        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));

                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforced) {
                            if (reinforced.getPotion() != PotionUtil.getPotion(stack)) {
                                LayeredReinforcedCauldronBlock.boom(level,pos);
                            } else {
                                level.setBlockState(pos, state.cycle(LeveledCauldronBlock.LEVEL));
                                level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                level.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                            }
                        }
                    }

                    return ActionResult.success(level.isClient);
                } else {
                    return ActionResult.PASS;
                }
            });
            WATER.put(Items.LEATHER_BOOTS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
            WATER.put(Items.LEATHER_LEGGINGS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
            WATER.put(Items.LEATHER_CHESTPLATE, CauldronBehavior.CLEAN_DYEABLE_ITEM);
            WATER.put(Items.LEATHER_HELMET, CauldronBehavior.CLEAN_DYEABLE_ITEM);
            WATER.put(Items.LEATHER_HORSE_ARMOR, CauldronBehavior.CLEAN_DYEABLE_ITEM);
            WATER.put(Items.WHITE_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.GRAY_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.BLACK_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.BLUE_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.BROWN_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.CYAN_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.GREEN_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.LIGHT_BLUE_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.LIGHT_GRAY_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.LIME_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.MAGENTA_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.ORANGE_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.PINK_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.PURPLE_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.RED_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.YELLOW_BANNER, CauldronBehavior.CLEAN_BANNER);
            WATER.put(Items.WHITE_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.GRAY_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.BLACK_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.BLUE_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.BROWN_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.CYAN_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.GREEN_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.LIME_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.MAGENTA_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.ORANGE_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.PINK_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.PURPLE_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.RED_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);
            WATER.put(Items.YELLOW_SHULKER_BOX, CauldronBehavior.CLEAN_SHULKER_BOX);

            //custom//
            WATER.put(Items.DRAGON_BREATH, ModCauldronInteractions::dragonsBreath);
            WATER.put(Items.ARROW, (state, level, pos, player, stack, stack2) -> arrowCoating(state, level, pos, player, stack2));

            for (Item item : Registry.ITEM) {
                if (item instanceof ToolItem) {
                    WATER.put(item, (state, level, pos, player, hand, stack) -> weaponCoating(state, level, pos, player, stack));
                } else if (item.isFood()) {
                    WATER.put(item,ModCauldronInteractions::spikedFood);
                }
            }

            EMPTY.put(Items.MILK_BUCKET, FILL_MILK);
            //end//

            LAVA.put(Items.BUCKET, (p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_) -> fillBucket(p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_, new ItemStack(Items.LAVA_BUCKET), (p_175651_) -> true, SoundEvents.ITEM_BUCKET_FILL_LAVA));
            addDefaultInteractions(LAVA);
            POWDER_SNOW.put(Items.BUCKET, (p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_) -> fillBucket(p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_, new ItemStack(Items.POWDER_SNOW_BUCKET), (p_175627_) -> p_175627_.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW));
            addDefaultInteractions(POWDER_SNOW);
        }

        static void addDefaultInteractions(Map<Item, CauldronBehavior> p_175648_) {
            //    p_175648_.put(Items.LAVA_BUCKET, FILL_LAVA);
            p_175648_.put(Items.WATER_BUCKET, FILL_WATER);
            //       p_175648_.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
        }

    @Nonnull
    static ActionResult spikedFood(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand p_175715_, ItemStack stack) {
        LayeredReinforcedCauldronBlock.handleFoodSpiking(state,level,pos,player,p_175715_,stack);
        return ActionResult.success(level.isClient);
    }

        static ActionResult fillBucket(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack second, Predicate<BlockState> p_175643_, SoundEvent soundEvent) {
            if (!p_175643_.test(state)) {
                return ActionResult.PASS;
            } else {
                if (!level.isClient) {
                    fillB(level,pos,player,hand,stack,soundEvent,second);
                }

                return ActionResult.success(level.isClient);
            }
        }

    //milk exists

    static ActionResult fillWaterBucket(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, Predicate<BlockState> statePredicate, SoundEvent soundEvent) {
        if (!statePredicate.test(state)) {
            return ActionResult.PASS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ReinforcedCauldronBlockEntity cauldronBlockEntity) {
                Potion potion = cauldronBlockEntity.getPotion();
                if (!canBucket(potion)) {
                    return ActionResult.PASS;
                }
                if (!level.isClient) {
                    fillB(level,pos,player,hand,stack,soundEvent,getBucket(potion));
                }
            }
            return ActionResult.success(level.isClient);
        }
    }

    static void fillB(World level, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, SoundEvent soundEvent, ItemStack second) {
        Item item = stack.getItem();
        player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, second));
        player.incrementStat(Stats.USE_CAULDRON);
        player.incrementStat(Stats.USED.getOrCreateStat(item));
        level.setBlockState(pos, ModBlocks.REINFORCED_CAULDRON.getDefaultState());//patch
        level.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        level.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
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
        static ActionResult dragonsBreath(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand p_175715_, ItemStack stack) {
            if (!level.isClient) {
                if (!player.getAbilities().creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                    stack.decrement(1);

                    ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                    if (!player.getInventory().insertStack(itemstack4)) {
                        player.dropItem(itemstack4, false);
                    } else {
                        //     ((ServerPlayer) player).refreshContainer(player.inventoryMenu);
                    }
                }
                level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                level.setBlockState(pos,state.with(DRAGONS_BREATH,true));
            }
            return ActionResult.success(level.isClient);
        }

        @Nonnull
        static ActionResult arrowCoating(BlockState state, World level, BlockPos pos, PlayerEntity player, ItemStack stack) {
            LayeredReinforcedCauldronBlock.handleArrowCoating(state,level,pos,player, stack);
            return ActionResult.success(level.isClient);
        }

        @Nonnull
        static ActionResult weaponCoating(BlockState state, World level, BlockPos pos, PlayerEntity player, ItemStack stack) {
            LayeredReinforcedCauldronBlock.handleWeaponCoating(state,level,pos,player, stack);
            return ActionResult.success(level.isClient);
        }
    }
