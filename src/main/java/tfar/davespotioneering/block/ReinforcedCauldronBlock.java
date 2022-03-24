package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModPotions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ReinforcedCauldronBlock extends CauldronBlock {

    public static final BooleanProperty DRAGONS_BREATH = BooleanProperty.create("dragons_breath");


    public static int brew_speed = 12;

    public ReinforcedCauldronBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(DRAGONS_BREATH,false));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        int level = state.get(LEVEL);
        if (stack.getItem() instanceof PotionItem) {
            handlePotionBottle(state, world, pos, player, stack, level);
            return ActionResultType.func_233537_a_(world.isRemote);
        } else if (stack.getItem() == Items.GLASS_BOTTLE) {
            handleEmptyBottle(state, world, pos, player, hand, stack, level);
            return ActionResultType.func_233537_a_(world.isRemote);
        } else if (stack.getItem() == Items.DRAGON_BREATH && level == 3) {
            handleDragonBreath(state,world, pos, player, stack);
        } else if (stack.getItem() instanceof TieredItem && level == 3 ) {
            handleWeaponCoating(state,world, pos, player, stack);
        } else if (stack.getItem() == Items.ARROW && level > 0) {
            handleArrowCoating(state,world, pos, player, stack,level);
        } else if (PotionUtils.getPotionFromItem(stack) != Potions.EMPTY && level > 0) {
            removeCoating(state,world, pos, player, stack);
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    private void handlePotionBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, int level) {
        Potion potion = PotionUtils.getPotionFromItem(stack);
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getTileEntity(pos);
        Potion storedPotion = reinforcedCauldronBlockEntity.getPotion();
        if (!world.isRemote) {
            if (level < 3) {
                if (!player.abilities.isCreativeMode) {
                    player.addStat(Stats.USE_CAULDRON);
                    stack.shrink(1);

                    ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                    if (!player.inventory.addItemStackToInventory(itemstack4)) {
                        player.dropItem(itemstack4, false);
                    } else {
                        ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (level > 0 && storedPotion != potion) {
                    boom(world, pos, state);
                } else {
                    this.setWaterLevel(world, pos, state, level + 1);
                    reinforcedCauldronBlockEntity.setPotion(potion);
                }
            }
        }
    }

    public void boom(World world,BlockPos pos,BlockState state) {
        this.setWaterLevel(world, pos, state, 0);
        world.createExplosion(null, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 1, false, Explosion.Mode.NONE);
    }

    private void handleDragonBreath(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        if (!world.isRemote) {
            if (!player.abilities.isCreativeMode) {
                player.addStat(Stats.USE_CAULDRON);
                stack.shrink(1);

                ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                if (!player.inventory.addItemStackToInventory(itemstack4)) {
                    player.dropItem(itemstack4, false);
                } else {
                    ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
                }
            }
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.setBlockState(pos,state.with(DRAGONS_BREATH,true));
        }
    }

    private void handleEmptyBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, int level) {
        if (level > 0 && !world.isRemote) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getTileEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!player.abilities.isCreativeMode) {
                ItemStack itemstack4 = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion);
                player.addStat(Stats.USE_CAULDRON);
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, itemstack4);
                } else if (!player.inventory.addItemStackToInventory(itemstack4)) {
                    player.dropItem(itemstack4, false);
                } else if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
                }
            }
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (potion != Potions.WATER)
                this.setWaterLevel(world, pos, state, level - 1);
        }
    }

    public static void handleWeaponCoating(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player, ItemStack stack) {
        if (state.get(DRAGONS_BREATH)) {
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getTileEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!world.isRemote) {
                if (player != null && !player.abilities.isCreativeMode) {
                    player.addStat(Stats.USE_CAULDRON);
                }
                addCoating(stack,potion);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                state = state.with(DRAGONS_BREATH, false);
                ((CauldronBlock)state.getBlock()).setWaterLevel(world,pos,state,0);
            }
        }
    }

    public static void handleArrowCoating(BlockState state, World world, BlockPos pos,@Nullable PlayerEntity player, ItemStack stack,int level) {
        if (state.get(DRAGONS_BREATH)) {
            //can't tip arrows if there's less than 8
            if (stack.getCount() < 8) {
                return;
            }
            ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getTileEntity(pos);
            Potion potion = reinforcedCauldronBlockEntity.getPotion();
            if (!world.isRemote) {
                if (player != null && !player.abilities.isCreativeMode) {
                    player.addStat(Stats.USE_CAULDRON);
                }
                ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW,8);
                addCoating(tippedArrows,potion);
                stack.shrink(8);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                world.addEntity(new ItemEntity(world,pos.getX(),pos.getY()+1,pos.getZ(),tippedArrows));
                if (level <= 1)
                state = state.with(DRAGONS_BREATH, false);
                ((CauldronBlock)state.getBlock()).setWaterLevel(world,pos,state,level - 1);
            }
        }
    }

    public static void removeCoating(BlockState state, World world, BlockPos pos,@Nullable PlayerEntity player, ItemStack stack) {
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getTileEntity(pos);
        Potion potion = reinforcedCauldronBlockEntity.getPotion();
        if (potion == ModPotions.MILK && !world.isRemote) {
            if (player != null && !player.abilities.isCreativeMode) {
                player.addStat(Stats.USE_CAULDRON);
            }
            removeCoating(stack);
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            ((CauldronBlock)state.getBlock()).setWaterLevel(world,pos,state,state.get(LEVEL) - 1);
        }
    }

    @Override
    public void setWaterLevel(World world, BlockPos pos, BlockState state, int level) {
        super.setWaterLevel(world, pos, state, level);
        ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) world.getTileEntity(pos);
        if (level == 0) {
            reinforcedCauldronBlockEntity.setPotion(Potions.EMPTY);
        } else {
            if (reinforcedCauldronBlockEntity.getPotion() == Potions.EMPTY) {
                reinforcedCauldronBlockEntity.setPotion(Potions.WATER);
            }
        }
    }

    public static void removeCoating(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        nbt.remove("uses");
        nbt.remove("Potion");
    }

    public static void addCoating(ItemStack stack,Potion potion) {
        if (stack.getItem() instanceof TieredItem) {
            CompoundNBT nbt = stack.getOrCreateTag();
            nbt.putInt("uses", 25);
            nbt.putString("Potion", potion.getRegistryName().toString());
        } else if (stack.getItem() == Items.TIPPED_ARROW) {
            PotionUtils.addPotionToItemStack(stack, potion);
        }
    }

    public static void useCharge(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(DRAGONS_BREATH);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReinforcedCauldronBlockEntity();
    }


    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link #randomTick} and {@link #ticksRandomly(BlockState)}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(DRAGONS_BREATH)) {
            double d0 = pos.getX();
            double d1 = (double) pos.getY() + 1D;
            double d2 = pos.getZ();
            for (int i = 0; i < 5; i++) {
                worldIn.addOptionalParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, d0 + (double) rand.nextFloat(), d1 + (double) rand.nextFloat(), d2 + (double) rand.nextFloat(), 0.0D, 0.04D, 0.0D);
            }
        }
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof ReinforcedCauldronBlockEntity) {
            ((ReinforcedCauldronBlockEntity) tileentity).onEntityCollision(entityIn);
        }
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }


    //this is used for the coating
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        int level = state.get(LEVEL);

        world.playSound(null,pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1);

        if (state.get(LEVEL) > 1) {
            setWaterLevel(world,pos,state,level - 1);
            world.getPendingBlockTicks().scheduleTick(pos, this, brew_speed);
        } else {
            List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class,
                    new AxisAlignedBB(pos));

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
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            for (int i = 0; i < S_LINES;i++) {

                tooltip.add(this.getShiftDescriptions(i).mergeStyle(TextFormatting.GRAY));
            }

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES;i++) {
                tooltip.add(this.getCtrlDescriptions(i).mergeStyle(TextFormatting.GRAY));
            }

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_alt.desc"));
        if (Screen.hasAltDown()) {
            for (int i = 0; i < A_LINES;i++) {
                tooltip.add(this.getAltDescriptions(i).mergeStyle(TextFormatting.GRAY));
            }
        }
    }

    public IFormattableTextComponent getShiftDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".shift.desc");
    }

    public IFormattableTextComponent getShiftDescriptions(int i) {
        return new TranslationTextComponent(this.getTranslationKey() + i +".ctrl.desc");
    }

    public IFormattableTextComponent getCtrlDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".ctrl.desc");
    }

    public IFormattableTextComponent getCtrlDescriptions(int i) {
        return new TranslationTextComponent(this.getTranslationKey() + i +".ctrl.desc");
    }

    public IFormattableTextComponent getAltDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".alt.desc");
    }

    public IFormattableTextComponent getAltDescriptions(int i) {
        return new TranslationTextComponent(this.getTranslationKey() + i+".alt.desc");
    }
}
