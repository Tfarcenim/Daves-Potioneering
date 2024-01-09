package tfar.davespotioneering.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import tfar.davespotioneering.PotionUtils2;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.platform.Services;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CLayeredReinforcedCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public static final BooleanProperty DRAGONS_BREATH = BooleanProperty.create("dragons_breath");


    public static int brew_speed = 12;

    public CLayeredReinforcedCauldronBlock(Properties properties) {
        this(properties, LayeredCauldronBlock.RAIN, ModCauldronInteractions.WATER);
    }

    public CLayeredReinforcedCauldronBlock(Properties p_153522_, Predicate<Biome.Precipitation> p_153523_, Map<Item, CauldronInteraction> p_153524_) {
        super(p_153522_, p_153523_, p_153524_);
        this.registerDefaultState(this.stateDefinition.any().setValue(DRAGONS_BREATH,false));
    }

    public static void lowerFillLevel0(BlockState p_153560_, Level p_153561_, BlockPos pos) {
        int i = p_153560_.getValue(LEVEL) - 1;
        p_153561_.setBlockAndUpdate(pos, i == 0 ? ModBlocks.REINFORCED_CAULDRON.defaultBlockState() : p_153560_.setValue(LEVEL, i));
    }

    public static void boom(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos,ModBlocks.REINFORCED_CAULDRON.defaultBlockState());
        level.explode(null, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 1f, false, Level.ExplosionInteraction.NONE);
    }

    public static void handleWeaponCoating(BlockState state, Level level, BlockPos pos, @Nullable Player player, ItemStack stack) {
        if (state.getValue(DRAGONS_BREATH)) {
            CReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (CReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            List<MobEffectInstance> customEffects = reinforcedCauldronBlockEntity.getCustomEffects();
            if (!level.isClientSide) {
                if (player != null && !player.getAbilities().instabuild) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                addCoating(stack,potion,customEffects,reinforcedCauldronBlockEntity.getCustomPotionColor());
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlockAndUpdate(pos,ModBlocks.REINFORCED_CAULDRON.defaultBlockState());
            }
        }
    }

    public static void handleArrowCoating(BlockState state, Level level, BlockPos pos, @Nullable Player player, ItemStack stack) {
        int wLevel = state.getValue(LEVEL);
        if (state.getValue(DRAGONS_BREATH)) {
            //can't tip arrows if there's less than 8
            if (stack.getCount() < 8) {
                return;
            }
            CReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (CReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            List<MobEffectInstance> customEffects = reinforcedCauldronBlockEntity.getCustomEffects();
            if (!level.isClientSide) {
                if (player != null && !player.getAbilities().instabuild) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW, 8);
                addCoating(tippedArrows, potion,customEffects,reinforcedCauldronBlockEntity.getCustomPotionColor());
                stack.shrink(8);
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), tippedArrows));
                if (wLevel <= 1) {
                    level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_CAULDRON.defaultBlockState());
                } else {
                    level.setBlockAndUpdate(pos,ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState().setValue(LEVEL,wLevel - 1));
                }
            }
        }
    }

    public static boolean canStripEffects(ItemStack stack) {
        return !(stack.getItem() instanceof PotionItem);
    }
    public static void handleFoodSpiking(BlockState state, Level level, BlockPos pos, @Nullable Player player, InteractionHand p_175715_, ItemStack stack) {
        int wLevel = state.getValue(LEVEL);
        if (stack.getCount() < 8) {
            return;
        }
        CReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (CReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        List<MobEffectInstance> customEffects = reinforcedCauldronBlockEntity.getCustomEffects();
        if (!level.isClientSide) {
            if (player != null && !player.getAbilities().instabuild) {
                player.awardStat(Stats.USE_CAULDRON);
            }
            ItemStack tippedArrows = stack.split(8);
            addCoating(tippedArrows, potion,customEffects,reinforcedCauldronBlockEntity.getCustomPotionColor());
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), tippedArrows));
            if (wLevel <= 1) {
                level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_CAULDRON.defaultBlockState());
            } else {
                level.setBlockAndUpdate(pos,ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState().setValue(LEVEL,wLevel - 1));
            }
        }
    }

    public static void removeCoating(BlockState state, Level world, BlockPos pos,@Nullable Player player, ItemStack stack) {
        CReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (CReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        if (potion == ModPotions.MILK && !world.isClientSide) {
            if (player != null && !player.getAbilities().instabuild) {
                player.awardStat(Stats.USE_CAULDRON);
            }
            removeCoating(stack);
            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            //setWaterLevel(world,pos,state,state.getValue(LEVEL) - 1);
        }
    }

    public static void setWaterLevel(Level world, BlockPos pos, BlockState state, int level) {
        CReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (CReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        if (level == 0) {
            reinforcedCauldronBlockEntity.setPotion(Potions.EMPTY);
        } else {
            if (reinforcedCauldronBlockEntity.getPotion() == Potions.EMPTY && reinforcedCauldronBlockEntity.getCustomEffects().isEmpty()) {
                reinforcedCauldronBlockEntity.setPotion(Potions.WATER);
            }
        }
    }

    public static final String TAG_USES = "uses";

    public static void removeCoating(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        nbt.remove(TAG_USES);
        nbt.remove(PotionUtils.TAG_POTION);
        nbt.remove(PotionUtils.TAG_CUSTOM_POTION_EFFECTS);
        nbt.remove(PotionUtils.TAG_CUSTOM_POTION_COLOR);
    }


    public static void addCoating(ItemStack stack, Potion potion, List<MobEffectInstance> customEffects,@Nullable Integer color) {
        PotionUtils.setPotion(stack, potion);
        PotionUtils.setCustomEffects(stack,customEffects);
        if (color != null) {
            PotionUtils2.setCustomColor(stack, color);
        }
        if (Util.CoatingType.getCoatingType(stack) != Util.CoatingType.FOOD) {
            stack.getTag().putInt(TAG_USES, Services.PLATFORM.coatingUses());
        }
    }

    public static void useCharge(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null) {
            int uses = nbt.getInt(TAG_USES);
            uses--;
            if (uses > 0) {
                nbt.putInt(TAG_USES,uses);
            } else {
                removeCoating(stack);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DRAGONS_BREATH);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link #randomTick} and {@link #isRandomlyTicking(BlockState)} (BlockState)}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @Override
    public void animateTick(BlockState stateIn, Level world, BlockPos pos, RandomSource rand) {
        if (stateIn.getValue(DRAGONS_BREATH)) {
            world.playLocalSound(pos.getX(),pos.getY(),pos.getZ(), ModSoundEvents.BUBBLING_WATER_CAULDRON, SoundSource.BLOCKS,.25f,1,false);
        }
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof CReinforcedCauldronBlockEntity) {
            ((CReinforcedCauldronBlockEntity) tileentity).onEntityCollision(entityIn);
        }
        super.entityInside(state, worldIn, pos, entityIn);
    }
    //this is used for the coating
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        int wLevel = state.getValue(LEVEL);

        if (wLevel > 1) {
            world.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);
            world.setBlockAndUpdate(pos, state.setValue(LEVEL, wLevel - 1));
            world.scheduleTick(pos, this, brew_speed);
        } else {
            List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class,
                    new AABB(pos));

            if (items.size() == 1) {
                handleWeaponCoating(state, world, pos,null, items.get(0).getItem());
            } else {
                boom(world,pos);
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return Services.PLATFORM.makeReinforcedCauldron(p_153215_,p_153216_);
    }
}
