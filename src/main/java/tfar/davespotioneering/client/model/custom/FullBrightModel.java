package tfar.davespotioneering.client.model.custom;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class FullBrightModel extends ForwardingBakedModel implements UnbakedModel {

    public FullBrightModel(UnbakedModel unbakedModel, boolean doCaching, Identifier... textures) {
        this(unbakedModel,doCaching,null,textures);
    }

    private final UnbakedModel base;
    private final Set<Identifier> textures;
    private final boolean doCaching;
    private Predicate<BlockState> state;

    public FullBrightModel(UnbakedModel base, boolean doCaching, Predicate<BlockState> state, Identifier... textures) {
        super();
        this.base = base;
        this.textures = new HashSet<>(Arrays.asList(textures));
        this.doCaching = doCaching;
        this.state = state;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (state == null) {
            return transformQuads(wrapped.getQuads(state,null, rand), textures);
        }

        if (this.state != null && !this.state.test(state)) {
            return wrapped.getQuads(state, side, rand);
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

     //       if (textures.contains(quad.getSprite().getName())) {
     //           quads.set(i, transformQuad(quad));
    //        }
        }

        return quads;
    }

    private static BakedQuad transformQuad(BakedQuad quad) {
        int[] vertexData = quad.getVertexData().clone();
        int step = vertexData.length / 4;

        // Set lighting to fullbright on all vertices
        vertexData[6] = 0x00F000F0;
        vertexData[6 + step] = 0x00F000F0;
        vertexData[6 + 2 * step] = 0x00F000F0;
        vertexData[6 + 3 * step] = 0x00F000F0;

        return new BakedQuad(
                vertexData,
                quad.getColorIndex(),
                quad.getFace(),
                null,
                quad.hasShade()
        );
    }

    // The minecraft default block model
    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:item/generated");

    private ModelTransformation transformation;

    // We need to add the default model to the dependencies
    public Collection<Identifier> getModelDependencies() {
        return Collections.singletonList(DEFAULT_BLOCK_MODEL);
    }

    private static final SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[]{
            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/furnace_front_on")),
            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/furnace_top"))
    };
    private Sprite[] SPRITES = new Sprite[2];

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return Arrays.asList(SPRITE_IDS);// The textures this model (and all its model dependencies, and their dependencies, etc...!) depends on.
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        wrapped = base.bake(loader, textureGetter, rotationContainer, modelId);
        return this;
    }
}
