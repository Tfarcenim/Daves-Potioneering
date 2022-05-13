package tfar.davespotioneering;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tfar.davespotioneering.mixin.ItemAccess;

public class Util {

    public static void setStackSize(Item item, int count) {
        ((ItemAccess) item).setMaxStackSize(count);
    }

    public static final String MILKIFY = "milkified";

    public static void milkifyPotion(ItemStack potion) {
        potion.getOrCreateTag().putBoolean(MILKIFY, true);
    }

    public static boolean isMilkified(ItemStack potion) {
        return potion.hasTag() && potion.getTag().getBoolean(MILKIFY);
    }

    //brewing xp is determined by the ingredient used, more valuable ingredients should give more xp
    public static double getBrewXp(ItemStack stack) {
        return stack.getRarity() == Rarity.RARE ? 10 : 7;
    }

    public static void splitAndSpawnExperience(Level world, Vec3 pos, double experience) {
        world.addFreshEntity(new ExperienceOrb(world, pos.x, pos.y, pos.z, (int) experience));
    }

    public static void brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes) {
        for (int i : inputIndexes) {
            ItemStack output = PotionBrewing.mix(inputs.get(i), ingredient);
            output.setCount(inputs.get(i).getCount());//the change from the forge version
            if (!output.isEmpty()) {
                inputs.set(i, output);
            }
        }
    }


    public static boolean isValidInputCountInsensitive(ItemStack stack) {
       return BrewingStandMenu.PotionSlot.mayPlaceItem(stack);
    }

}
