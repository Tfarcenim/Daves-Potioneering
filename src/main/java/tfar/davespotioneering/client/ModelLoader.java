package tfar.davespotioneering.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public class ModelLoader implements IGeometryLoader<FullBrightModel.UnbakedFullBrightModel> {
    public static final ModelLoader INSTANCE = new ModelLoader();


    //makes unbakedmodels
    @Override
    public FullBrightModel.UnbakedFullBrightModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
        BlockModel baseModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents,"base"), BlockModel.class);

        return new FullBrightModel.UnbakedFullBrightModel(baseModel);
    }
}
