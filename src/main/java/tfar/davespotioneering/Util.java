package tfar.davespotioneering;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import tfar.davespotioneering.mixin.ItemAccess;

public class Util {

    public static void setStackSize(Item item, int count) {
        ((ItemAccess)item).setMaxStackSize(count);
    }

    public static final String MILKIFY = "milkified";

    public static void milkifyPotion(ItemStack potion) {
        potion.getOrCreateTag().putBoolean(MILKIFY,true);
    }

    public static boolean isMilkified(ItemStack potion) {
        return potion.hasTag() && potion.getTag().getBoolean(MILKIFY);
    }

    //brewing xp is determined by the ingredient used, more valuable ingredients should give more xp
    public static double getBrewXp(ItemStack stack) {
        return stack.getRarity() == Rarity.RARE ? 10 : 7;
    }

    public static void splitAndSpawnExperience(World world, Vector3d pos, double experience) {
            world.addEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, (int)experience));
    }
}
