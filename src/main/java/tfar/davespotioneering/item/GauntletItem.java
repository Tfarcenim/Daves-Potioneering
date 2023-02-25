package tfar.davespotioneering.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.menu.PotionInjectorMenu;
import tfar.davespotioneering.net.PacketHandler;
import tfar.davespotioneering.net.S2CCooldownPacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
        return new TranslatableComponent(this.getDescriptionId() + i +".ctrl.desc");
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
            Potion[] potions = getPotionsFromNBT(info);
            if (attacker instanceof Player) {

                boolean active = stack.getTag().getBoolean(ACTIVE);

                if (active && potions != null && getCooldownFromPotionByIndex(info.getInt(INFO), stack,attacker) <= 0 && info.getInt(BLAZE) > 0) {
                    Potion potion = potions[0];
                    for (MobEffectInstance effectInstance : potion.getEffects()) {
                        victim.addEffect(new MobEffectInstance(effectInstance));
                    }
                    info.putInt(BLAZE, info.getInt(BLAZE) - 1);

                    if (info.getInt(BLAZE) == 0) {
                        stack.getTag().putBoolean(ACTIVE,false);
                    }

                    setPotionCooldownByIndex(info.getInt(ACTIVE_POTION), ModConfig.Server.gauntlet_cooldown.get(), stack, attacker);
                }
            }
        }
        return super.hurtEnemy(stack, victim, attacker);
    }

    public static final int C_LINES = 3;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslatableComponent(getDescriptionId() + ".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().withStyle(ChatFormatting.GRAY));

        tooltip.add(new TranslatableComponent(getDescriptionId() + ".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES; i++) {
                tooltip.add(this.getCtrlDescriptions(i).withStyle(ChatFormatting.GRAY));
            }

        Tuple<List<MobEffectInstance>, List<Potion>> tuple = getEffectsFromGauntlet(stack);
        if (tuple == null) return;
        if (tuple.getA().isEmpty()) return;
        tooltip.add(new TextComponent(" "));

        for (MobEffectInstance instance : tuple.getA()) {
            TranslatableComponent effectFormatted = new TranslatableComponent(instance.getDescriptionId());
            effectFormatted.withStyle(instance.getEffect().getCategory().getTooltipFormatting());
            TextComponent amplifier = new TextComponent("");
            TextComponent duration;
            TranslatableComponent product;
            if (instance.getAmplifier() > 0) {
                amplifier = new TextComponent(String.valueOf(instance.getAmplifier()));
            }
            if (instance.getDuration() > 1) {
                duration = new TextComponent(MobEffectUtil.formatDuration(instance, 1f));
                product = new TranslatableComponent("davespotioneering.tooltip.gauntlet.withDuration", effectFormatted, amplifier, duration);
            } else {
                product = new TranslatableComponent("davespotioneering.tooltip.gauntlet", effectFormatted, amplifier);
            }

            tooltip.add(product);

        }
    }

    public MutableComponent getShiftDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".shift.desc");
    }

    public MutableComponent getCtrlDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".ctrl.desc");
    }

    @Nullable
    public static Tuple<List<MobEffectInstance>, List<Potion>> getEffectsFromGauntlet(ItemStack stack) {
        if (!stack.hasTag()) return null;
        ListTag nbts = stack.getTag().getCompound(INFO).getList(POTIONS, Tag.TAG_STRING);
        List<MobEffectInstance> effects = new ArrayList<>();
        List<Potion> potions = new ArrayList<>();
        for (Tag inbt : nbts) {
            if (inbt instanceof StringTag stringNBT) {
                Potion potion = Registry.POTION.get(new ResourceLocation(stringNBT.getAsString()));
                effects.addAll(potion.getEffects());
                potions.add(potion);
            }
        }
        return new Tuple<>(effects, potions);
    }

    public static void cycleGauntletForward(Player player) {
        if (player == null) return;
        CompoundTag info = player.getMainHandItem().getOrCreateTag().getCompound(INFO);
        ListTag nbts = info.getList(POTIONS, Tag.TAG_STRING);
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
        ListTag nbts = info.getList(POTIONS, Tag.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt(ACTIVE_POTION);
        index--;
        if (index < 0) {
            index = 5;
        }
        info.putInt(ACTIVE_POTION, index);
    }

    public static Potion[] getPotionsFromNBT(CompoundTag info) {
        ListTag nbts = info.getList(POTIONS, Tag.TAG_STRING);
        if (nbts.isEmpty()) return null;

        // get active potion
        int index = info.getInt(ACTIVE_POTION);
        index--;
        if (index < 0) {
            index = 5;
        }

        // get the potion in front of active potion
        Tag pre = nbts.get(index);
        if (pre == null) return null;

        index += 2;
        index %= 6;

        // get the potion behind of active potion
        Tag post = nbts.get(index);
        if (post == null) return null;

        Potion activePotion = Registry.POTION.get(new ResourceLocation(nbts.get(info.getInt(ACTIVE_POTION)).getAsString()));
        Potion prePotion = Registry.POTION.get(new ResourceLocation(pre.getAsString()));
        Potion postPotion = Registry.POTION.get(new ResourceLocation(post.getAsString()));

        return new Potion[]{activePotion, prePotion, postPotion};
    }

    public static void setPotionCooldownByIndex(int index, int cooldown, ItemStack stack, LivingEntity living) {
        CompoundTag persistent = living.getPersistentData();
        CompoundTag tag;
        if (!persistent.contains(DavesPotioneering.MODID)) {
            tag = new CompoundTag();
        } else {
            tag = persistent.getCompound(DavesPotioneering.MODID);
        }

        persistent.put(DavesPotioneering.MODID,tag);

        int[] cooldowns = tag.getIntArray(COOLDOWNS);

        if (cooldowns.length == 0) {
            cooldowns = new int[6];
        }
        cooldowns[index] = cooldown;
        tag.putIntArray(COOLDOWNS,cooldowns);
    }

    private static int getCooldownFromPotionByIndex(int indexOfPotion, ItemStack stack,LivingEntity living) {
        CompoundTag tag = living.getPersistentData().getCompound(DavesPotioneering.MODID);
        int[] cooldowns = tag.getIntArray(COOLDOWNS);
        if (cooldowns.length == 0) return 0;
        return cooldowns[indexOfPotion];
    }

    public static void tickCooldowns(LivingEntity living) {
        CompoundTag tag = living.getPersistentData().getCompound(DavesPotioneering.MODID);
        int[] cooldowns = tag.getIntArray(COOLDOWNS);
        if (cooldowns.length == 0) return;
        boolean sync = false;
        for (int i = 0; i < cooldowns.length;i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
                sync = true;
            }
        }
        if (sync) {
            tag.putIntArray(COOLDOWNS, cooldowns);
            PacketHandler.sendToClient(new S2CCooldownPacket(cooldowns),(ServerPlayer)living);
        }
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
