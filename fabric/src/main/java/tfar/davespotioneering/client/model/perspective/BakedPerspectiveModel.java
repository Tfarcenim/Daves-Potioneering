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
        ItemTransforms transforms = new ItemTransforms(base.getTransforms());

        if (perspectives.containsKey(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)) {
            transforms = new ItemTransforms(
                    perspectives.get(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).getTransforms().thirdPersonLeftHand,
                    transforms.thirdPersonRightHand,
                    transforms.firstPersonLeftHand,
                    transforms.firstPersonRightHand,
                    transforms.head,
                    transforms.gui,
                    transforms.ground,
                    transforms.fixed);
        }

        if (perspectives.containsKey(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
            transforms = new ItemTransforms(
                    transforms.thirdPersonLeftHand,
                    perspectives.get(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).getTransforms().thirdPersonRightHand,
                    transforms.firstPersonLeftHand,
                    transforms.firstPersonRightHand,
                    transforms.head,
                    transforms.gui,
                    transforms.ground,
                    transforms.fixed);
        }

        if (perspectives.containsKey(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)) {
            transforms = new ItemTransforms(
                    transforms.thirdPersonLeftHand,
                    transforms.thirdPersonRightHand,
                    perspectives.get(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).getTransforms().firstPersonLeftHand,
                    transforms.firstPersonRightHand,
                    transforms.head,
                    transforms.gui,
                    transforms.ground,
                    transforms.fixed);
        }

        if (perspectives.containsKey(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)) {
            transforms = new ItemTransforms(
                    transforms.thirdPersonLeftHand,
                    transforms.thirdPersonRightHand,
                    transforms.firstPersonLeftHand,
                    perspectives.get(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).getTransforms().firstPersonRightHand,
                    transforms.head,
                    transforms.gui,
                    transforms.ground,
                    transforms.fixed);
        }

        if (perspectives.containsKey(ItemDisplayContext.HEAD)) {
            transforms = new ItemTransforms(
                    transforms.thirdPersonLeftHand,
                    transforms.thirdPersonRightHand,
                    transforms.firstPersonLeftHand,
                    transforms.firstPersonRightHand,
                    perspectives.get(ItemDisplayContext.HEAD).getTransforms().head,
                    transforms.gui,
                    transforms.ground,
                    transforms.fixed);
        }

        if (perspectives.containsKey(ItemDisplayContext.GUI)) {
            transforms = new ItemTransforms(
                    transforms.thirdPersonLeftHand,
                    transforms.thirdPersonRightHand,
                    transforms.firstPersonLeftHand,
                    transforms.firstPersonRightHand,
                    transforms.head,
                    perspectives.get(ItemDisplayContext.GUI).getTransforms().gui,
                    transforms.ground,
                    transforms.fixed);
        }


        if (perspectives.containsKey(ItemDisplayContext.GROUND)) {
            transforms = new ItemTransforms(
                    transforms.thirdPersonLeftHand,
                    transforms.thirdPersonRightHand,
                    transforms.firstPersonLeftHand,
                    transforms.firstPersonRightHand,
                    transforms.head,
                    transforms.gui,
                    perspectives.get(ItemDisplayContext.GROUND).getTransforms().ground,
                    transforms.fixed);
        }

        if (perspectives.containsKey(ItemDisplayContext.FIXED)) {
            transforms = new ItemTransforms(
                    transforms.thirdPersonLeftHand,
                    transforms.thirdPersonRightHand,
                    transforms.firstPersonLeftHand,
                    transforms.firstPersonRightHand,
                    transforms.head,
                    transforms.gui,
                    transforms.ground,
                    perspectives.get(ItemDisplayContext.FIXED).getTransforms().fixed);
        }

        return transforms;
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
