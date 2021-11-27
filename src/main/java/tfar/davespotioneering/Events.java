package tfar.davespotioneering;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TieredItem;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModPotions;

public class Events {

    public static void potionCooldown(PlayerInteractEvent.RightClickItem e) {
        ItemStack stack = e.getItemStack();
        PlayerEntity player = e.getPlayer();
        if (!player.world.isRemote && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldownTracker().setCooldown(stack.getItem(), ModConfig.Server.potion_cooldown);
        }
    }

    public static void milkCow(PlayerInteractEvent.EntityInteractSpecific e) {
        Entity clicked = e.getTarget();
        PlayerEntity player = e.getPlayer();
        if (clicked instanceof CowEntity) {
            CowEntity cowEntity = (CowEntity)clicked;
            ItemStack itemstack = player.getHeldItem(e.getHand());
            if (itemstack.getItem() == Items.GLASS_BOTTLE && !cowEntity.isChild()) {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                itemstack.shrink(1);
                ItemStack milkBottle = new ItemStack(Items.POTION);
                PotionUtils.addPotionToItemStack(milkBottle, ModPotions.MILK);
                player.addItemStackToInventory(milkBottle);
            }
        }
    }

    public static void afterHit(LivingDamageEvent e) {
        LivingEntity victim = e.getEntityLiving();

        DamageSource source = e.getSource();

        Entity trueSource = source.getTrueSource();

        if (trueSource instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity)trueSource;

            ItemStack weapon = attacker.getHeldItemMainhand();

            if (weapon.getItem() instanceof TieredItem) {
                Potion potion = PotionUtils.getPotionFromItem(weapon);
                if (potion != Potions.EMPTY) {
                    for(EffectInstance effectinstance : potion.getEffects()) {
                        victim.addPotionEffect(new EffectInstance(effectinstance.getPotion(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.doesShowParticles()));
                    }
                    ReinforcedCauldronBlock.useCharge(weapon);
                }
            }
        }
    }

    public static void playerTick(TickEvent.PlayerTickEvent e) {
        PlayerEntity player = e.player;
        if (e.phase == TickEvent.Phase.END && e.side == LogicalSide.CLIENT && player.world.getGameTime() %4 == 0) {
            IParticleData particleData = Fluids.WATER.getDefaultState().getDripParticleData();
            Vector3d vec = player.getPositionVec().add(0,+ player.getHeight() / 2,0);

            double yaw = -MathHelper.wrapDegrees(player.rotationYaw);

            double z1 = Math.cos(yaw * Math.PI/180) * .75;
            double x1 = Math.sin(yaw * Math.PI/180) * .75;

            double z2 = Math.cos((yaw+270) * Math.PI/180)/2;
            double x2 = Math.sin((yaw+270) * Math.PI/180)/2;

            vec = vec.add(x1 + x2,0,z1 + z2);

            spawnFluidParticle((ClientWorld)player.world,vec, Blocks.STONE.getDefaultState(), particleData, true);
        }
    }

    private static void spawnFluidParticle(ClientWorld world, Vector3d blockPosIn, BlockState blockStateIn, IParticleData particleDataIn, boolean shapeDownSolid) {
        VoxelShape voxelshape = blockStateIn.getCollisionShapeUncached(world, new BlockPos(blockPosIn));
       // world.spawnParticle(new BlockPos(blockPosIn), particleDataIn, voxelshape, blockPosIn.getY() +.5);
        world.addParticle(particleDataIn,blockPosIn.x,blockPosIn.y,blockPosIn.z,0,0,0);
    }
}
