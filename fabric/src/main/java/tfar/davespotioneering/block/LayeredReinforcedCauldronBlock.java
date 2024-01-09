package tfar.davespotioneering.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import tfar.davespotioneering.*;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.init.ModSoundEvents;

import javax.annotation.Nullable;
import java.util.List;

public class LayeredReinforcedCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public static final BooleanProperty DRAGONS_BREATH = BooleanProperty.create("dragons_breath");


    public static int brew_speed = 12;

    public static final String LAYERS = DavesPotioneering.MODID+":layers";
    public static final String USES = "uses";

    public LayeredReinforcedCauldronBlock(Properties properties) {
        super(properties,LayeredCauldronBlock.RAIN, ModCauldronInteractions.WATER);
        this.registerDefaultState(this.stateDefinition.any().setValue(DRAGONS_BREATH,false));
    }

    public static void lowerFillLevel0(BlockState p_153560_, Level p_153561_, BlockPos pos) {
        int i = p_153560_.getValue(LEVEL) - 1;
        p_153561_.setBlockAndUpdate(pos, i == 0 ? ModBlocks.REINFORCED_CAULDRON.defaultBlockState() : p_153560_.setValue(LEVEL, i));
    }

    public static void boom(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos,ModBlocks.REINFORCED_CAULDRON.defaultBlockState());
        level.explode(null, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 1, false, Level.ExplosionInteraction.NONE);
    }

    public static void handleWeaponCoating(BlockState state, Level level, BlockPos pos, @Nullable Player player, ItemStack stack) {
        if (state.getValue(DRAGONS_BREATH)) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            List<MobEffectInstance> customEffects = reinforcedCauldronBlockEntity.getCustomEffects();
            Integer customPotionColor = reinforcedCauldronBlockEntity.getCustomPotionColor();
            if (!level.isClientSide) {
                if (player != null && !player.getAbilities().instabuild) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                addCoating(stack,potion,customEffects,customPotionColor);
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
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!level.isClientSide) {
                if (player != null && !player.getAbilities().instabuild) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                List<MobEffectInstance> customEffects = reinforcedCauldronBlockEntity.getCustomEffects();
                Integer customPotionColor = reinforcedCauldronBlockEntity.getCustomPotionColor();
                ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW, 8);
                addCoating(tippedArrows, potion,customEffects,customPotionColor);
                stack.shrink(8);
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), tippedArrows));
                if (wLevel <= 1) {
                    level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_CAULDRON.defaultBlockState());
                } else {
                    level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState().setValue(LEVEL,wLevel - 1));
                }
            }
        }
    }

    public static void handleFoodSpiking(BlockState state, Level level, BlockPos pos, @Nullable Player player, InteractionHand p_175715_, ItemStack stack) {
        int wLevel = state.getValue(LEVEL);
        if (stack.getCount() < 8) {
            return;
        }
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        List<MobEffectInstance> customEffects = reinforcedCauldronBlockEntity.getCustomEffects();
        Integer customPotionColor = reinforcedCauldronBlockEntity.getCustomPotionColor();
        if (!level.isClientSide) {
            boolean milk = potion == ModPotions.MILK;

            if (milk && PotionUtils.getPotion(stack) == Potions.EMPTY) return;

            if (player != null && !player.getAbilities().instabuild) {
                player.awardStat(Stats.USE_CAULDRON);
            }
            ItemStack spikedFood = stack.split(8);

            if (milk)
                removeCoating(spikedFood);
            else
                addCoating(spikedFood, potion,customEffects,customPotionColor);
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), spikedFood));
            if (wLevel <= 1) {
                level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_CAULDRON.defaultBlockState());
            } else {
                level.setBlockAndUpdate(pos,ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState().setValue(LEVEL,wLevel - 1));
            }
        }
    }


    public static void removeCoating(BlockState state, Level world, BlockPos pos,@Nullable Player player, ItemStack stack) {
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        if (potion == ModPotions.MILK && !world.isClientSide) {
            if (player != null && !player.getAbilities().instabuild) {
                player.awardStat(Stats.USE_CAULDRON);
            }
            removeCoating(stack);
            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
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


    public static void addCoating(ItemStack stack, Potion potion, List<MobEffectInstance> customEffects, @Nullable Integer color) {
        PotionUtils.setPotion(stack, potion);
        PotionUtils.setCustomEffects(stack,customEffects);
        if (color != null) {
            PotionUtils2.setCustomColor(stack, color);
        }
        if (Util.CoatingType.getCoatingType(stack) != Util.CoatingType.FOOD) {
            stack.getTag().putInt(TAG_USES, DavesPotioneeringFabric.CONFIG.coating_uses);
        }
    }

    public static void useCharge(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null) {

            int uses = nbt.getInt(USES);
            uses--;
            if (uses > 0) {
                nbt.putInt(USES,uses);
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
     * this method is unrelated to {@link #randomTick} and {@link #animateTick(BlockState)}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (stateIn.getValue(DRAGONS_BREATH)) {
            if (worldIn.getGameTime() % 5 == 0)
                worldIn.playLocalSound(pos.getX(),pos.getY(),pos.getZ(), ModSoundEvents.BUBBLING_WATER_CAULDRON, SoundSource.BLOCKS,.5f,1,false);
        }
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ReinforcedCauldronBlockEntity) {
            ((ReinforcedCauldronBlockEntity) tileentity).onEntityCollision(entityIn);
        }
        super.entityInside(state, worldIn, pos, entityIn);
    }

    //this is used for the coating
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        int wLevel = state.getValue(LEVEL);

        world.playSound(null,pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);

        if (state.getValue(LEVEL) > 1) {
            lowerFillLevel0(state,world,pos);
            world.scheduleTick(pos, this, brew_speed);
        } else {
            List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class,
                    new AABB(pos),a -> true);

            if (items.size() == 1) {
                handleWeaponCoating(state, world, pos, null, items.get(0).getItem());
            } else {
                boom(world,pos);
            }
        }
    }

    public static final int S_LINES = 2;
    public static final int C_LINES = 2;
    public static final int A_LINES = 2;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(Component.translatable(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            for (int i = 0; i < S_LINES;i++) {

                tooltip.add(this.getShiftDescriptions(i).withStyle(ChatFormatting.GRAY));
            }

        tooltip.add(Component.translatable(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES;i++) {
                tooltip.add(this.getCtrlDescriptions(i).withStyle(ChatFormatting.GRAY));
            }

        tooltip.add(Component.translatable(getDescriptionId()+".hold_alt.desc"));
        if (Screen.hasAltDown()) {
            for (int i = 0; i < A_LINES;i++) {
                tooltip.add(this.getAltDescriptions(i).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public MutableComponent getShiftDescription() {
        return Component.translatable(this.getDescriptionId() + ".shift.desc");
    }

    public MutableComponent getShiftDescriptions(int i) {
        return Component.translatable(this.getDescriptionId() + i +".shift.desc");
    }

    public MutableComponent getCtrlDescription() {
        return Component.translatable(this.getDescriptionId() + ".ctrl.desc");
    }

    public MutableComponent getCtrlDescriptions(int i) {
        return Component.translatable(this.getDescriptionId() + i +".ctrl.desc");
    }

    public MutableComponent getAltDescription() {
        return Component.translatable(this.getDescriptionId() + ".alt.desc");
    }

    public MutableComponent getAltDescriptions(int i) {
        return Component.translatable(this.getDescriptionId() + i+".alt.desc");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReinforcedCauldronBlockEntity(blockPos,blockState);
    }
}
