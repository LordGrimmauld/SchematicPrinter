package mod.grimmauld.schematicprinter.client.palette;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.util.ColorHelper;
import mod.grimmauld.schematicprinter.util.EmptyModelData;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Map;

import static net.minecraft.client.gui.AbstractGui.blit;

public class PaletteOverlay {
	private static final Minecraft MC = Minecraft.getInstance();


	public void render(RenderGameOverlayEvent.Pre event) {
		if (!PaletteManager.PALETTE.isEmpty()) {
			draw(event.getMatrixStack());
		}
	}

	private void draw(MatrixStack ms) {
		MainWindow window = MC.getMainWindow();

		final int menuWidth = 198;
		final int menuHeight = (int) (16 * (Math.ceil(PaletteManager.PALETTE.size() / 12.)) + 4);

		ms.push();
		RenderSystem.enableBlend();
		RenderSystem.color4f(1, 1, 1, 3 / 4f);

		MC.getTextureManager().bindTexture(ExtraTextures.GRAY.getLocation());
		ms.translate((window.getScaledWidth() - menuWidth) / 2f, 10, 0);
		blit(ms, 0, 0, 0, 0, menuWidth, menuHeight, 16, 16);


		BlockRendererDispatcher blockRenderer = MC.getBlockRendererDispatcher();
		IRenderTypeBuffer.Impl buffer = MC.getRenderTypeBuffers()
			.getBufferSource();
		int scale = 10;
		ms.translate(18, 15, 10);
		RenderSystem.enableRescaleNormal();
		RenderSystem.enableAlphaTest();
		RenderHelper.setupGui3DDiffuseLighting();
		RenderSystem.alphaFunc(516, 0.1F);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);


		int xOffset = 0;
		FontRenderer fr = MC.fontRenderer;

		for (Map.Entry<BlockState, Integer> entry : PaletteManager.PALETTE.entrySet()) {
			BlockState state = entry.getKey();
			RenderType renderType = RenderTypeLookup.func_239220_a_(state, state.isTransparent());
			IVertexBuilder vb = buffer.getBuffer(renderType);
			blockRenderer.getModelForState(state);

			ms.push();
			ms.scale(scale, scale, scale);
			ms.rotate(Vector3f.XN.rotationDegrees(180));
			ms.rotate(Vector3f.XP.rotationDegrees(22.5f));
			ms.rotate(Vector3f.YP.rotationDegrees(180 + 45));

			Vector3d rgb = ColorHelper.getRGB(Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0));

			MC.getTextureManager()
				.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
			blockRenderer.getBlockModelRenderer()
				.renderModel(ms.getLast(), vb, state, MC.getBlockRendererDispatcher()
						.getModelForState(state), (float) rgb.x, (float) rgb.y, (float) rgb.z,
					15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
			buffer.finish();
			ms.pop();
			ms.push();
			ms.translate(0, 0, 10);
			String weight = String.valueOf(entry.getValue());
			fr.renderString(weight, 2 - fr.getStringWidth(weight), -4, 16777215, true, ms.getLast().getMatrix(), buffer, false, 0, 15728880);
			buffer.finish();
			ms.pop();

			ms.translate(16, 0, 0);
			xOffset++;

			if (xOffset == 12) {
				ms.translate(-16 * xOffset, 16, 0);
				xOffset = 0;
			}
		}

		RenderSystem.disableAlphaTest();
		RenderSystem.disableRescaleNormal();
		ms.pop();
	}
}
