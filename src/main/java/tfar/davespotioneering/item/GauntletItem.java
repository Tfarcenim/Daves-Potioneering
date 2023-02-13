package tfar.davespotioneering.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GauntletItem extends SwordItem {

    public static final String ACTIVE = "active";
    public static final String ACTIVE_POTION = "activePotionIndex";
    public static final String BLAZE = "blaze";
    public static final String INFO = "info";
    public static final String COOLDOWNS = "potionCooldownMap";
    public static final String POTIONS = "potions";
    public static final int SLOTS = 6;

    public GauntletItem(Properties properties) {
        super(ItemTier.NETHERITE, 4, -2.8f, properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
//                PacketHandler.sendToClient(new GauntletHUDMovementGuiPacket(), (ServerPlayerEntity) playerIn);


            boolean active = stack.getOrCreateTag().getBoolean(ACTIVE);

            int blaze = getBlaze(stack);

            if (!world.isRemote && (blaze > 0 || active)) {
                stack.getOrCreateTag().putBoolean(ACTIVE, !active);
                world.playSound(null,playerIn.getPosX(),playerIn.getPosY(),playerIn.getPosZ(),active ? ModSoundEvents.GAUNTLET_TURNING_OFF : ModSoundEvents.GAUNTLET_TURNING_ON, SoundCategory.PLAYERS,.5f,1);

            } else {
            }
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultPass(stack);
    }

    @Override
    public int getDamage(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        double blaze = 0;
        if (tag != null) {
            CompoundNBT info = tag.getCompound(INFO);
            blaze = info.getInt(BLAZE);
        }
        return PotionInjectorMenu.BLAZE_CAP - (int) blaze;
    }

    @Override
    public boolean isDamageable() {
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
    public boolean showDurabilityBar(ItemStack stack) {
        CompoundNBT info = stack.getOrCreateTag().getCompound(INFO);
        double blaze = info.getInt(BLAZE);
        return blaze > 0;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return TextFormatting.GOLD.getColor();
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        if (stack.getItem() instanceof GauntletItem) {
            CompoundNBT info = stack.getOrCreateTag().getCompound(INFO);
            Potion[] potions = getPotionsFromNBT(info);
            if (attacker instanceof PlayerEntity) {

                boolean active = stack.getTag().getBoolean(ACTIVE);

                if (potions != null && getCooldownFromPotionByIndex(info.getInt(INFO), stack) <= 0 && info.getInt(BLAZE) > 0 && active) {
                    Potion potion = potions[0];
                    for (EffectInstance effectInstance : potion.getEffects()) {
                        victim.addPotionEffect(new EffectInstance(effectInstance));
                    }
                    info.putInt(BLAZE, info.getInt(BLAZE) - 1);

                    if (info.getInt(BLAZE) == 0) {
                        stack.getTag().putBoolean(ACTIVE,false);
                    }

                    ListNBT cooldownMap;
                    if (info.get(COOLDOWNS) instanceof ListNBT) {
                        cooldownMap = (ListNBT) info.get(COOLDOWNS);

                    } else {
                        cooldownMap = new ListNBT();
                        cooldownMap.add(0, new IntArrayNBT(new ArrayList<>()));
                        cooldownMap.add(1, new IntArrayNBT(new ArrayList<>()));
                    }
                    addPotionCooldownByIndex(info.getInt(ACTIVE_POTION), ModConfig.Server.gauntlet_cooldown.get(), stack, cooldownMap);
                }
            }
        }
        return super.hitEntity(stack, victim, attacker);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().mergeStyle(TextFormatting.GRAY));

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().mergeStyle(TextFormatting.GRAY));

        Tuple<List<EffectInstance>, List<Potion>> tuple = getEffectsFromGauntlet(stack);
        if (tuple == null) return;
        if (tuple.getA().isEmpty()) return;
        tooltip.add(new StringTextComponent(" "));

        for (EffectInstance instance : tuple.getA()) {
            TranslationTextComponent effectFormatted = new TranslationTextComponent(instance.getEffectName());
            effectFormatted.mergeStyle(instance.getPotion().getEffectType().getColor());
            StringTextComponent amplifier = new StringTextComponent("");
            StringTextComponent duration;
            TranslationTextComponent product;
            if (instance.getAmplifier() > 0) {
                amplifier = new StringTextComponent(String.valueOf(instance.getAmplifier()));
            }
            if (instance.getDuration() > 1) {
                duration = new StringTextComponent(EffectUtils.getPotionDurationString(instance, 1f));
                product = new TranslationTextComponent("davespotioneering.tooltip.gauntlet.withDuration", effectFormatted, amplifier, duration);
            } else {
                product = new TranslationTextComponent("davespotioneering.tooltip.gauntlet", effectFormatted, amplifier);
            }

            tooltip.add(product);

            }


    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (entity instanceof PlayerEntity && !entity.getEntityWorld().isRemote()) {
            modifyCooldowns(stack,(cd) -> {
                if (cd > 0) cd -= 1;
                return cd;
            });
        }
    }

    public IFormattableTextComponent getShiftDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".shift.desc");
    }

    public IFormattableTextComponent getCtrlDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".ctrl.desc");
    }

    @Nullable
    public static Tuple<List<EffectInstance>, List<Potion>> getEffectsFromGauntlet(ItemStack stack) {
        if (!stack.hasTag()) return null;
        ListNBT nbts = stack.getTag().getCompound(INFO).getList(POTIONS, Constants.NBT.TAG_STRING);
        List<EffectInstance> effects = new ArrayList<>();

        List<Potion> potions = new ArrayList<>();
        for (INBT inbt : nbts) {
            if (inbt instanceof StringNBT) {
                StringNBT stringNBT = (StringNBT) inbt;
                Potion potion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(stringNBT.getString()));
                if (potion != null) {
                    effects.addAll(potion.getEffects());
                    potions.add(potion);
                }
            }
        }
        return new Tuple<>(effects, potions);
    }

    public static void cycleGauntletForward(PlayerEntity player) {
        if (player == null) return;
        CompoundNBT info = player.getHeldItemMainhand().getOrCreateTag().getCompound(INFO);
        ListNBT nbts = info.getList(POTIONS, Constants.NBT.TAG_STRING);

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
        CompoundNBT info = player.getHeldItemMainhand().getOrCreateTag().getCompound(INFO);
        ListNBT nbts = info.getList(POTIONS, Constants.NBT.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt(ACTIVE_POTION);
        index--;
        if (index < 0) {
            index = 5;
        }
        info.putInt(ACTIVE_POTION, index);
    }

    public static Potion[] getPotionsFromNBT(CompoundNBT info) {
        ListNBT nbts = info.getList(POTIONS, Constants.NBT.TAG_STRING);
        if (nbts.isEmpty()) return null;

        // get active potion
        int index = info.getInt(ACTIVE_POTION);
        index--;
        if (index < 0) {
            index = 5;
        }

        // get the potion in front of active potion
        INBT pre = nbts.get(index);
        if (pre == null) return null;

        index += 2;
        index %= 6;

        // get the potion behind of active potion
        INBT post = nbts.get(index);
        if (post == null) return null;

        Potion activePotion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(nbts.get(info.getInt(ACTIVE_POTION)).getString()));
        Potion prePotion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(pre.getString()));
        Potion postPotion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(post.getString()));
        return new Potion[]{activePotion, prePotion, postPotion};
    }

    public static ListNBT addPotionCooldownByIndex(int index, int cooldown, ItemStack stack, ListNBT cooldownMap) {
        CompoundNBT info = stack.getOrCreateTag().getCompound(INFO);
        if (cooldownMap.get(0) instanceof IntArrayNBT) {
            if (cooldownMap.get(1) instanceof IntArrayNBT) {
                IntArrayNBT indexArray = (IntArrayNBT) cooldownMap.get(0);
                IntArrayNBT cooldownArray = (IntArrayNBT) cooldownMap.get(1);

                indexArray.add(IntNBT.valueOf(index));
                cooldownArray.add(IntNBT.valueOf(cooldown));

                ListNBT list = new ListNBT();
                list.add(0, indexArray);
                list.add(1, cooldownArray);

                info.put(COOLDOWNS, list);
                return list;
            }
        }
        return cooldownMap;
    }

    public static int getCooldownFromPotionByIndex(int indexOfPotion, ItemStack stack) {
        CompoundNBT info = stack.getOrCreateTag().getCompound(INFO);
        INBT inbt = info.get(COOLDOWNS);
        if (inbt instanceof ListNBT) {
            ListNBT cooldownMap = (ListNBT) inbt;
            if (cooldownMap.get(0) instanceof IntArrayNBT) {
                if (cooldownMap.get(1) instanceof IntArrayNBT) {
                    IntArrayNBT indexArray = (IntArrayNBT) cooldownMap.get(0);
                    IntArrayNBT cooldownArray = (IntArrayNBT) cooldownMap.get(1);

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
        CompoundNBT info = gauntlet.getOrCreateTag().getCompound(INFO);
        INBT inbt = info.get(COOLDOWNS);
        if (inbt instanceof ListNBT) {
            ListNBT map = (ListNBT) inbt;
            if (map.get(0) instanceof IntArrayNBT && map.get(1) instanceof IntArrayNBT) {
                IntArrayNBT cooldownArray = (IntArrayNBT) map.get(1);
                IntArrayNBT indexArray = (IntArrayNBT) map.get(0);
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
                IntArrayNBT newArray = new IntArrayNBT(cooldownList);
                IntArrayNBT newArrayIndex = new IntArrayNBT(indexList);
                map.set(1, newArray);
                map.set(0, newArrayIndex);
            }
        }
    }

    public static List<Integer> toList(int... in) {
        List<Integer> list = new ArrayList<>();
        for (int i : in) {
            list.add(i);
        }
        return list;
    }

    public static int getBlaze(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        int blaze = 0;
        if (tag != null) {
            CompoundNBT info = tag.getCompound(INFO);
            blaze = info.getInt(BLAZE);
        }
        return blaze;
    }
}
