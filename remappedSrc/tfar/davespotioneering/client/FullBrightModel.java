package tfar.davespotioneering.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import java.util.*;
import java.util.function.Predicate;

public class FullBrightModel extends BakedModelWrapper<BakedModel> {

    private static final LoadingCache<CacheKey, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(@Nonnull CacheKey key) {
            return transformQuads(key.base.getQuads(key.state, key.side, key.random), key.textures);
        }
    });

    public static void invalidateCache() {
        CACHE.invalidateAll();
    }

    private final Set<Identifier> textures;
    private final boolean doCaching;
    private Predicate<BlockState> state = null;

    public FullBrightModel(BakedModel base, boolean doCaching, Identifier... textures) {
        super(base);

        this.textures = new HashSet<>(Arrays.asList(textures));
        this.doCaching = doCaching;
    }

    public FullBrightModel(BakedModel base, boolean doCaching, Predicate<BlockState> state, Identifier... textures) {
        super(base);

        this.textures = new HashSet<>(Arrays.asList(textures));
        this.doCaching = doCaching;
        this.state = state;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (state == null) {
            return transformQuads(originalModel.getQuads(state,null, rand), textures);
        }

        if (this.state != null && !this.state.test(state)) {
            return originalModel.getQuads(state, side, rand);
        }

        if (!doCaching) {
            return transformQuads(originalModel.getQuads(state, side, rand), textures);
        }

        return CACHE.getUnchecked(new CacheKey(originalModel, textures, rand, state, side));
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

    private static class CacheKey {
        private final BakedModel base;
        private final Set<Identifier> textures;
        private final Random random;
        private final BlockState state;
        private final Direction side;

        public CacheKey(BakedModel base, Set<Identifier> textures, Random random, BlockState state, Direction side) {
            this.base = base;
            this.textures = textures;
            this.random = random;
            this.state = state;
            this.side = side;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CacheKey cacheKey = (CacheKey) o;

            if (cacheKey.side != side) {
                return false;
            }

            return state.equals(cacheKey.state);
        }

        @Override
        public int hashCode() {
            return state.hashCode() + (31 * (side != null ? side.hashCode() : 0));
        }
    }
}
