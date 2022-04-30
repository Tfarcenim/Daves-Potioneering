package tfar.davespotioneering;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

//can't use protection class because of the enum
import net.minecraft.enchantment.Enchantment.Rarity;

public class MagicProtectionEnchantment extends Enchantment {

    public MagicProtectionEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getDamageProtection(int level, DamageSource source) {
        if (source.isMagic()) {
            return level * 2;
        }
        return super.getDamageProtection(level, source);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return ModConfig.Server.magic_protection.get() && super.canEnchant(stack);
    }
}
