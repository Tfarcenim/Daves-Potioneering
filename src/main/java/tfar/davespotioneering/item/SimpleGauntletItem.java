package tfar.davespotioneering.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class SimpleGauntletItem extends SwordItem {
    private static PlayerEntity player;

    public SimpleGauntletItem(IItemTier tier, int damage, float attackSpeed, Properties properties) {
        super(tier, damage, attackSpeed, properties);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }
}
