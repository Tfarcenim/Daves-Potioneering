package tfar.davespotioneering.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ReinforcedCauldronBlock extends CauldronBlock implements EntityBlock {

    public static final BooleanProperty DRAGONS_BREATH = BooleanProperty.create("dragons_breath");


    public static int brew_speed = 12;

    public ReinforcedCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DRAGONS_BREATH,false));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        int level = state.getValue(LEVEL);
        if (stack.getItem() instanceof PotionItem) {
            handlePotionBottle(state, world, pos, player, stack, level);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (stack.getItem() == Items.GLASS_BOTTLE) {
            handleEmptyBottle(state, world, pos, player, hand, stack, level);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (stack.getItem() == Items.DRAGON_BREATH && level == 3) {
            handleDragonBreath(state,world, pos, player, stack);
        } else if (stack.getItem() instanceof TieredItem && level == 3 ) {
            handleWeaponCoating(state,world, pos, player, stack);
        } else if (stack.getItem() == Items.ARROW && level > 0) {
            handleArrowCoating(state,world, pos, player, stack,level);
        } else if (PotionUtils.getPotion(stack) != Potions.EMPTY && level > 0) {
            removeCoating(state,world, pos, player, stack);
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    private void handlePotionBottle(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, int level) {
        Potion potion = PotionUtils.getPotion(stack);
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion storedPotion = reinforcedCauldronBlockEntity.getPotion();
        if (!world.isClientSide) {
            if (level < 3) {
                if (!player.abilities.instabuild) {
                    player.awardStat(Stats.USE_CAULDRON);
                    stack.shrink(1);

                    ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                    if (!player.inventory.add(itemstack4)) {
                        player.drop(itemstack4, false);
                    } else {
                        ((ServerPlayer) player).refreshContainer(player.inventoryMenu);
                    }
                }

                world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (level > 0 && storedPotion != potion) {
                    boom(world, pos, state);
                } else {
                    this.setWaterLevel(world, pos, state, level + 1);
                    reinforcedCauldronBlockEntity.setPotion(potion);
                }
            }
        }
    }

    public void boom(Level world,BlockPos pos,BlockState state) {
        this.setWaterLevel(world, pos, state, 0);
        world.explode(null, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 1, false, Explosion.BlockInteraction.NONE);
    }

    private void handleDragonBreath(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack) {
        if (!world.isClientSide) {
            if (!player.abilities.instabuild) {
                player.awardStat(Stats.USE_CAULDRON);
                stack.shrink(1);

                ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                if (!player.inventory.add(itemstack4)) {
                    player.drop(itemstack4, false);
                } else {
                    ((ServerPlayer) player).refreshContainer(player.inventoryMenu);
                }
            }
            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.setBlockAndUpdate(pos,state.setValue(DRAGONS_BREATH,true));
        }
    }

    private void handleEmptyBottle(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, int level) {
        if (level > 0 && !world.isClientSide) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!player.abilities.instabuild) {
                ItemStack itemstack4 = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
                player.awardStat(Stats.USE_CAULDRON);
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, itemstack4);
                } else if (!player.inventory.add(itemstack4)) {
                    player.drop(itemstack4, false);
                } else if (player instanceof ServerPlayer) {
                    ((ServerPlayer) player).refreshContainer(player.inventoryMenu);
                }
            }
            world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (potion != Potions.WATER)
                this.setWaterLevel(world, pos, state, level - 1);
        }
    }

    public static void handleWeaponCoating(BlockState state, Level world, BlockPos pos, @Nullable Player player, ItemStack stack) {
        if (state.getValue(DRAGONS_BREATH)) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!world.isClientSide) {
                if (player != null && !player.abilities.instabuild) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                addCoating(stack,potion);
                world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                state = state.setValue(DRAGONS_BREATH, false);
                ((CauldronBlock)state.getBlock()).setWaterLevel(world,pos,state,0);
            }
        }
    }

    public static void handleArrowCoating(BlockState state, Level world, BlockPos pos,@Nullable Player player, ItemStack stack,int level) {
        if (state.getValue(DRAGONS_BREATH)) {
            //can't tip arrows if there's less than 8
            if (stack.getCount() < 8) {
                return;
            }
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!world.isClientSide) {
                if (player != null && !player.abilities.instabuild) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW,8);
                addCoating(tippedArrows,potion);
                stack.shrink(8);
                world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                world.addFreshEntity(new ItemEntity(world,pos.getX(),pos.getY()+1,pos.getZ(),tippedArrows));
                if (level <= 1)
                state = state.setValue(DRAGONS_BREATH, false);
                ((CauldronBlock)state.getBlock()).setWaterLevel(world,pos,state,level - 1);
            }
        }
    }

    public static void removeCoating(BlockState state, Level world, BlockPos pos,@Nullable Player player, ItemStack stack) {
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        if (potion == ModPotions.MILK && !world.isClientSide) {
            if (player != null && !player.abilities.instabuild) {
                player.awardStat(Stats.USE_CAULDRON);
            }
            removeCoating(stack);
            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            ((CauldronBlock)state.getBlock()).setWaterLevel(world,pos,state,state.getValue(LEVEL) - 1);
        }
    }

    @Override
    public void setWaterLevel(Level world, BlockPos pos, BlockState state, int level) {
        super.setWaterLevel(world, pos, state, level);
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        if (level == 0) {
            reinforcedCauldronBlockEntity.setPotion(Potions.EMPTY);
        } else {
            if (reinforcedCauldronBlockEntity.getPotion() == Potions.EMPTY) {
                reinforcedCauldronBlockEntity.setPotion(Potions.WATER);
            }
        }
    }

    public static void removeCoating(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        nbt.remove("uses");
        nbt.remove("Potion");
    }

    public static void addCoating(ItemStack stack,Potion potion) {
        if (stack.getItem() instanceof TieredItem) {
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.putInt("uses", 25);
            nbt.putString("Potion", potion.getRegistryName().toString());
        } else if (stack.getItem() == Items.TIPPED_ARROW) {
            PotionUtils.setPotion(stack, potion);
        }
    }

    public static void useCharge(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DRAGONS_BREATH);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new ReinforcedCauldronBlockEntity();
    }


    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link #randomTick} and {@link #ticksRandomly(BlockState)}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(DRAGONS_BREATH)) {
            double d0 = pos.getX();
            double d1 = (double) pos.getY() + 1D;
            double d2 = pos.getZ();
            for (int i = 0; i < 5; i++) {
                worldIn.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, d0 + (double) rand.nextFloat(), d1 + (double) rand.nextFloat(), d2 + (double) rand.nextFloat(), 0.0D, 0.04D, 0.0D);
            }
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
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        int level = state.getValue(LEVEL);

        world.playSound(null,pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1);

        if (state.getValue(LEVEL) > 1) {
            setWaterLevel(world,pos,state,level - 1);
            world.getBlockTicks().scheduleTick(pos, this, brew_speed);
        } else {
            List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class,
                    new AABB(pos));

            if (items.size() == 1) {
                handleWeaponCoating(state, world, pos, null, items.get(0).getItem());
            } else {
                boom(world,pos,state);
            }
        }
    }

    public static final int S_LINES = 2;
    public static final int C_LINES = 2;
    public static final int A_LINES = 2;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            for (int i = 0; i < S_LINES;i++) {

                tooltip.add(this.getShiftDescriptions(i).withStyle(ChatFormatting.GRAY));
            }

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES;i++) {
                tooltip.add(this.getCtrlDescriptions(i).withStyle(ChatFormatting.GRAY));
            }

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_alt.desc"));
        if (Screen.hasAltDown()) {
            for (int i = 0; i < A_LINES;i++) {
                tooltip.add(this.getAltDescriptions(i).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public MutableComponent getShiftDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".shift.desc");
    }

    public MutableComponent getShiftDescriptions(int i) {
        return new TranslatableComponent(this.getDescriptionId() + i +".shift.desc");
    }

    public MutableComponent getCtrlDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".ctrl.desc");
    }

    public MutableComponent getCtrlDescriptions(int i) {
        return new TranslatableComponent(this.getDescriptionId() + i +".ctrl.desc");
    }

    public MutableComponent getAltDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".alt.desc");
    }

    public MutableComponent getAltDescriptions(int i) {
        return new TranslatableComponent(this.getDescriptionId() + i+".alt.desc");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new ReinforcedCauldronBlockEntity();
    }
}
