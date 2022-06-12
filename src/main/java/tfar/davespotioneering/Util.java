package tfar.davespotioneering;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tfar.davespotioneering.mixin.ItemAccess;

public class Util {

    public static void setStackSize(Item item, int count) {
        ((ItemAccess) item).setMaxCount(count);
    }

    public static final String MILKIFY = "milkified";

    public static void milkifyPotion(ItemStack potion) {
        potion.getOrCreateNbt().putBoolean(MILKIFY, true);
    }

    public static boolean isMilkified(ItemStack potion) {
        return potion.hasNbt() && potion.getNbt().getBoolean(MILKIFY);
    }

    //brewing xp is determined by the ingredient used, more valuable ingredients should give more xp
    public static double getBrewXp(ItemStack stack) {
        return stack.getRarity() == Rarity.RARE ? 10 : 7;
    }

    public static void splitAndSpawnExperience(World world, Vec3d pos, double experience) {
        world.spawnEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, (int) experience));
    }

    public static void brewPotions(DefaultedList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes) {
        for (int i : inputIndexes) {
            ItemStack output = BrewingRecipeRegistry.craft(inputs.get(i), ingredient);
            output.setCount(inputs.get(i).getCount());//the change from the forge version
            if (!output.isEmpty()) {
                inputs.set(i, output);
            }
        }
    }


    public static boolean isValidInputCountInsensitive(ItemStack stack) {
       return BrewingStandScreenHandler.PotionSlot.matches(stack);
    }

}
