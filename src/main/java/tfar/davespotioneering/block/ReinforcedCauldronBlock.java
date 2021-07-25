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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;

import javax.annotation.Nullable;

public class ReinforcedCauldronBlock extends CauldronBlock {
    public ReinforcedCauldronBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        int level = state.get(LEVEL);
        if (stack.getItem() instanceof PotionItem && PotionUtils.getPotionFromItem(stack) != Potions.WATER) {
            if (level < 3 && !worldIn.isRemote) {
                if (!player.abilities.isCreativeMode) {
                    player.addStat(Stats.USE_CAULDRON);
                    stack.shrink(1);

                    ItemStack itemstack4 = new ItemStack(Items.GLASS_BOTTLE);

                    if (!player.inventory.addItemStackToInventory(itemstack4)) {
                        player.dropItem(itemstack4, false);
                    } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
                    }

                    if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
                    }
                }

                worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                this.setWaterLevel(worldIn, pos, state, level + 1);
                ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) worldIn.getTileEntity(pos);
                reinforcedCauldronBlockEntity.setPotion(PotionUtils.getPotionFromItem(stack));
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        } else if (stack.getItem() == Items.GLASS_BOTTLE) {
            if (level > 0 && !worldIn.isRemote) {
                if (!player.abilities.isCreativeMode) {
                    ReinforcedCauldronBlockEntity reinforcedCauldronBlockEntity = (ReinforcedCauldronBlockEntity) worldIn.getTileEntity(pos);
                    Potion potion = reinforcedCauldronBlockEntity.getPotion();
                    ItemStack itemstack4 = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion);
                    player.addStat(Stats.USE_CAULDRON);
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.setHeldItem(handIn, itemstack4);
                    } else if (!player.inventory.addItemStackToInventory(itemstack4)) {
                        player.dropItem(itemstack4, false);
                    } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
                    }
                }
                worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                this.setWaterLevel(worldIn, pos, state, level - 1);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
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
