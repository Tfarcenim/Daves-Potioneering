package tfar.davespotioneering;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MagicProtectionEnchantment extends Enchantment {

    public MagicProtectionEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {
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
        return ModConfig.Server.magic_protection && super.canEnchant(stack);
    }
}
