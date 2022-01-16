package tfar.davespotioneering.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import tfar.davespotioneering.block.GauntletWorkstationBlock;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;
import tfar.davespotioneering.init.ModItems;

public class PotionInjectorRenderer extends TileEntityRenderer<PotionInjectorBlockEntity> {
    public PotionInjectorRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(PotionInjectorBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockstate = tileEntityIn.getBlockState();
        if (blockstate.get(GauntletWorkstationBlock.HAS_GAUNTLET)) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
            float f = blockstate.get(GauntletWorkstationBlock.FACING).getHorizontalAngle();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
           // matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(67.5F));
            matrixStackIn.translate(0.0D, -0.125D, 0.0D);
            Minecraft.getInstance().getItemRenderer().renderItem(ModItems.POTIONEER_GAUNTLET.getDefaultInstance(), ItemCameraTransforms.TransformType.FIXED,combinedLightIn,combinedOverlayIn,matrixStackIn,bufferIn);
            matrixStackIn.pop();
        }
    }
}
