package tfar.davespotioneering.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;
import tfar.davespotioneering.init.ModItems;

public class PotionInjectorRenderer implements BlockEntityRenderer<PotionInjectorBlockEntity> {
    public PotionInjectorRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(PotionInjectorBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockstate = tileEntityIn.getBlockState();
        if (blockstate.getValue(PotionInjectorBlock.HAS_GAUNTLET)) {
            matrixStackIn.pushPose();

            Direction facing = blockstate.getValue(PotionInjectorBlock.FACING);

            matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
            float f = facing.toYRot();
            matrixStackIn.mulPose(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStackIn.mulPose(Vec3f.POSITIVE_X.getDegreesQuaternion(-22.5F));
            matrixStackIn.translate(0.0D, 0.125D, 0.0625D);
            Minecraft.getInstance().getItemRenderer().renderStatic(ModItems.POTIONEER_GAUNTLET.getDefaultInstance(), ModelTransformation.Mode.FIXED,combinedLightIn,combinedOverlayIn,matrixStackIn,bufferIn,0);
            matrixStackIn.popPose();
        }
    }
}
