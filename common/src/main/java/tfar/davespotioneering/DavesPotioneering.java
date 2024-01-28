package tfar.davespotioneering;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import tfar.davespotioneering.block.CLayeredReinforcedCauldronBlock;
import tfar.davespotioneering.block.ModCauldronInteractions;
import tfar.davespotioneering.client.DavesPotioneeringClient;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.item.CUmbrellaItem;
import tfar.davespotioneering.menu.CAdvancedBrewingStandMenu;
import tfar.davespotioneering.mixin.BlockEntityTypeAcces;
import tfar.davespotioneering.mixin.BrewingStandContainerAccess;
import tfar.davespotioneering.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class DavesPotioneering {

    public static final String MOD_NAME = "Dave's Potioneering";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final String MODID = "davespotioneering";
    public static final boolean isFabric = Services.PLATFORM.getPlatformName().equals("Fabric");

    public static void earlySetup() {
        Services.PLATFORM.superRegister(ModBlocks.class,BuiltInRegistries.BLOCK, Block.class);
        Services.PLATFORM.superRegister(ModEffects.class,BuiltInRegistries.MOB_EFFECT, MobEffect.class);
        Services.PLATFORM.superRegister(ModItems.class,BuiltInRegistries.ITEM, Item.class);
        Services.PLATFORM.superRegister(ModBlockEntityTypes.class,BuiltInRegistries.BLOCK_ENTITY_TYPE, BlockEntityType.class);
        Services.PLATFORM.superRegister(ModMenuTypes.class,BuiltInRegistries.MENU, MenuType.class);
        Services.PLATFORM.superRegister(ModPotions.class,BuiltInRegistries.POTION, Potion.class);
        Services.PLATFORM.superRegister(ModParticleTypes.class,BuiltInRegistries.PARTICLE_TYPE, ParticleType.class);
        Services.PLATFORM.superRegister(ModSoundEvents.class,BuiltInRegistries.SOUND_EVENT, SoundEvent.class);
    }


    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void commonSetup() {
        Set<Block> newSet = new HashSet<>(((BlockEntityTypeAcces)BlockEntityType.LECTERN).getValidBlocks());
        newSet.add(ModBlocks.MAGIC_LECTERN);
        ((BlockEntityTypeAcces)BlockEntityType.LECTERN).setValidBlocks(newSet);
    }

    public static void onEat(Player player, ItemStack stack) {
        List<MobEffectInstance> mobEffectInstances = PotionUtils.getMobEffects(stack);
        for (MobEffectInstance effectInstance : mobEffectInstances) {
            player.addEffect(new MobEffectInstance(effectInstance.getEffect(), Math.max(effectInstance.getDuration() / 8, 1), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.showIcon()));
        }
    }

    //this is called when the potion is done brewing, we use this instead of the forge event because it has a reference
    // to the blockentity that created the potions
    public static void potionBrew(BlockEntity brewingStandTileEntity, ItemStack ingredient) {
        ((BrewingStandDuck)brewingStandTileEntity).addXp(Util.getBrewXp(ingredient));
    }

    public static void heldItemChangeEvent(Player player) {
        ItemStack stack = player.getMainHandItem();
        if ((stack.getItem() instanceof LingeringPotionItem || stack.getItem() instanceof SplashPotionItem)) {
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, Services.PLATFORM.potionSwitchCooldown());
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, Services.PLATFORM.potionSwitchCooldown());
        }
    }


    //this is called when the player takes a potion from the brewing stand
    public static void playerTakeBrewedPotion(Player player) {
        if (!player.level().isClientSide) {
            AbstractContainerMenu container = player.containerMenu;
            BlockEntity entity = null;
            if (container instanceof BrewingStandMenu) {
                entity = (BrewingStandBlockEntity)((BrewingStandContainerAccess)container).getBrewingStand();
            } else if (container instanceof CAdvancedBrewingStandMenu) {
                entity = ((CAdvancedBrewingStandMenu)container).blockEntity;
            }

            if (entity != null) {
                ((BrewingStandDuck)entity).dump(player);
            }
        }
    }

    public static boolean canApplyEffect(LivingEntity entity) {
        if (entity instanceof Player player) {
            return !(player.getUseItem().getItem() instanceof CUmbrellaItem);
        }
        return true;
    }

    public static void afterHit(Player player, LivingEntity victim) {
        ItemStack weapon = player.getMainHandItem();
        if (weapon.is(ModItems.WHITELISTED)) {
            Potion potion = PotionUtils.getPotion(weapon);
            if (potion != Potions.EMPTY) {
                for(MobEffectInstance effectinstance : potion.getEffects()) {
                    victim.addEffect(new MobEffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
                }
                if (!player.getAbilities().instabuild)
                    CLayeredReinforcedCauldronBlock.useCharge(weapon);
            }
        }
    }

    public static void tagsUpdated() {
        long start = net.minecraft.Util.getNanos();
        ModCauldronInteractions.reload();
        long end = net.minecraft.Util.getNanos();
        long time = end - start;
        float ms = time /1000000f;

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.DOWN);
        String s = df.format(ms);

        LOG.info("Took "+ s + " ms to reload cauldron interactions");
    }
}