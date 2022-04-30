package tfar.davespotioneering.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.client.model.IModelLoader;

public class ModelLoader implements IModelLoader<FullBrightModel.UnbakedFullBrightModel> {
    public static final ModelLoader INSTANCE = new ModelLoader();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // Not used
    }


    //makes unbakedmodels
    @Override
    public FullBrightModel.UnbakedFullBrightModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        BlockModel baseModel = deserializationContext.deserialize(JSONUtils.getAsJsonObject(modelContents,"base"), BlockModel.class);

        return new FullBrightModel.UnbakedFullBrightModel(baseModel);
    }
}
