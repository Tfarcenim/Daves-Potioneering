package tfar.davespotioneering;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tfar.davespotioneering.mixin.ItemAccess;

import java.util.List;

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

    public static void dropContents(Level pLevel, BlockPos pPos, List<ItemStack> pStackList) {
        pStackList.forEach(stack -> Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), stack));
    }
    public enum CoatingType {
        TOOL,FOOD,ANY;

        public static CoatingType getCoatingType(ItemStack stack) {
            if (DavesPotioneering.canBeTipped(stack)) {
                return TOOL;
            }
            else if (stack.getItem().isEdible()) {
                return FOOD;
            }
            return ANY;
        }
    }

    public static boolean isPotion(ItemStack stack) {
        return stack.getItem() instanceof PotionItem;
    }
}
