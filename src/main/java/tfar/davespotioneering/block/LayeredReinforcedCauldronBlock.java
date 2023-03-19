package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.init.ModSoundEvents;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class LayeredReinforcedCauldronBlock extends LeveledCauldronBlock implements BlockEntityProvider {

    public static final BooleanProperty DRAGONS_BREATH = BooleanProperty.of("dragons_breath");


    public static int brew_speed = 12;

    public static final String LAYERS = DavesPotioneering.MODID+":layers";

    public LayeredReinforcedCauldronBlock(Settings properties) {
        super(properties,LeveledCauldronBlock.RAIN_PREDICATE, ModCauldronInteractions.WATER);
        this.setDefaultState(this.stateManager.getDefaultState().with(DRAGONS_BREATH,false));
    }

    public static void handlePotionBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, int level) {
        Potion potion = PotionUtil.getPotion(stack);
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion storedPotion = reinforcedCauldronBlockEntity.getPotion();
        if (!world.isClient) {
            if (level < 3) {
                if (!player.getAbilities().creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                    stack.decrement(1);

                    ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                    if (!player.getInventory().insertStack(itemstack4)) {
                        player.dropItem(itemstack4, false);
                    } else {
                        // ((ServerPlayer) player).refreshContainer(player.inventoryMenu);
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (level > 0 && storedPotion != potion) {
                    boom(world, pos);
                } else {
                //    setWaterLevel(world, pos, state, level + 1);
                    reinforcedCauldronBlockEntity.setPotion(potion);
                }
            }
        }
    }

    public static void lowerFillLevel0(BlockState p_153560_, World p_153561_, BlockPos pos) {
        int i = p_153560_.get(LEVEL) - 1;
        p_153561_.setBlockState(pos, i == 0 ? ModBlocks.REINFORCED_CAULDRON.getDefaultState() : p_153560_.with(LEVEL, i));
    }

    public static void boom(World level, BlockPos pos) {
        level.setBlockState(pos,ModBlocks.REINFORCED_CAULDRON.getDefaultState());
        level.createExplosion(null, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 1, false, Explosion.DestructionType.NONE);
    }

    public static void handleWeaponCoating(BlockState state, World level, BlockPos pos, @Nullable PlayerEntity player, ItemStack stack) {
        if (state.get(DRAGONS_BREATH)) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!level.isClient) {
                if (player != null && !player.getAbilities().creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                }
                addCoating(stack,potion);
                level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                level.setBlockState(pos,ModBlocks.REINFORCED_CAULDRON.getDefaultState());
            }
        }
    }

    public static void handleWeaponCoatingEntity(BlockState state, World level, BlockPos pos, @Nullable PlayerEntity player, ItemEntity stack) {
        if (state.get(DRAGONS_BREATH)) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!level.isClient) {
                if (player != null && !player.getAbilities().creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                }
                ItemStack copy = stack.getStack().copy();
                addCoating(copy,potion);
                stack.setStack(copy);
                level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                level.setBlockState(pos,ModBlocks.REINFORCED_CAULDRON.getDefaultState());
            }
        }
    }

    public static void handleArrowCoating(BlockState state, World level, BlockPos pos, @Nullable PlayerEntity player, ItemStack stack) {
        int wLevel = state.get(LEVEL);
        if (state.get(DRAGONS_BREATH)) {
            //can't tip arrows if there's less than 8
            if (stack.getCount() < 8) {
                return;
            }
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) level.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!level.isClient) {
                if (player != null && !player.getAbilities().creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                }
                ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW, 8);
                addCoating(tippedArrows, potion);
                stack.decrement(8);
                level.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                level.spawnEntity(new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), tippedArrows));
                if (wLevel <= 1) {
                    level.setBlockState(pos, ModBlocks.REINFORCED_CAULDRON.getDefaultState());
                } else {
                    level.setBlockState(pos, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState().with(LEVEL,wLevel - 1));
                }
            }
        }
    }

    public static void removeCoating(BlockState state, World world, BlockPos pos,@Nullable PlayerEntity player, ItemStack stack) {
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        if (potion == ModPotions.MILK && !world.isClient) {
            if (player != null && !player.getAbilities().creativeMode) {
                player.incrementStat(Stats.USE_CAULDRON);
            }
            removeCoating(stack);
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void removeCoating(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        nbt.remove("uses");
        nbt.remove("Potion");
    }

    public static void addCoating(ItemStack stack,Potion potion) {
        if (stack.getItem() instanceof ToolItem) {
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putInt("uses", 25);
            nbt.putString("Potion", Registry.POTION.getId(potion).toString());
        } else if (stack.getItem() == Items.TIPPED_ARROW) {
            PotionUtil.setPotion(stack, potion);
        }
    }

    public static void useCharge(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {

            int uses = nbt.getInt("uses");
            uses--;
            if (uses > 0) {
                nbt.putInt("uses",uses);
            } else {
                removeCoating(stack);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(DRAGONS_BREATH);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link #randomTick} and {@link #randomDisplayTick(BlockState)}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(DRAGONS_BREATH)) {
            double d0 = pos.getX();
            double d1 = (double) pos.getY() + 1D;
            double d2 = pos.getZ();
            for (int i = 0; i < 5; i++) {
                worldIn.addImportantParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, d0 + (double) rand.nextFloat(), d1 + (double) rand.nextFloat(), d2 + (double) rand.nextFloat(), 0.0D, 0.04D, 0.0D);
            }
            worldIn.playSound(pos.getX(),pos.getY(),pos.getZ(), ModSoundEvents.BUBBLING_WATER_CAULDRON, SoundCategory.BLOCKS,.5f,1,false);
        }
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ReinforcedCauldronBlockEntity) {
            ((ReinforcedCauldronBlockEntity) tileentity).onEntityCollision(entityIn);
        }
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    //this is used for the coating
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        int level = state.get(LEVEL);

        world.playSound(null,pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1);

        if (state.get(LEVEL) > 1) {
            lowerFillLevel0(state,world,pos);
            world.createAndScheduleBlockTick(pos, this, brew_speed);
        } else {
            List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class,
                    new Box(pos),a -> true);

            if (items.size() == 1) {
                handleWeaponCoating(state, world, pos, null, items.get(0).getStack());
            } else {
                boom(world,pos);
            }
        }
    }

    public static final int S_LINES = 2;
    public static final int C_LINES = 2;
    public static final int A_LINES = 2;

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView worldIn, List<Text> tooltip, TooltipContext flagIn) {

        tooltip.add(new TranslatableText(getTranslationKey()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            for (int i = 0; i < S_LINES;i++) {

                tooltip.add(this.getShiftDescriptions(i).formatted(Formatting.GRAY));
            }

        tooltip.add(new TranslatableText(getTranslationKey()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES;i++) {
                tooltip.add(this.getCtrlDescriptions(i).formatted(Formatting.GRAY));
            }

        tooltip.add(new TranslatableText(getTranslationKey()+".hold_alt.desc"));
        if (Screen.hasAltDown()) {
            for (int i = 0; i < A_LINES;i++) {
                tooltip.add(this.getAltDescriptions(i).formatted(Formatting.GRAY));
            }
        }
    }

    public MutableText getShiftDescription() {
        return new TranslatableText(this.getTranslationKey() + ".shift.desc");
    }

    public MutableText getShiftDescriptions(int i) {
        return new TranslatableText(this.getTranslationKey() + i +".shift.desc");
    }

    public MutableText getCtrlDescription() {
        return new TranslatableText(this.getTranslationKey() + ".ctrl.desc");
    }

    public MutableText getCtrlDescriptions(int i) {
        return new TranslatableText(this.getTranslationKey() + i +".ctrl.desc");
    }

    public MutableText getAltDescription() {
        return new TranslatableText(this.getTranslationKey() + ".alt.desc");
    }

    public MutableText getAltDescriptions(int i) {
        return new TranslatableText(this.getTranslationKey() + i+".alt.desc");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReinforcedCauldronBlockEntity(blockPos,blockState);
    }
}
