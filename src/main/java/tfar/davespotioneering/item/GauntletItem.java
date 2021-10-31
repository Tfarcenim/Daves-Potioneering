package tfar.davespotioneering.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GauntletItem extends Item {
    public GauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                toggleGauntlet(stack);
            }
        }
        return ActionResult.resultSuccess(stack);
    }

    private static void toggleGauntlet(ItemStack stack) {
        boolean b = stack.getOrCreateTag().getBoolean("active");
        stack.getOrCreateTag().putBoolean("active",!b);
    }
}
