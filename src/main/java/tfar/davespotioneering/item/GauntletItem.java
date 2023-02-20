package tfar.davespotioneering.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.*;
import net.minecraft.potion.Potion;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GauntletItem extends SwordItem implements Perspective {


    public static final String ACTIVE = "active";
    public static final String ACTIVE_POTION = "activePotionIndex";
    public static final String BLAZE = "blaze";
    public static final String INFO = "info";
    public static final String COOLDOWNS = "potionCooldownMap";
    public static final String POTIONS = "potions";
    public static final int SLOTS = 6;

    public GauntletItem(Settings properties) {
        super(ToolMaterials.NETHERITE, 4, -2.8f, properties);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getStackInHand(handIn);
        if (playerIn.isSneaking()) {
//                PacketHandler.sendToClient(new GauntletHUDMovementGuiPacket(), (ServerPlayerEntity) playerIn);


            boolean active = stack.getOrCreateNbt().getBoolean(ACTIVE);

            int blaze = stack.getMaxDamage() - stack.getDamage();

            if (!world.isClient && (blaze > 0 || active)) {
                stack.getOrCreateNbt().putBoolean(ACTIVE, !active);
                world.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), active ? ModSoundEvents.GAUNTLET_TURNING_OFF : ModSoundEvents.GAUNTLET_TURNING_ON, SoundCategory.PLAYERS, .5f, 1);
            } else {
            }
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    // @Override
    //  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    //      return oldStack.getItem() != newStack.getItem();
    //  }

    //  @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Formatting.GOLD.getColorValue();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        if (stack.getItem() instanceof GauntletItem) {
            NbtCompound info = stack.getOrCreateNbt().getCompound(INFO);
            Potion[] potions = getPotionsFromNBT(info);
            if (attacker instanceof PlayerEntity) {

                boolean active = stack.getNbt().getBoolean(ACTIVE);

                if (potions != null && getCooldownFromPotionByIndex(info.getInt(ACTIVE_POTION), stack) <= 0 && (stack.getMaxDamage() - stack.getDamage()) > 0 && active) {
                    Potion potion = potions[0];
                    for (StatusEffectInstance effectInstance : potion.getEffects()) {
                        victim.addStatusEffect(new StatusEffectInstance(effectInstance));
                    }

                    stack.damage(1, attacker, livingEntity -> {
                    });

                    if (stack.getDamage() == stack.getMaxDamage()) {
                        stack.getNbt().putBoolean(ACTIVE, false);
                    }

                    NbtList cooldownMap;
                    if (info.get(COOLDOWNS) instanceof NbtList) {
                        cooldownMap = (NbtList) info.get(COOLDOWNS);
                    } else {
                        cooldownMap = new NbtList();
                        cooldownMap.add(0, new NbtIntArray(new ArrayList<>()));
                        cooldownMap.add(1, new NbtIntArray(new ArrayList<>()));
                    }
                    addPotionCooldownByIndex(info.getInt(ACTIVE_POTION), ClothConfig.gauntlet_cooldown, stack, cooldownMap);
                }
            }
        }
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslatableText(getTranslationKey() + ".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().formatted(Formatting.GRAY));

        tooltip.add(new TranslatableText(getTranslationKey() + ".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().formatted(Formatting.GRAY));

        Pair<List<StatusEffectInstance>, List<Potion>> tuple = getEffectsFromGauntlet(stack);
        if (tuple == null) return;
        if (tuple.getLeft().isEmpty()) return;
        tooltip.add(new LiteralText(" "));

        for (StatusEffectInstance instance : tuple.getLeft()) {
            TranslatableText effectFormatted = new TranslatableText(instance.getTranslationKey());
            effectFormatted.formatted(instance.getEffectType().getCategory().getFormatting());
            LiteralText amplifier = new LiteralText("");
            LiteralText duration;
            TranslatableText product;
            if (instance.getAmplifier() > 0) {
                amplifier = new LiteralText(String.valueOf(instance.getAmplifier()));
            }
            if (instance.getDuration() > 1) {
                duration = new LiteralText(StatusEffectUtil.durationToString(instance, 1f));
                product = new TranslatableText("davespotioneering.tooltip.gauntlet.withDuration", effectFormatted, amplifier, duration);
            } else {
                product = new TranslatableText("davespotioneering.tooltip.gauntlet", effectFormatted, amplifier);
            }

            tooltip.add(product);

        }


    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (entity instanceof PlayerEntity && !entity.getEntityWorld().isClient()) {
            modifyCooldowns(stack, (cd) -> {
                if (cd > 0) cd -= 1;
                return cd;
            });
        }
    }

    public MutableText getShiftDescription() {
        return new TranslatableText(this.getTranslationKey() + ".shift.desc");
    }

    public MutableText getCtrlDescription() {
        return new TranslatableText(this.getTranslationKey() + ".ctrl.desc");
    }

    @Nullable
    public static Pair<List<StatusEffectInstance>, List<Potion>> getEffectsFromGauntlet(ItemStack stack) {
        if (!stack.hasNbt()) return null;
        NbtList nbts = stack.getNbt().getCompound(INFO).getList(POTIONS, PotionInjectorMenu.TAG_STRING);//NbtString?
        List<StatusEffectInstance> effects = new ArrayList<>();
        List<Potion> potions = new ArrayList<>();
        for (NbtElement inbt : nbts) {
            if (inbt instanceof NbtString stringNBT) {
                Potion potion = Registry.POTION.get(new Identifier(stringNBT.asString()));
                effects.addAll(potion.getEffects());
                potions.add(potion);
            }
        }
        return new Pair<>(effects, potions);
    }

    public static void cycleGauntletForward(PlayerEntity player) {
        if (player == null) return;
        NbtCompound info = player.getMainHandStack().getOrCreateNbt().getCompound(INFO);
        NbtList nbts = info.getList(POTIONS, PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt(ACTIVE_POTION);
        index++;
        if (index > 5) {
            index = 0;
        }
        info.putInt(ACTIVE_POTION, index);
    }

    public static void cycleGauntletBackward(PlayerEntity player) {
        if (player == null) return;
        NbtCompound info = player.getMainHandStack().getOrCreateNbt().getCompound(INFO);
        NbtList nbts = info.getList(POTIONS, PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt(ACTIVE_POTION);
        index--;
        if (index < 0) {
            index = 5;
        }
        info.putInt(ACTIVE_POTION, index);
    }

    public static Potion[] getPotionsFromNBT(NbtCompound info) {
        NbtList nbts = info.getList(POTIONS, PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return null;

        // get active potion
        int index = info.getInt(ACTIVE_POTION);
        index--;
        if (index < 0) {
            index = 5;
        }

        // get the potion in front of active potion
        NbtElement pre = nbts.get(index);
        if (pre == null) return null;

        index += 2;
        index %= 6;

        // get the potion behind of active potion
        NbtElement post = nbts.get(index);
        if (post == null) return null;

        Potion activePotion = Registry.POTION.get(new Identifier(nbts.get(info.getInt(ACTIVE_POTION)).asString()));
        Potion prePotion = Registry.POTION.get(new Identifier(pre.asString()));
        Potion postPotion = Registry.POTION.get(new Identifier(post.asString()));

        return new Potion[]{activePotion, prePotion, postPotion};
    }

    public static NbtList addPotionCooldownByIndex(int index, int cooldown, ItemStack stack, NbtList cooldownMap) {
        NbtCompound info = stack.getOrCreateNbt().getCompound(INFO);
        if (cooldownMap.get(0) instanceof NbtIntArray) {
            if (cooldownMap.get(1) instanceof NbtIntArray cooldownArray) {
                NbtIntArray indexArray = (NbtIntArray) cooldownMap.get(0);

                indexArray.add(NbtInt.of(index));
                cooldownArray.add(NbtInt.of(cooldown));

                NbtList list = new NbtList();
                list.add(0, indexArray);
                list.add(1, cooldownArray);

                info.put(COOLDOWNS, list);
                return list;
            }
        }
        return cooldownMap;
    }

    public static int getCooldownFromPotionByIndex(int indexOfPotion, ItemStack stack) {
        NbtCompound info = stack.getOrCreateNbt().getCompound(INFO);
        NbtElement inbt = info.get(COOLDOWNS);
        if (inbt instanceof NbtList cooldownMap) {
            if (cooldownMap.get(0) instanceof NbtIntArray) {
                if (cooldownMap.get(1) instanceof NbtIntArray cooldownArray) {
                    NbtIntArray indexArray = (NbtIntArray) cooldownMap.get(0);
                    try {
                        int indexOfPotionIndex = toList(indexArray.getIntArray()).indexOf(indexOfPotion);
                        return toList(cooldownArray.getIntArray()).get(indexOfPotionIndex);
                    } catch (Exception ignore) {
                        // if the potion doesn't have cooldown an IndexOutOfBounds exception will be thrown, but it is not an actual problem, so we are just ignoring it
                    }
                }
            }
        }
        return 0;
    }

    public static void modifyCooldowns(ItemStack gauntlet, Function<Integer, Integer> modifier) {
        NbtCompound info = gauntlet.getOrCreateNbt().getCompound(INFO);
        NbtElement inbt = info.get(COOLDOWNS);
        if (inbt instanceof NbtList map) {
            if (map.get(0) instanceof NbtIntArray indexArray && map.get(1) instanceof NbtIntArray cooldownArray) {
                if (cooldownArray.isEmpty() || indexArray.isEmpty()) return;
                if (cooldownArray.getIntArray().length != indexArray.getIntArray().length) return;
                List<Integer> cooldownList = new ArrayList<>();
                List<Integer> indexList = new ArrayList<>();
                for (int i = 0; i < cooldownArray.getIntArray().length; i++) {
                    int modified = modifier.apply(cooldownArray.getIntArray()[i]);
                    // copying over the cooldown and index to a new list, remove ones that are already expired
                    if (modified > 0) {
                        cooldownList.add(modified);
                        indexList.add(indexArray.getIntArray()[i]);
                    }
                }
                NbtIntArray newArray = new NbtIntArray(cooldownList);
                NbtIntArray newArrayIndex = new NbtIntArray(indexList);
                map.set(1, newArray);
                map.set(0, newArrayIndex);
            }
        }
    }

    public static List<ItemStack> getItemsFromInventory(ItemStack item, PlayerInventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack stack : inventory.main) {
            if (stack.isItemEqualIgnoreDamage(item)) items.add(stack);
        }
        for (ItemStack stack : inventory.offHand) {
            if (stack.isItemEqualIgnoreDamage(item)) items.add(stack);
        }
        for (ItemStack stack : inventory.armor) {
            if (stack.isItemEqualIgnoreDamage(item)) items.add(stack);
        }
        return items;
    }

    public static List<Integer> toList(int... in) {
        List<Integer> list = new ArrayList<>();
        for (int i : in) {
            list.add(i);
        }
        return list;
    }

    public static final Identifier ALC_ID = new Identifier(DavesPotioneering.MODID, "item/sprite/potioneer_gauntlet");
    public static final Identifier LIT_ALC_ID = new Identifier(DavesPotioneering.MODID, "item/sprite/lit_potioneer_gauntlet");

    @Override
    public Identifier getGuiModel(boolean active) {
        return active ? LIT_ALC_ID : ALC_ID;
    }
}
