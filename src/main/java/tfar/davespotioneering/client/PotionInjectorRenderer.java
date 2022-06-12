package tfar.davespotioneering.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;
import tfar.davespotioneering.init.ModItems;

public class PotionInjectorRenderer implements BlockEntityRenderer<PotionInjectorBlockEntity> {
    public PotionInjectorRenderer(BlockEntityRendererFactory.Context rendererDispatcherIn) {
    }

    @Override
    public void render(PotionInjectorBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockstate = tileEntityIn.getCachedState();
        if (blockstate.get(PotionInjectorBlock.HAS_GAUNTLET)) {
            matrixStackIn.push();

            Direction facing = blockstate.get(PotionInjectorBlock.FACING);

            matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
            float f = facing.asRotation();
            matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStackIn.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-22.5F));
            matrixStackIn.translate(0.0D, 0.125D, 0.0625D);
            MinecraftClient.getInstance().getItemRenderer().renderItem(ModItems.POTIONEER_GAUNTLET.getDefaultStack(), ModelTransformation.Mode.FIXED,combinedLightIn,combinedOverlayIn,matrixStackIn,bufferIn,0);
            matrixStackIn.pop();
        }
    }
}
