package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ReinforcedCauldronBlock extends CauldronBlock implements BlockEntityProvider {

    public static final BooleanProperty DRAGONS_BREATH = BooleanProperty.of("dragons_breath");


    public static int brew_speed = 12;

    public ReinforcedCauldronBlock(Settings properties) {
        super(properties);
        this.setDefaultState(this.stateManager.getDefaultState().with(DRAGONS_BREATH,false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        int level = state.get(LEVEL);
        if (stack.getItem() instanceof PotionItem) {
            handlePotionBottle(state, world, pos, player, stack, level);
            return ActionResult.success(world.isClient);
        } else if (stack.getItem() == Items.GLASS_BOTTLE) {
            handleEmptyBottle(state, world, pos, player, hand, stack, level);
            return ActionResult.success(world.isClient);
        } else if (stack.getItem() == Items.DRAGON_BREATH && level == 3) {
            handleDragonBreath(state,world, pos, player, stack);
        } else if (stack.getItem() instanceof ToolItem && level == 3 ) {
            handleWeaponCoating(state,world, pos, player, stack);
        } else if (stack.getItem() == Items.ARROW && level > 0) {
            handleArrowCoating(state,world, pos, player, stack,level);
        } else if (PotionUtil.getPotion(stack) != Potions.EMPTY && level > 0) {
            removeCoating(state,world, pos, player, stack);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    private void handlePotionBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, int level) {
        Potion potion = PotionUtil.getPotion(stack);
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion storedPotion = reinforcedCauldronBlockEntity.getPotion();
        if (!world.isClient) {
            if (level < 3) {
                if (!player.abilities.creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                    stack.decrement(1);

                    ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                    if (!player.inventory.insertStack(itemstack4)) {
                        player.dropItem(itemstack4, false);
                    } else {
                        ((ServerPlayerEntity) player).refreshScreenHandler(player.playerScreenHandler);
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (level > 0 && storedPotion != potion) {
                    boom(world, pos, state);
                } else {
                    reinforcedCauldronBlockEntity.setPotion(potion);
                    this.setLevel(world, pos, state, level + 1);
                }
            }
        }
    }

    public void boom(World world,BlockPos pos,BlockState state) {
        this.setLevel(world, pos, state, 0);
        world.createExplosion(null, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 1, false, Explosion.DestructionType.NONE);
    }

    private void handleDragonBreath(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        if (!world.isClient) {
            if (!player.abilities.creativeMode) {
                player.incrementStat(Stats.USE_CAULDRON);
                stack.decrement(1);

                ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                if (!player.inventory.insertStack(itemstack4)) {
                    player.dropItem(itemstack4, false);
                } else {
                    ((ServerPlayerEntity) player).refreshScreenHandler(player.playerScreenHandler);
                }
            }
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.setBlockState(pos,state.with(DRAGONS_BREATH,true));
        }
    }

    private void handleEmptyBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, int level) {
        if (level > 0 && !world.isClient) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!player.abilities.creativeMode) {
                ItemStack itemstack4 = PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
                player.incrementStat(Stats.USE_CAULDRON);
                stack.decrement(1);
                if (stack.isEmpty()) {
                    player.setStackInHand(hand, itemstack4);
                } else if (!player.inventory.insertStack(itemstack4)) {
                    player.dropItem(itemstack4, false);
                } else if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) player).refreshScreenHandler(player.playerScreenHandler);
                }
            }
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (potion != Potions.WATER)
                this.setLevel(world, pos, state, level - 1);
        }
    }

    public static void handleWeaponCoating(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player, ItemStack stack) {
        if (state.get(DRAGONS_BREATH)) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!world.isClient) {
                if (player != null && !player.abilities.creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                }
                addCoating(stack,potion);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                state = state.with(DRAGONS_BREATH, false);
                ((CauldronBlock)state.getBlock()).setLevel(world,pos,state,0);
            }
        }
    }

    public static void handleArrowCoating(BlockState state, World world, BlockPos pos,@Nullable PlayerEntity player, ItemStack stack,int level) {
        if (state.get(DRAGONS_BREATH)) {
            //can't tip arrows if there's less than 8
            if (stack.getCount() < 8) {
                return;
            }
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!world.isClient) {
                if (player != null && !player.abilities.creativeMode) {
                    player.incrementStat(Stats.USE_CAULDRON);
                }
                ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW,8);
                addCoating(tippedArrows,potion);
                stack.decrement(8);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                world.spawnEntity(new ItemEntity(world,pos.getX(),pos.getY()+1,pos.getZ(),tippedArrows));
                if (level <= 1)
                state = state.with(DRAGONS_BREATH, false);
                ((CauldronBlock)state.getBlock()).setLevel(world,pos,state,level - 1);
            }
        }
    }

    public static void removeCoating(BlockState state, World world, BlockPos pos,@Nullable PlayerEntity player, ItemStack stack) {
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getBlockEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        if (potion == ModPotions.MILK && !world.isClient) {
            if (player != null && !player.abilities.creativeMode) {
                player.incrementStat(Stats.USE_CAULDRON);
            }
            removeCoating(stack);
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            ((CauldronBlock)state.getBlock()).setLevel(world,pos,state,state.get(LEVEL) - 1);
        }
    }

    @Override
    public void setLevel(World world, BlockPos pos, BlockState state, int level) {
        super.setLevel(world, pos, state, level);
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
        if (stack.getItem() instanceof ToolItem) {
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.putInt("uses", 25);
            nbt.putString("Potion", Registry.POTION.getId(potion).toString());
        } else if (stack.getItem() == Items.TIPPED_ARROW) {
            PotionUtil.setPotion(stack, potion);
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(DRAGONS_BREATH);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link #randomTick} and {@link #ticksRandomly(BlockState)}, and will always be called regardless
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
            setLevel(world,pos,state,level - 1);
            world.getBlockTickScheduler().schedule(pos, this, brew_speed);
        } else {
            List<ItemEntity> items = world.getNonSpectatingEntities(ItemEntity.class,
                    new Box(pos));

            if (items.size() == 1) {
                handleWeaponCoating(state, world, pos, null, items.get(0).getStack());
            } else {
                boom(world,pos,state);
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
    public BlockEntity createBlockEntity(BlockView blockGetter) {
        return new ReinforcedCauldronBlockEntity();
    }
}
