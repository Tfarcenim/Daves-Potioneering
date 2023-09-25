package tfar.davespotioneering.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.menu.PotionInjectorMenu;
import tfar.davespotioneering.net.PacketHandler;
import tfar.davespotioneering.net.S2CCooldownPacket;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class GauntletItem extends SwordItem {

    public static final String ACTIVE = "active";
    public static final String ACTIVE_POTION = "activePotionIndex";
    public static final String BLAZE = "blaze";
    public static final String INFO = "info";
    public static final String COOLDOWNS = "potionCooldownMap";
    public static final String POTIONS = "potions";
    public static final int SLOTS = 6;

    public GauntletItem(Properties properties) {
        super(Tiers.NETHERITE, 4, -2.8f, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (playerIn.isShiftKeyDown()) {
//                PacketHandler.sendToClient(new GauntletHUDMovementGuiPacket(), (ServerPlayerEntity) playerIn);


            boolean active = stack.getOrCreateTag().getBoolean(ACTIVE);

            int blaze = getBlaze(stack);

            if (!world.isClientSide && (blaze > 0 || active)) {
                stack.getOrCreateTag().putBoolean(ACTIVE, !active);
                world.playSound(null,playerIn.getX(),playerIn.getY(),playerIn.getZ(),active ? ModSoundEvents.GAUNTLET_TURNING_OFF : ModSoundEvents.GAUNTLET_TURNING_ON, SoundSource.PLAYERS,.5f,1);
            } else {
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    public MutableComponent getCtrlDescriptions(int i) {
        return Component.translatable(this.getDescriptionId() + i +".ctrl.desc");
    }

    @Override
    public int getDamage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        double blaze = 0;
        if (tag != null) {
            CompoundTag info = tag.getCompound(INFO);
            blaze = info.getInt(BLAZE);
        }
        return PotionInjectorMenu.BLAZE_CAP - (int) blaze;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        CompoundTag info = stack.getOrCreateTag().getCompound(INFO);
        double blaze = info.getInt(BLAZE);
        return blaze > 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ChatFormatting.GOLD.getColor();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        if (stack.getItem() instanceof GauntletItem) {
            CompoundTag info = stack.getOrCreateTag().getCompound(INFO);
            boolean active = stack.getTag().getBoolean(ACTIVE);
            if (active) {
                CompoundTag activeEffect = getActiveEffectFromNBT(info);
                int cooldown = getCooldownFromPotionByIndex(info.getInt(ACTIVE_POTION), attacker);
                if (activeEffect != null && cooldown <= 0 && info.getInt(BLAZE) > 0) {
                    for (MobEffectInstance effectInstance : PotionUtils.getAllEffects(activeEffect)) {
                        victim.addEffect(new MobEffectInstance(effectInstance));
                    }

                    int blaze = info.getInt(BLAZE);
                    info.putInt(BLAZE, --blaze);
                    if (blaze <= 0) {
                        stack.getTag().putBoolean(ACTIVE, false);
                    }
                    setPotionCooldownByIndex(info.getInt(ACTIVE_POTION), ModConfig.Server.gauntlet_cooldown.get(), attacker);
                }
            }
        }
        return super.hurtEnemy(stack, victim, attacker);
    }

    public static final int C_LINES = 3;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        tooltip.add(Component.translatable(getDescriptionId() + ".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable(getDescriptionId() + ".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES; i++) {
                tooltip.add(this.getCtrlDescriptions(i).withStyle(ChatFormatting.GRAY));
            }

        if (!stack.hasTag()) return;

        ListTag nbts = stack.getTag().getCompound(INFO).getList(POTIONS, Tag.TAG_COMPOUND);

        if (nbts.isEmpty()) return;
        tooltip.add(Component.literal(" "));

        for (Tag instance : nbts) {
            Util.addPotionTooltip((CompoundTag) instance,tooltip,.125f);
            tooltip.add(Component.literal("----------------"));
        }
    }

    public static void tickCooldowns(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side.isServer()) {
            Player player = event.player;
            CompoundTag persistent = player.getPersistentData();
            if (!persistent.contains(DavesPotioneering.MODID)) return;
            CompoundTag tag = persistent.getCompound(DavesPotioneering.MODID);

            int[] cooldowns = tag.getIntArray(COOLDOWNS);

            int[] newCooldowns = Arrays.copyOf(cooldowns,cooldowns.length);

            for (int i = 0; i < newCooldowns.length; i++) {
                int cooldown = newCooldowns[i];
                if (cooldown > 0) {
                    newCooldowns[i] = --cooldown;
                }
            }
            if (!Arrays.equals(cooldowns,newCooldowns)) {
                tag.putIntArray(COOLDOWNS, newCooldowns);
                PacketHandler.sendToClient(new S2CCooldownPacket(newCooldowns), (ServerPlayer) player);
            }
        }
    }

    public MutableComponent getShiftDescription() {
        return Component.translatable(this.getDescriptionId() + ".shift.desc");
    }

    public MutableComponent getCtrlDescription() {
        return Component.translatable(this.getDescriptionId() + ".ctrl.desc");
    }


    public static CompoundTag getActiveEffectFromNBT(CompoundTag info) {
        ListTag nbts = info.getList(POTIONS, Tag.TAG_COMPOUND);
        if (nbts.isEmpty()) return null;
        // get active potion
        int index = info.getInt(ACTIVE_POTION);
        return nbts.getCompound(index);
    }

    public static void cycleGauntletForward(Player player) {
        if (player == null) return;
        CompoundTag info = player.getMainHandItem().getOrCreateTag().getCompound(INFO);
        ListTag nbts = info.getList(POTIONS, Tag.TAG_COMPOUND);
        if (nbts.isEmpty()) return;
        int index = info.getInt(ACTIVE_POTION);
        index++;
        if (index > 5) {
            index = 0;
        }
        info.putInt(ACTIVE_POTION, index);
    }

    public static void cycleGauntletBackward(Player player) {
        if (player == null) return;
        CompoundTag info = player.getMainHandItem().getOrCreateTag().getCompound(INFO);
        ListTag nbts = info.getList(POTIONS, Tag.TAG_COMPOUND);
        if (nbts.isEmpty()) return;
        int index = info.getInt(ACTIVE_POTION);
        index--;
        if (index < 0) {
            index = 5;
        }
        info.putInt(ACTIVE_POTION, index);
    }

    public static Potion[] getVisibleEffects(CompoundTag info) {
        ListTag nbts = info.getList(POTIONS, Tag.TAG_COMPOUND);
        if (nbts.isEmpty()) return null;

        // get active potion
        int index = info.getInt(ACTIVE_POTION);

        CompoundTag tag = nbts.getCompound(index);

        index--;
        if (index < 0) {
            index = 5;
        }

        // get the potion in front of active potion
        CompoundTag pre = nbts.getCompound(index);

        index += 2;
        index %= 6;

        // get the potion behind of active potion

        CompoundTag post = nbts.getCompound(index);

        Potion activePotion = PotionUtils.getPotion(tag);
        Potion prePotion = PotionUtils.getPotion(pre);
        Potion postPotion = PotionUtils.getPotion(post);
        return new Potion[]{activePotion, prePotion, postPotion};
    }

    public static void setPotionCooldownByIndex(int index, int cooldown, LivingEntity living) {
        CompoundTag persistent = living.getPersistentData();
        CompoundTag tag;
        if (!persistent.contains(DavesPotioneering.MODID)) {
            tag = new CompoundTag();
        } else {
            tag = persistent.getCompound(DavesPotioneering.MODID);
        }

        persistent.put(DavesPotioneering.MODID, tag);

        int[] cooldowns = tag.getIntArray(COOLDOWNS);

        if (cooldowns.length == 0) {
            cooldowns = new int[6];
        }
        cooldowns[index] = cooldown;
        tag.putIntArray(COOLDOWNS, cooldowns);
    }

    private static int getCooldownFromPotionByIndex(int indexOfPotion, LivingEntity living) {
        CompoundTag tag = living.getPersistentData().getCompound(DavesPotioneering.MODID);
        int[] cooldowns = tag.getIntArray(COOLDOWNS);
        if (cooldowns.length == 0) return 0;
        return cooldowns[indexOfPotion];
    }

    public static int getBlaze(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int blaze = 0;
        if (tag != null) {
            CompoundTag info = tag.getCompound(INFO);
            blaze = info.getInt(BLAZE);
        }
        return blaze;
    }
}