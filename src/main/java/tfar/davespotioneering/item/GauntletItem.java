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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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

    public GauntletItem(Settings properties) {
        super(ToolMaterials.NETHERITE, 4, -2.8f, properties);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getStackInHand(handIn);
        if (playerIn.isSneaking()) {
//                PacketHandler.sendToClient(new GauntletHUDMovementGuiPacket(), (ServerPlayerEntity) playerIn);


            boolean active = stack.getOrCreateNbt().getBoolean("active");

            int blaze = stack.getMaxDamage() - stack.getDamage();

            if (!world.isClient && (blaze > 0 || active)) {
                stack.getOrCreateNbt().putBoolean("active", !active);
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
            NbtCompound info = stack.getOrCreateNbt().getCompound("info");
            Potion[] potions = getPotionsFromNBT(info);
            if (attacker instanceof PlayerEntity) {

                boolean active = stack.getNbt().getBoolean("active");

                if (potions != null && getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), stack) <= 0 && (stack.getMaxDamage() - stack.getDamage()) > 0 && active) {
                    Potion potion = potions[0];
                    for (StatusEffectInstance effectInstance : potion.getEffects()) {
                        victim.addStatusEffect(new StatusEffectInstance(effectInstance));
                    }

                    stack.damage(1, attacker, livingEntity -> {
                    });

                    if (stack.getDamage() == stack.getMaxDamage()) {
                        stack.getNbt().putBoolean("active", false);
                    }

                    NbtList cooldownMap;
                    if (info.get("potionCooldownMap") instanceof NbtList) {
                        cooldownMap = (NbtList) info.get("potionCooldownMap");
                    } else {
                        cooldownMap = new NbtList();
                        cooldownMap.add(0, new NbtIntArray(new ArrayList<>()));
                        cooldownMap.add(1, new NbtIntArray(new ArrayList<>()));
                    }
                    addPotionCooldownByIndex(info.getInt("activePotionIndex"), DavesPotioneering.CONFIG.gauntlet_cooldown, stack, cooldownMap);
                }
            }
        }
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);

        tooltip.add(Text.translatable(getTranslationKey() + ".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().formatted(Formatting.GRAY));

        tooltip.add(Text.translatable(getTranslationKey() + ".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().formatted(Formatting.GRAY));

        Pair<List<StatusEffectInstance>, List<Potion>> tuple = getEffectsFromGauntlet(stack);
        if (tuple == null) return;
        if (tuple.getLeft().isEmpty()) return;
        tooltip.add(Text.literal(" "));

        for (StatusEffectInstance instance : tuple.getLeft()) {
            MutableText effectFormatted = Text.translatable(instance.getTranslationKey());
            effectFormatted.formatted(instance.getEffectType().getCategory().getFormatting());
            MutableText amplifier = Text.literal("");
            MutableText duration;
            MutableText product;
            if (instance.getAmplifier() > 0) {
                amplifier = Text.literal(String.valueOf(instance.getAmplifier()));
            }
            if (instance.getDuration() > 1) {
                duration = Text.literal(StatusEffectUtil.durationToString(instance, 1f));
                product = Text.translatable("davespotioneering.tooltip.gauntlet.withDuration", effectFormatted, amplifier, duration);
            } else {
                product = Text.translatable("davespotioneering.tooltip.gauntlet", effectFormatted, amplifier);
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
        return Text.translatable(this.getTranslationKey() + ".shift.desc");
    }

    public MutableText getCtrlDescription() {
        return Text.translatable(this.getTranslationKey() + ".ctrl.desc");
    }

    @Nullable
    public static Pair<List<StatusEffectInstance>, List<Potion>> getEffectsFromGauntlet(ItemStack stack) {
        if (!stack.hasNbt()) return null;
        NbtList nbts = stack.getNbt().getCompound("info").getList("potions", PotionInjectorMenu.TAG_STRING);//NbtString?
        List<StatusEffectInstance> effects = new ArrayList<>();
        List<Potion> potions = new ArrayList<>();
        for (NbtElement inbt : nbts) {
            if (inbt instanceof NbtString) {
                NbtString stringNBT = (NbtString) inbt;
                Potion potion = Registry.POTION.get(new Identifier(stringNBT.asString()));
                effects.addAll(potion.getEffects());
                potions.add(potion);
            }
        }
        return new Pair<>(effects, potions);
    }

    public static void cycleGauntletForward(PlayerEntity player) {
        if (player == null) return;
        NbtCompound info = player.getMainHandStack().getOrCreateNbt().getCompound("info");
        NbtList nbts = info.getList("potions", PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt("activePotionIndex");
        index++;
        if (index > 5) {
            index = 0;
        }
        info.putInt("activePotionIndex", index);
    }

    public static void cycleGauntletBackward(PlayerEntity player) {
        if (player == null) return;
        NbtCompound info = player.getMainHandStack().getOrCreateNbt().getCompound("info");
        NbtList nbts = info.getList("potions", PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt("activePotionIndex");
        index--;
        if (index < 0) {
            index = 5;
        }
        info.putInt("activePotionIndex", index);
    }

    public static Potion[] getPotionsFromNBT(NbtCompound info) {
        NbtList nbts = info.getList("potions", PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return null;

        // get active potion
        int index = info.getInt("activePotionIndex");
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

        Potion activePotion = Registry.POTION.get(new Identifier(nbts.get(info.getInt("activePotionIndex")).asString()));
        Potion prePotion = Registry.POTION.get(new Identifier(pre.asString()));
        Potion postPotion = Registry.POTION.get(new Identifier(post.asString()));

        return new Potion[]{activePotion, prePotion, postPotion};
    }

    public static NbtList addPotionCooldownByIndex(int index, int cooldown, ItemStack stack, NbtList cooldownMap) {
        NbtCompound info = stack.getOrCreateNbt().getCompound("info");
        if (cooldownMap.get(0) instanceof NbtIntArray) {
            if (cooldownMap.get(1) instanceof NbtIntArray) {
                NbtIntArray indexArray = (NbtIntArray) cooldownMap.get(0);
                NbtIntArray cooldownArray = (NbtIntArray) cooldownMap.get(1);

                indexArray.add(NbtInt.of(index));
                cooldownArray.add(NbtInt.of(cooldown));

                NbtList list = new NbtList();
                list.add(0, indexArray);
                list.add(1, cooldownArray);

                info.put("potionCooldownMap", list);
                return list;
            }
        }
        return cooldownMap;
    }

    public static int getCooldownFromPotionByIndex(int indexOfPotion, ItemStack stack) {
        NbtCompound info = stack.getOrCreateNbt().getCompound("info");
        NbtElement inbt = info.get("potionCooldownMap");
        if (inbt instanceof NbtList) {
            NbtList cooldownMap = (NbtList) inbt;
            if (cooldownMap.get(0) instanceof NbtIntArray) {
                if (cooldownMap.get(1) instanceof NbtIntArray) {
                    NbtIntArray indexArray = (NbtIntArray) cooldownMap.get(0);
                    NbtIntArray cooldownArray = (NbtIntArray) cooldownMap.get(1);
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
        NbtCompound info = gauntlet.getOrCreateNbt().getCompound("info");
        NbtElement inbt = info.get("potionCooldownMap");
        if (inbt instanceof NbtList) {
            NbtList map = (NbtList) inbt;
            if (map.get(0) instanceof NbtIntArray && map.get(1) instanceof NbtIntArray) {
                NbtIntArray cooldownArray = (NbtIntArray) map.get(1);
                NbtIntArray indexArray = (NbtIntArray) map.get(0);
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
