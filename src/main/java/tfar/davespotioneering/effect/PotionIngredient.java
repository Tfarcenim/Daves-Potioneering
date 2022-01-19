package tfar.davespotioneering.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class PotionIngredient extends Ingredient {

    private final ItemStack stack;

    protected PotionIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.SingleItemList(stack)));
        this.stack = stack;
    }

    public static PotionIngredient create(ItemStack stack) {
        return new PotionIngredient(stack);
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return this.stack.getItem() == input.getItem() && this.stack.areShareTagsEqual(input);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.addProperty("item", stack.getItem().getRegistryName().toString());
        json.addProperty("count", stack.getCount());
        if (stack.hasTag())
            json.addProperty("nbt", stack.getTag().toString());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<PotionIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public PotionIngredient parse(PacketBuffer buffer) {
            return new PotionIngredient(buffer.readItemStack());
        }

        @Override
        public PotionIngredient parse(JsonObject json) {
            return new PotionIngredient(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(PacketBuffer buffer, PotionIngredient ingredient) {
            buffer.writeItemStack(ingredient.stack);
        }
    }
}