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

    public GauntletItem(Settings properties) {
        super(ToolMaterials.NETHERITE, 4, -2.8f, properties);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getStackInHand(handIn);
        if (playerIn.isSneaking()) {


            boolean active = stack.getOrCreateTag().getBoolean("active");

            int blaze = stack.getMaxDamage() - stack.getDamage();

            if (!world.isClient && (blaze > 0 || active)) {
                stack.getOrCreateTag().putBoolean("active", !active);
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
            CompoundTag info = stack.getOrCreateTag().getCompound("info");
            Potion[] potions = getPotionsFromNBT(info);
            if (attacker instanceof PlayerEntity) {

                boolean active = stack.getTag().getBoolean("active");

                if (potions != null && getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), stack) <= 0 && (stack.getMaxDamage() - stack.getDamage()) > 0 && active) {
                    Potion potion = potions[0];
                    for (StatusEffectInstance effectInstance : potion.getEffects()) {
                        victim.addStatusEffect(new StatusEffectInstance(effectInstance));
                    }

                    stack.damage(1, attacker, livingEntity -> {
                    });

                    if (stack.getDamage() == stack.getMaxDamage()) {
                        stack.getTag().putBoolean("active", false);
                    }

                    ListTag cooldownMap;
                    if (info.get("potionCooldownMap") instanceof ListTag) {
                        cooldownMap = (ListTag) info.get("potionCooldownMap");
                    } else {
                        cooldownMap = new ListTag();
                        cooldownMap.add(0, new IntArrayTag(new ArrayList<>()));
                        cooldownMap.add(1, new IntArrayTag(new ArrayList<>()));
                    }
                    addPotionCooldownByIndex(info.getInt("activePotionIndex"), ClothConfig.gauntlet_cooldown, stack, cooldownMap);
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
            effectFormatted.formatted(instance.getEffectType().getType().getFormatting());
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
        if (!stack.hasTag()) return null;
        ListTag nbts = stack.getTag().getCompound("info").getList("potions", PotionInjectorMenu.TAG_STRING);//StringTag?
        List<StatusEffectInstance> effects = new ArrayList<>();
        List<Potion> potions = new ArrayList<>();
        for (Tag inbt : nbts) {
            if (inbt instanceof StringTag) {
                StringTag stringNBT = (StringTag) inbt;
                Potion potion = Registry.POTION.get(new Identifier(stringNBT.asString()));
                effects.addAll(potion.getEffects());
                potions.add(potion);
            }
        }
        return new Pair<>(effects, potions);
    }

    public static void cycleGauntletForward(PlayerEntity player) {
        if (player == null) return;
        CompoundTag info = player.getMainHandStack().getOrCreateTag().getCompound("info");
        ListTag nbts = info.getList("potions", PotionInjectorMenu.TAG_STRING);
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
        CompoundTag info = player.getMainHandStack().getOrCreateTag().getCompound("info");
        ListTag nbts = info.getList("potions", PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt("activePotionIndex");
        index--;
        if (index < 0) {
            index = 5;
        }
        info.putInt("activePotionIndex", index);
    }

    public static Potion[] getPotionsFromNBT(CompoundTag info) {
        ListTag nbts = info.getList("potions", PotionInjectorMenu.TAG_STRING);
        if (nbts.isEmpty()) return null;

        // get active potion
        int index = info.getInt("activePotionIndex");
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

        Potion activePotion = Registry.POTION.get(new Identifier(nbts.get(info.getInt("activePotionIndex")).asString()));
        Potion prePotion = Registry.POTION.get(new Identifier(pre.asString()));
        Potion postPotion = Registry.POTION.get(new Identifier(post.asString()));

        return new Potion[]{activePotion, prePotion, postPotion};
    }

    public static ListTag addPotionCooldownByIndex(int index, int cooldown, ItemStack stack, ListTag cooldownMap) {
        CompoundTag info = stack.getOrCreateTag().getCompound("info");
        if (cooldownMap.get(0) instanceof IntArrayTag) {
            if (cooldownMap.get(1) instanceof IntArrayTag) {
                IntArrayTag indexArray = (IntArrayTag) cooldownMap.get(0);
                IntArrayTag cooldownArray = (IntArrayTag) cooldownMap.get(1);

                indexArray.add(IntTag.of(index));
                cooldownArray.add(IntTag.of(cooldown));

                ListTag list = new ListTag();
                list.add(0, indexArray);
                list.add(1, cooldownArray);

                info.put("potionCooldownMap", list);
                return list;
            }
        }
        return cooldownMap;
    }

    public static int getCooldownFromPotionByIndex(int indexOfPotion, ItemStack stack) {
        CompoundTag info = stack.getOrCreateTag().getCompound("info");
        Tag inbt = info.get("potionCooldownMap");
        if (inbt instanceof ListTag) {
            ListTag cooldownMap = (ListTag) inbt;
            if (cooldownMap.get(0) instanceof IntArrayTag) {
                if (cooldownMap.get(1) instanceof IntArrayTag) {
                    IntArrayTag indexArray = (IntArrayTag) cooldownMap.get(0);
                    IntArrayTag cooldownArray = (IntArrayTag) cooldownMap.get(1);
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
        CompoundTag info = gauntlet.getOrCreateTag().getCompound("info");
        Tag inbt = info.get("potionCooldownMap");
        if (inbt instanceof ListTag) {
            ListTag map = (ListTag) inbt;
            if (map.get(0) instanceof IntArrayTag && map.get(1) instanceof IntArrayTag) {
                IntArrayTag cooldownArray = (IntArrayTag) map.get(1);
                IntArrayTag indexArray = (IntArrayTag) map.get(0);
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
                IntArrayTag newArray = new IntArrayTag(cooldownList);
                IntArrayTag newArrayIndex = new IntArrayTag(indexList);
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
