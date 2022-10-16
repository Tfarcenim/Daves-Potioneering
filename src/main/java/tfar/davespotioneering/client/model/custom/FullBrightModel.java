package tfar.davespotioneering.client.model.custom;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import tfar.davespotioneering.mixin.BakedQuadAccess;

import javax.annotation.Nullable;
import java.util.*;

public class FullBrightModel extends ForwardingBakedModel {

    private final Set<Identifier> textures;
    private final boolean doCaching;

    public FullBrightModel(BakedModel base, boolean doCaching, Identifier... textures) {
        super();
        this.wrapped = base;
        this.textures = new HashSet<>(Arrays.asList(textures));
        this.doCaching = doCaching;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (state == null) {
            return transformQuads(wrapped.getQuads(state,side, rand), textures);
        }

        if (!doCaching) {
            return transformQuads(wrapped.getQuads(state, side, rand), textures);
        }
        return null;
    }

    private static List<BakedQuad> transformQuads(List<BakedQuad> oldQuads, Set<Identifier> textures) {
        List<BakedQuad> quads = new ArrayList<>(oldQuads);

        for (int i = 0; i < quads.size(); ++i) {
            BakedQuad quad = quads.get(i);

            if (true) {
                quads.set(i, transformQuad(quad));
            }
        }

        return quads;
    }

    private static BakedQuad transformQuad(BakedQuad quad) {
        int[] vertexData = quad.getVertexData().clone();
        int inc = 4;
        int step = vertexData.length / inc;

        int j = 6;

        // Set lighting to fullbright on all vertices

        for (int i = j ; i < vertexData.length ;i = i + inc )

        vertexData[i] = 0x0F000F0;

        return new BakedQuad(
                vertexData,
                quad.getColorIndex(),
                quad.getFace(),
                ((BakedQuadAccess)quad).getSprite(),
                quad.hasShade()
        );
    }

    // @Override
   // public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
  //      return Arrays.asList(SPRITE_IDS);// The textures this model (and all its model dependencies, and their dependencies, etc...!) depends on.
  //  }

 //   @org.jetbrains.annotations.Nullable
 //   @Override
   // public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        // //     wrapped = base.bake(loader, textureGetter, rotationContainer, modelId);
        //       return this;
        //   }
}
