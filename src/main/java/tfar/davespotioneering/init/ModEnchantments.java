package tfar.davespotioneering.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import tfar.davespotioneering.MagicProtectionEnchantment;

public class ModEnchantments {
    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    public static final Enchantment MAGIC_PROTECTION = new MagicProtectionEnchantment(Enchantment.Rarity.RARE,EnchantmentType.ARMOR,ARMOR_SLOTS);

}
