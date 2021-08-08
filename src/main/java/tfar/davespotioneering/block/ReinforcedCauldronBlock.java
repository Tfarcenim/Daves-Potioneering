package tfar.davespotioneering.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;

import javax.annotation.Nullable;

public class ReinforcedCauldronBlock extends CauldronBlock {
    public ReinforcedCauldronBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        int level = state.get(LEVEL);
        if (stack.getItem() instanceof PotionItem) {
            handlePotionBottle(state,world,pos,player,stack,level);
            return ActionResultType.func_233537_a_(world.isRemote);
        } else if (stack.getItem() == Items.GLASS_BOTTLE) {
            handleEmptyBottle(state,world,pos,player, hand, stack,level);
            return ActionResultType.func_233537_a_(world.isRemote);
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    private void handlePotionBottle(BlockState state,World world,BlockPos pos,PlayerEntity player,ItemStack stack,int level) {
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
                    } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
                    }

                    if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (level > 0 && storedPotion != potion) {
                    this.setWaterLevel(world, pos, state, 0);
                    world.createExplosion(null,pos.getX(),pos.getY(),pos.getZ(),1,false, Explosion.Mode.NONE);
                } else {
                    this.setWaterLevel(world, pos, state, level + 1);
                    reinforcedCauldronBlockEntity.setPotion(potion);
                }
            }
        }
    }

    private void handleEmptyBottle(BlockState state,World world,BlockPos pos,PlayerEntity player,Hand hand,ItemStack stack,int level) {
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
                    ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
                }
            }
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (potion != Potions.WATER)
            this.setWaterLevel(world, pos, state, level - 1);
        }
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
}
