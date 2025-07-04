package tfar.davespotioneering.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tfar.davespotioneering.init.ModRecipeSerializers;

import javax.annotation.Nonnull;

public class UpgradeRecipe extends ShapedRecipe {

    public UpgradeRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), "dank",recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null));
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack newBag = super.assemble(inv,access).copy();
        ItemStack oldBag = inv.getItem(4);
        if (!oldBag.hasTag()) return newBag;
        newBag.setTag(oldBag.getTag());
        return newBag;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.UPGRADE;
    }
}
