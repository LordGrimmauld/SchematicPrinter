package mod.grimmauld.schematicprinter.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;

public class TERenderHelper {
	public static void renderTileEntities(World world, Iterable<TileEntity> customRenderTEs, MatrixStack ms, MatrixStack localTransform, IRenderTypeBuffer buffer) {
		float pt = Minecraft.getInstance().getRenderPartialTicks();
		Matrix4f matrix = localTransform.getLast().getMatrix();
		for (Iterator<TileEntity> iterator = customRenderTEs.iterator(); iterator.hasNext(); ) {
			TileEntity tileEntity = iterator.next();
			TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tileEntity);
			if (renderer == null) {
				iterator.remove();
			} else {
				try {
					BlockPos pos = tileEntity.getPos();
					ms.push();
					ms.translate(pos.getX(), pos.getY(), pos.getZ());
					Vector4f vec = new Vector4f((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, 1.0F);
					vec.transform(matrix);
					BlockPos lightPos = new BlockPos(vec.getX(), vec.getY(), vec.getZ());
					renderer.render(tileEntity, pt, ms, buffer, WorldRenderer.getCombinedLight(world, lightPos), OverlayTexture.NO_OVERLAY);
					ms.pop();
				} catch (Exception var13) {
					iterator.remove();
					ResourceLocation teName = tileEntity.getType().getRegistryName();
					SchematicPrinter.LOGGER.error("TileEntity " + (teName != null ? teName.toString() : "null") + " didn't want to render while moved.\n", var13);
				}
			}
		}
	}
}
