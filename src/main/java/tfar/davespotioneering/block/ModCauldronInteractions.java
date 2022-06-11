package tfar.davespotioneering.block;

import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
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

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Predicate;

import static tfar.davespotioneering.block.LayeredReinforcedCauldronBlock.DRAGONS_BREATH;
/**
 * Copied from @CauldronBehavior
 */
public class ModCauldronBehaviors {

        public static final Map<Item, CauldronBehavior> WATER = CauldronBehavior.createMap();

        public static final Map<Item, CauldronBehavior> EMPTY = CauldronBehavior.createMap();
        static final Map<Item, CauldronBehavior> LAVA = CauldronBehavior.createMap();
        static final Map<Item, CauldronBehavior> POWDER_SNOW = CauldronBehavior.createMap();
        static final CauldronBehavior FILL_WATER = (p_175683_, p_175684_, p_175685_, p_175686_, p_175687_, p_175688_) -> {
            return emptyBucket(p_175684_, p_175685_, p_175686_, p_175687_, p_175688_, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState().setValue(LeveledCauldronBlock.LEVEL, Integer.valueOf(3)), SoundEvents.BUCKET_EMPTY);
        };
        static final CauldronBehavior FILL_LAVA = (p_175676_, p_175677_, p_175678_, p_175679_, p_175680_, p_175681_) -> {
            return emptyBucket(p_175677_, p_175678_, p_175679_, p_175680_, p_175681_, Blocks.LAVA_CAULDRON., SoundEvents.BUCKET_EMPTY_LAVA);
        };
        static final CauldronBehavior FILL_POWDER_SNOW = (p_175669_, p_175670_, p_175671_, p_175672_, p_175673_, p_175674_) -> {
            return emptyBucket(p_175670_, p_175671_, p_175672_, p_175673_, p_175674_, Blocks.POWDER_SNOW_CAULDRON.getDefaultState().setValue(LeveledCauldronBlock.LEVEL, Integer.valueOf(3)), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
        };
        static final CauldronBehavior SHULKER_BOX = (p_175662_, p_175663_, p_175664_, p_175665_, p_175666_, p_175667_) -> {
            Block block = Block.byItem(p_175667_.getItem());
            if (!(block instanceof ShulkerBoxBlock)) {
                return ActionResult.PASS;
            } else {
                if (!p_175663_.isClient) {
                    ItemStack itemstack = new ItemStack(Blocks.SHULKER_BOX);
                    if (p_175667_.hasNbt()) {
                        itemstack.setNbt(p_175667_.getNbt().copy());
                    }

                    p_175665_.setItemInHand(p_175666_, itemstack);
                    p_175665_.awardStat(Stats.CLEAN_SHULKER_BOX);
                    LeveledCauldronBlock.lowerFillLevel(p_175662_, p_175663_, p_175664_);
                }

                return ActionResult.success(p_175663_.isClient);
            }
        };
        static CauldronBehavior BANNER = (p_175653_, p_175654_, p_175655_, p_175656_, p_175657_, p_175658_) -> {
            if (BannerBlockEntity.getPatternCount(p_175658_) <= 0) {
                return ActionResult.PASS;
            } else {
                if (!p_175654_.isClient) {
                    ItemStack itemstack = p_175658_.copy();
                    itemstack.setCount(1);
                    BannerBlockEntity.removeLastPattern(itemstack);
                    if (!p_175656_.getAbilities().instabuild) {
                        p_175658_.shrink(1);
                    }

                    if (p_175658_.isEmpty()) {
                        p_175656_.setItemInHand(p_175657_, itemstack);
                    } else if (p_175656_.getInventory().add(itemstack)) {
                        p_175656_.inventoryMenu.sendAllDataToRemote();
                    } else {
                        p_175656_.drop(itemstack, false);
                    }

                    p_175656_.awardStat(Stats.CLEAN_BANNER);
                    LeveledCauldronBlock.lowerFillLevel(p_175653_, p_175654_, p_175655_);
                }

                return ActionResult.success(p_175654_.isClient);
            }
        };
        static CauldronBehavior DYED_ITEM = (p_175629_, p_175630_, p_175631_, p_175632_, p_175633_, p_175634_) -> {
            Item item = p_175634_.getItem();
            if (!(item instanceof DyeableArmorItem dyeableleatheritem)) {
                return ActionResult.PASS;
            } else {
                if (!dyeableleatheritem.hasColor(p_175634_)) {
                    return ActionResult.PASS;
                } else {
                    if (!p_175630_.isClient) {
                        dyeableleatheritem.removeColor(p_175634_);
                        p_175632_.awardStat(Stats.CLEAN_ARMOR);
                        LeveledCauldronBlock.lowerFillLevel(p_175629_, p_175630_, p_175631_);
                    }

                    return ActionResult.success(p_175630_.isClient);
                }
            }
        };

        public static void bootStrap() {
            addDefaultInteractions(EMPTY);
            EMPTY.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
                if (!level.isClient) {
                    Item item = stack.getItem();
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    level.setBlockState(pos, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState());

                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforced) {
                        reinforced.setPotion(PotionUtils.getPotion(stack));
                    }

                    level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                }

                return ActionResult.success(level.isClient);
            });
            addDefaultInteractions(WATER);
            WATER.put(Items.BUCKET, (p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_) -> {
                return fillBucket(p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_, new ItemStack(Items.WATER_BUCKET), (p_175660_) -> {
                    return p_175660_.get(LeveledCauldronBlock.LEVEL) == 3;
                }, SoundEvents.ITEM_BUCKET_FILL);
            });
            WATER.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
                if (!level.isClient) {
                    Item item = stack.getItem();

                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforced) {
                        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionUtils.setPotion(new ItemStack(Items.POTION), reinforced.getPotion())));
                    }

                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    LayeredReinforcedCauldronBlock.lowerFillLevel0(state, level, pos);
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                }

                return ActionResult.success(level.isClient);
            });
            WATER.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
                if (state.getValue(LeveledCauldronBlock.LEVEL) != 3) {
                    if (!level.isClient) {
                        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                        player.awardStat(Stats.USE_CAULDRON);
                        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforced) {

                            if (reinforced.getPotion() != PotionUtils.getPotion(stack)) {
                                LayeredReinforcedCauldronBlock.boom(level,pos);
                            } else {
                                level.setBlockstate(pos, state.cycle(LeveledCauldronBlock.LEVEL));
                                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                            }
                        }
                    }

                    return ActionResult.success(level.isClient);
                } else {
                    return ActionResult.PASS;
                }
            });
            WATER.put(Items.LEATHER_BOOTS, DYED_ITEM);
            WATER.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
            WATER.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
            WATER.put(Items.LEATHER_HELMET, DYED_ITEM);
            WATER.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
            WATER.put(Items.WHITE_BANNER, BANNER);
            WATER.put(Items.GRAY_BANNER, BANNER);
            WATER.put(Items.BLACK_BANNER, BANNER);
            WATER.put(Items.BLUE_BANNER, BANNER);
            WATER.put(Items.BROWN_BANNER, BANNER);
            WATER.put(Items.CYAN_BANNER, BANNER);
            WATER.put(Items.GREEN_BANNER, BANNER);
            WATER.put(Items.LIGHT_BLUE_BANNER, BANNER);
            WATER.put(Items.LIGHT_GRAY_BANNER, BANNER);
            WATER.put(Items.LIME_BANNER, BANNER);
            WATER.put(Items.MAGENTA_BANNER, BANNER);
            WATER.put(Items.ORANGE_BANNER, BANNER);
            WATER.put(Items.PINK_BANNER, BANNER);
            WATER.put(Items.PURPLE_BANNER, BANNER);
            WATER.put(Items.RED_BANNER, BANNER);
            WATER.put(Items.YELLOW_BANNER, BANNER);
            WATER.put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.RED_SHULKER_BOX, SHULKER_BOX);
            WATER.put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);

            //custom//
            WATER.put(Items.DRAGON_BREATH,ModCauldronBehaviors::dragonsBreath);
            WATER.put(Items.ARROW,ModCauldronBehaviors::arrowCoating);

            for (Item item : Registry.ITEM) {
                if (item instanceof ToolItem) {
                    WATER.put(item,ModCauldronBehaviors::weaponCoating);
                }
            }
            //end//

            LAVA.put(Items.BUCKET, (p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_) -> {
                return fillBucket(p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_, new ItemStack(Items.LAVA_BUCKET), (p_175651_) -> {
                    return true;
                }, SoundEvents.BUCKET_FILL_LAVA);
            });
            addDefaultInteractions(LAVA);
            POWDER_SNOW.put(Items.BUCKET, (p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_) -> {
                return fillBucket(p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_, new ItemStack(Items.POWDER_SNOW_BUCKET), (p_175627_) -> {
                    return p_175627_.getValue(LeveledCauldronBlock.LEVEL) == 3;
                }, SoundEvents.BUCKET_FILL_POWDER_SNOW);
            });
            addDefaultInteractions(POWDER_SNOW);
        }

        static void addDefaultInteractions(Map<Item, CauldronBehavior> p_175648_) {
            //    p_175648_.put(Items.LAVA_BUCKET, FILL_LAVA);
            p_175648_.put(Items.WATER_BUCKET, FILL_WATER);
            //       p_175648_.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
        }

        static ActionResult fillBucket(BlockState p_175636_, World p_175637_, BlockPos p_175638_, PlayerEntity player, Hand p_175640_, ItemStack p_175641_, ItemStack p_175642_, Predicate<BlockState> p_175643_, SoundEvent p_175644_) {
            if (!p_175643_.test(p_175636_)) {
                return ActionResult.PASS;
            } else {
                if (!p_175637_.isClient) {
                    Item item = p_175641_.getItem();
                    player.setItemInHand(p_175640_, ItemUtils.createFilledResult(p_175641_, player, p_175642_));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    p_175637_.setBlockstate(p_175638_, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState());//patch
                    p_175637_.playSound(null, p_175638_, p_175644_, SoundSource.BLOCKS, 1.0F, 1.0F);
                    p_175637_.gameEvent(null, GameEvent.FLUID_PICKUP, p_175638_);
                }

                return ActionResult.success(p_175637_.isClient);
            }
        }

        static ActionResult emptyBucket(World p_175619_, BlockPos p_175620_, PlayerEntity p_175621_, Hand p_175622_, ItemStack p_175623_, BlockState p_175624_, SoundEvent p_175625_) {
            if (!p_175619_.isClient) {
                Item item = p_175623_.getItem();
                p_175621_.setItemInHand(p_175622_, ItemUtils.createFilledResult(p_175623_, p_175621_, new ItemStack(Items.BUCKET)));
                p_175621_.awardStat(Stats.FILL_CAULDRON);
                p_175621_.awardStat(Stats.ITEM_USED.get(item));
                p_175619_.setBlockState(p_175620_, p_175624_);
                p_175619_.playSound(null, p_175620_, p_175625_, SoundSource.BLOCKS, 1.0F, 1.0F);
                p_175619_.emitGameEvent(null, GameEvent.FLUID_PLACE, p_175620_);
            }

            return ActionResult.success(p_175619_.isClient);
        }

        @Nonnull
        static ActionResult dragonsBreath(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand p_175715_, ItemStack stack) {
            if (!level.isClient) {
                if (!player.getAbilities().creativeMode) {
                    player.awardStat(Stats.USE_CAULDRON);
                    stack.shrink(1);

                    ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                    if (!player.getInventory().add(itemstack4)) {
                        player.drop(itemstack4, false);
                    } else {
                        //     ((ServerPlayer) player).refreshContainer(player.inventoryMenu);
                    }
                }
                level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                level.setBlockState(pos,state.setValue(DRAGONS_BREATH,true));
            }
            return ActionResult.success(level.isClient);
        }

        @Nonnull
        static ActionResult arrowCoating(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand p_175715_, ItemStack stack) {
            LayeredReinforcedCauldronBlock.handleArrowCoating(state,level,pos,player,p_175715_,stack);
            return ActionResult.success(level.isClient);
        }

        @Nonnull
        static ActionResult weaponCoating(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand p_175715_, ItemStack stack) {
            LayeredReinforcedCauldronBlock.handleWeaponCoating(state,level,pos,player,p_175715_,stack);
            return ActionResult.success(level.isClient);
        }
    }
