package tfar.davespotioneering.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (stack.hasTag()) {
            CompoundNBT info = stack.getTag().getCompound("info");

            ListNBT potions = info.getList("potions", Constants.NBT.TAG_STRING);
            for (INBT inbt : potions) {
                if (inbt instanceof StringNBT) {
                    StringNBT stringNBT = (StringNBT)inbt;
                    tooltip.add(new StringTextComponent(stringNBT.getString()));
                }
            }
            double blaze = info.getInt("blaze");
            tooltip.add(new StringTextComponent("Blaze: "+blaze));
        }
    }

    private static void toggleGauntlet(ItemStack stack) {
        boolean b = stack.getOrCreateTag().getBoolean("active");
        stack.getOrCreateTag().putBoolean("active",!b);
    }
}
