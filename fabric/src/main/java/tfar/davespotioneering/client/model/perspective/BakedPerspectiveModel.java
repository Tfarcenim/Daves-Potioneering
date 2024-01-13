package tfar.davespotioneering.client.model.perspective;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BakedPerspectiveModel extends ForwardingBakedModel {
    private final ImmutableMap<ItemDisplayContext, BakedModel> perspectives;
    private final ItemTransforms perspectiveTransforms;

    public BakedPerspectiveModel(BakedModel baseModel, ImmutableMap<ItemDisplayContext, BakedModel> perspectives) {
        this.wrapped = baseModel;
        this.perspectives = perspectives;
        perspectiveTransforms = createPerspectiveTransforms(baseModel,perspectives);
    }

    public static ItemTransforms createPerspectiveTransforms(BakedModel base,ImmutableMap<ItemDisplayContext, BakedModel> perspectives) {
        ItemTransforms baseTransforms = base.getTransforms();

        baseTransforms = new ItemTransforms(
                perspectives.containsKey(ItemDisplayContext.THIRD_PERSON_LEFT_HAND) ?
                        perspectives.get(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).getTransforms().thirdPersonLeftHand : baseTransforms.thirdPersonLeftHand,

                perspectives.containsKey(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) ?
                        perspectives.get(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).getTransforms().thirdPersonRightHand : baseTransforms.thirdPersonRightHand,

                perspectives.containsKey(ItemDisplayContext.FIRST_PERSON_LEFT_HAND) ?
                        perspectives.get(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).getTransforms().firstPersonLeftHand : baseTransforms.firstPersonLeftHand,

                perspectives.containsKey(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) ?
                        perspectives.get(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).getTransforms().firstPersonRightHand : baseTransforms.firstPersonRightHand,

                perspectives.containsKey(ItemDisplayContext.HEAD) ? perspectives.get(ItemDisplayContext.HEAD).getTransforms().head : baseTransforms.head,

                perspectives.containsKey(ItemDisplayContext.GUI) ? perspectives.get(ItemDisplayContext.GUI).getTransforms().gui : baseTransforms.gui,

                perspectives.containsKey(ItemDisplayContext.GROUND) ? perspectives.get(ItemDisplayContext.GROUND).getTransforms().ground : baseTransforms.ground,

                perspectives.containsKey(ItemDisplayContext.FIXED) ? perspectives.get(ItemDisplayContext.FIXED).getTransforms().fixed : baseTransforms.fixed);

        return baseTransforms;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public ItemTransforms getTransforms() {
        return perspectiveTransforms;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        ItemDisplayContext itemDisplayContext = context.itemTransformationMode();
        if (perspectives.containsKey(itemDisplayContext)) {
            perspectives.get(itemDisplayContext).emitItemQuads(stack, randomSupplier, context);
        } else {
            super.emitItemQuads(stack, randomSupplier, context);
        }
    }
}
