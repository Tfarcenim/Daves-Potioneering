package tfar.davespotioneering;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

//can't use protection class because of the enum
public class MagicProtectionEnchantment extends Enchantment {

    public MagicProtectionEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int calcModifierDamage(int level, DamageSource source) {
        if (source.isMagicDamage()) {
            return level * 2;
        }
        return super.calcModifierDamage(level, source);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return ModConfig.Server.magic_protection.get() && super.canApply(stack);
    }
}
