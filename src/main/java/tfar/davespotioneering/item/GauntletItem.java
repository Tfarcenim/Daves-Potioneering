package tfar.davespotioneering.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Potion;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.menu.GauntletMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GauntletItem extends SwordItem {
    private static PlayerEntity player;

    public GauntletItem(Properties properties) {
        super(ItemTier.STONE, 7, -2.8f, properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
//                PacketHandler.sendToClient(new GauntletHUDMovementGuiPacket(), (ServerPlayerEntity) playerIn);
                toggleGauntlet(stack);
            }
        }
        return ActionResult.resultSuccess(stack);
    }

    @Override
    public int getDamage(ItemStack stack) {
        CompoundNBT info = stack.getOrCreateTag().getCompound("info");
        double blaze = info.getInt("blaze");
        return GauntletMenu.BLAZE_CAP - (int) blaze;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        if (stack.getItem() instanceof GauntletItem) {
            CompoundNBT info = stack.getOrCreateTag().getCompound("info");
            Potion[] potions = getPotionsFromNBT(info);
            if (attacker instanceof PlayerEntity) {
                if (potions != null && getCooldownFromPotionByIndex(info.getInt("activePotionIndex"), stack) <= 0 && info.getInt("blaze") > 0 && stack.getOrCreateTag().getBoolean("active")) {
                    Potion potion = potions[0];
                    for (EffectInstance effectInstance : potion.getEffects()) {
                        victim.addPotionEffect(new EffectInstance(effectInstance));
                    }
                    info.putInt("blaze", info.getInt("blaze") - 1);
                    ListNBT cooldownMap;
                    if (info.get("potionCooldownMap") instanceof ListNBT) {
                        cooldownMap = (ListNBT) info.get("potionCooldownMap");
                    } else {
                        cooldownMap = new ListNBT();
                        cooldownMap.add(0, new IntArrayNBT(new ArrayList<>()));
                        cooldownMap.add(1, new IntArrayNBT(new ArrayList<>()));
                    }
                    addPotionCooldownByIndex(info.getInt("activePotionIndex"), ModConfig.Server.gauntlet_cooldown.get(), stack, cooldownMap);
                }
            }
        }
        return super.hitEntity(stack, victim, attacker);
    }

    @Override
    public boolean isRepairable(ItemStack p_isRepairable_1_) {
        return false;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return TextFormatting.GOLD.getColor();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT info = stack.getOrCreateTag().getCompound("info");
        double blaze = info.getInt("blaze");
        tooltip.add(new StringTextComponent("Blaze: " + blaze));

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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
        if (entity instanceof PlayerEntity && !entity.getEntityWorld().isRemote()) {
            GauntletItem.player = (PlayerEntity) entity;
            ItemStack gauntletInstance = new ItemStack(ModItems.ALCHEMICAL_GAUNTLET);
            if (player.inventory.hasItemStack(gauntletInstance)) {
                List<ItemStack> gauntlets = getItemsFromInventory(gauntletInstance, player.inventory);
                for (ItemStack gauntlet : gauntlets) {
                    modifyCooldowns(gauntlet, (cd) -> {
                        if (cd > 0) cd -= 1;
                        return cd;
                    });
                }
            }
        }
    }

    private static void toggleGauntlet(ItemStack stack) {
        boolean b = stack.getOrCreateTag().getBoolean("active");
        stack.getOrCreateTag().putBoolean("active", !b);
    }

    @Nullable
    public static Tuple<List<EffectInstance>, List<Potion>> getEffectsFromGauntlet(ItemStack stack) {
        if (!stack.hasTag()) return null;
        ListNBT nbts = stack.getTag().getCompound("info").getList("potions", Constants.NBT.TAG_STRING);
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

    public static void cycleGauntletForward() {
        if (player == null) return;
        CompoundNBT info = player.getHeldItemMainhand().getOrCreateTag().getCompound("info");
        ListNBT nbts = info.getList("potions", Constants.NBT.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt("activePotionIndex");
        index++;
        if (index > 5) {
            index = 0;
        }
        info.putInt("activePotionIndex", index);
    }

    public static void cycleGauntletBackward() {
        if (player == null) return;
        CompoundNBT info = player.getHeldItemMainhand().getOrCreateTag().getCompound("info");
        ListNBT nbts = info.getList("potions", Constants.NBT.TAG_STRING);
        if (nbts.isEmpty()) return;
        int index = info.getInt("activePotionIndex");
        index--;
        if (index < 0) {
            index = 5;
        }
        info.putInt("activePotionIndex", index);
    }

    public static Potion[] getPotionsFromNBT(CompoundNBT info) {
        ListNBT nbts = info.getList("potions", Constants.NBT.TAG_STRING);
        if (nbts.isEmpty()) return null;

        // get active potion
        int index = info.getInt("activePotionIndex");
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

        Potion activePotion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(nbts.get(info.getInt("activePotionIndex")).getString()));
        Potion prePotion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(pre.getString()));
        Potion postPotion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(post.getString()));

        return new Potion[]{activePotion, prePotion, postPotion};
    }

    public static ListNBT addPotionCooldownByIndex(int index, int cooldown, ItemStack stack, ListNBT cooldownMap) {
        CompoundNBT info = stack.getOrCreateTag().getCompound("info");
        if (cooldownMap.get(0) instanceof IntArrayNBT) {
            if (cooldownMap.get(1) instanceof IntArrayNBT) {
                IntArrayNBT indexArray = (IntArrayNBT) cooldownMap.get(0);
                IntArrayNBT cooldownArray = (IntArrayNBT) cooldownMap.get(1);

                indexArray.add(IntNBT.valueOf(index));
                cooldownArray.add(IntNBT.valueOf(cooldown));

                ListNBT list = new ListNBT();
                list.add(0, indexArray);
                list.add(1, cooldownArray);

                info.put("potionCooldownMap", list);
                return list;
            }
        }
        return cooldownMap;
    }

    public static int getCooldownFromPotionByIndex(int indexOfPotion, ItemStack stack) {
        CompoundNBT info = stack.getOrCreateTag().getCompound("info");
        INBT inbt = info.get("potionCooldownMap");
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
        CompoundNBT info = gauntlet.getOrCreateTag().getCompound("info");
        INBT inbt = info.get("potionCooldownMap");
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

    public static List<ItemStack> getItemsFromInventory(ItemStack item, PlayerInventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack stack : inventory.mainInventory) {
            if (stack.isItemEqual(item)) items.add(stack);
        }
        for (ItemStack stack : inventory.offHandInventory) {
            if (stack.isItemEqual(item)) items.add(stack);
        }
        for (ItemStack stack : inventory.armorInventory) {
            if (stack.isItemEqual(item)) items.add(stack);
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
}
