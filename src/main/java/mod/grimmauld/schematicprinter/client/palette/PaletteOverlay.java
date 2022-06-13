package mod.grimmauld.schematicprinter.client.palette;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mod.grimmauld.schematicprinter.util.EmptyModelData;
import mod.grimmauld.sidebaroverlay.render.ExtraTextures;
import mod.grimmauld.sidebaroverlay.util.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Map;

import static net.minecraft.client.gui.GuiComponent.blit;

public class PaletteOverlay {

	private static final Minecraft MC = Minecraft.getInstance();


	public void render(RenderGameOverlayEvent.Pre event) {
		if (!PaletteManager.PALETTE.isEmpty()) {
			draw(event.getMatrixStack());
		}
	}

	private void draw(PoseStack ms) {
		Window window = MC.getWindow();

		final int menuWidth = 198;
		final int menuHeight = (int) (16 * (Math.ceil(PaletteManager.PALETTE.size() / 12.)) + 4);

		ms.pushPose();
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 3 / 4f);

		RenderSystem.setShaderTexture(0, ExtraTextures.GRAY.getLocation());
		ms.translate((window.getGuiScaledWidth() - menuWidth) / 2f, 10, 0);
		blit(ms, 0, 0, 0, 0, menuWidth, menuHeight, 16, 16);


		BlockRenderDispatcher blockRenderer = MC.getBlockRenderer();
		MultiBufferSource.BufferSource buffer = MC.renderBuffers()
				.bufferSource();
		int scale = 10;
		ms.translate(18, 15, 10);
		Lighting.setupFor3DItems();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


		int xOffset = 0;
		Font fr = MC.font;

		for (Map.Entry<BlockState, Integer> entry : PaletteManager.PALETTE.entrySet()) {
			BlockState state = entry.getKey();
			RenderType renderType = ItemBlockRenderTypes.getRenderType(state, state.useShapeForLightOcclusion());
			VertexConsumer vb = buffer.getBuffer(renderType);
			blockRenderer.getBlockModel(state);

			ms.pushPose();
			ms.scale(scale, scale, scale);
			ms.mulPose(Vector3f.XN.rotationDegrees(180));
			ms.mulPose(Vector3f.XP.rotationDegrees(22.5f));
			ms.mulPose(Vector3f.YP.rotationDegrees(180 + 45));

			Vec3 rgb = ColorHelper.getRGB(Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0));

			RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
			blockRenderer.getModelRenderer()
					.renderModel(ms.last(), vb, state, MC.getBlockRenderer()
									.getBlockModel(state), (float) rgb.x, (float) rgb.y, (float) rgb.z,
							15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
			buffer.endBatch();
			ms.popPose();
			ms.pushPose();
			ms.translate(0, 0, 10);
			String weight = String.valueOf(entry.getValue());
			fr.drawInBatch(weight, 2 - fr.width(weight), -4, 16777215, true, ms.last().pose(), buffer, false, 0, 15728880);
			buffer.endBatch();
			ms.popPose();

			ms.translate(16, 0, 0);
			xOffset++;

			if (xOffset == 12) {
				ms.translate(-16 * xOffset, 16, 0);
				xOffset = 0;
			}
		}

		ms.popPose();
	}
}
