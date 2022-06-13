package mod.grimmauld.schematicprinter.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Iterator;

public class TERenderHelper {
	public static void renderTileEntities(Level world, Iterable<BlockEntity> customRenderTEs, PoseStack ms, PoseStack localTransform, MultiBufferSource buffer) {
		float pt = Minecraft.getInstance().getFrameTime();
		Matrix4f matrix = localTransform.last().pose();
		for (Iterator<BlockEntity> iterator = customRenderTEs.iterator(); iterator.hasNext(); ) {
			BlockEntity tileEntity = iterator.next();
			BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tileEntity);
			if (renderer == null) {
				iterator.remove();
			} else {
				try {
					BlockPos pos = tileEntity.getBlockPos();
					ms.pushPose();
					ms.translate(pos.getX(), pos.getY(), pos.getZ());
					Vector4f vec = new Vector4f(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 1.0F);
					vec.transform(matrix);
					BlockPos lightPos = new BlockPos(vec.x(), vec.y(), vec.z());
					renderer.render(tileEntity, pt, ms, buffer, LevelRenderer.getLightColor(world, lightPos), OverlayTexture.NO_OVERLAY);
					ms.popPose();
				} catch (Exception var13) {
					iterator.remove();
					ResourceLocation teName = tileEntity.getType().getRegistryName();
					SchematicPrinter.LOGGER.error("TileEntity " + (teName != null ? teName.toString() : "null") + " didn't want to render on a schematic.\n", var13);
				}
			}
		}
	}
}
