package tfar.davespotioneering.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import tfar.davespotioneering.MagicProtectionEnchantment;

public class ModEnchantments {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static final Enchantment MAGIC_PROTECTION = new MagicProtectionEnchantment(Enchantment.Rarity.RARE,EnchantmentCategory.ARMOR,ARMOR_SLOTS);

}
