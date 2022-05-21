package tfar.davespotioneering;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public class MagicProtectionEnchantment extends Enchantment {

    public MagicProtectionEnchantment(Rarity rarityIn, EnchantmentTarget typeIn, EquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (source.getMagic()) {
            return level * 2;
        }
        return super.getProtectionAmount(level, source);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return ModConfig.Server.magic_protection && super.isAcceptableItem(stack);
    }
}
