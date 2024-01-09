package tfar.davespotioneering;

import tfar.davespotioneering.mixin.ItemAccess;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Util {

    public static void setStackSize(Item item, int count) {
        ((ItemAccess) item).setMaxStackSize(count);
    }

    //brewing xp is determined by the ingredient used, more valuable ingredients should give more xp
    public static double getBrewXp(ItemStack stack) {
        return stack.getRarity() == Rarity.RARE ? 10 : 7;
    }

    public static void splitAndSpawnExperience(Level world, Vec3 pos, double experience) {
        world.addFreshEntity(new ExperienceOrb(world, pos.x, pos.y, pos.z, (int) experience));
    }

    public static void brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] potionIndexes) {
        for (int i : potionIndexes) {
            ItemStack potion = inputs.get(i);
            ItemStack output = PotionBrewing.mix(ingredient, potion);
            output.setCount(inputs.get(i).getCount());
            if (!output.isEmpty()) {
                inputs.set(i, output);
            }
        }
    }

    public static void dropContents(Level pLevel, BlockPos pPos, List<ItemStack> pStackList) {
        pStackList.forEach(stack -> Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), stack));
    }

    public static boolean isValidInputCountInsensitive(ItemStack stack) {
       return BrewingStandMenu.PotionSlot.mayPlaceItem(stack);
    }

    public enum CoatingType {
        TOOL,FOOD,ANY;

        public static CoatingType getCoatingType(ItemStack stack) {
            if (stack.getItem() instanceof TieredItem) {
                return TOOL;
            }
            else if (stack.getItem().isEdible()) {
                return FOOD;
            }
            return ANY;
        }
    }


}
