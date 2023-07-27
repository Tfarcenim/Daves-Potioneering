package tfar.davespotioneering;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tfar.davespotioneering.mixin.ItemAccess;

import java.util.List;

public class Util {

    public static void setStackSize(Item item, int count) {
        ((ItemAccess) item).setMaxCount(count);
    }

    //brewing xp is determined by the ingredient used, more valuable ingredients should give more xp
    public static double getBrewXp(ItemStack stack) {
        return stack.getRarity() == Rarity.RARE ? 10 : 7;
    }

    public static void splitAndSpawnExperience(World world, Vec3d pos, double experience) {
        world.spawnEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, (int) experience));
    }

    public static void brewPotions(DefaultedList<ItemStack> inputs, ItemStack ingredient, int[] potionIndexes) {
        for (int i : potionIndexes) {
            ItemStack potion = inputs.get(i);
            ItemStack output = BrewingRecipeRegistry.craft(ingredient, potion);
            output.setCount(inputs.get(i).getCount());
            if (!output.isEmpty()) {
                inputs.set(i, output);
            }
        }
    }

    public static void dropContents(World pLevel, BlockPos pPos, List<ItemStack> pStackList) {
        pStackList.forEach(stack -> ItemScatterer.spawn(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), stack));
    }

    public static boolean isValidInputCountInsensitive(ItemStack stack) {
       return BrewingStandScreenHandler.PotionSlot.matches(stack);
    }

    public enum CoatingType {
        TOOL,FOOD,ANY;

        public static CoatingType getCoatingType(ItemStack stack) {
            if (stack.getItem() instanceof ToolItem) {
                return TOOL;
            }
            else if (stack.getItem().isFood()) {
                return FOOD;
            }
            return ANY;
        }
    }


}
