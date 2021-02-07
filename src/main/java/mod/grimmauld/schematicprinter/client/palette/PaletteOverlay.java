package mod.grimmauld.schematicprinter.client.palette;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.foundation.utility.VirtualEmptyModelData;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.util.ColorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Map;

import static net.minecraft.client.gui.AbstractGui.blit;

public class PaletteOverlay {
	private static final Minecraft MC = Minecraft.getInstance();

	private final boolean shouldRender = true;


	public void render(RenderGameOverlayEvent.Pre event) {
		if (shouldRender && !PaletteManager.PALETTE.isEmpty()) {
			MatrixStack ms = new MatrixStack();
			draw(ms);
		}
	}

	private void draw(MatrixStack ms) {
		MainWindow window = MC.getMainWindow();

		final int menuWidth = 198;
		final int menuHeight = (int) (16 * (Math.ceil(PaletteManager.PALETTE.size() / 12.)) + 4);

		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.color4f(1, 1, 1, 3 / 4f);

		MC.getTextureManager().bindTexture(ExtraTextures.GRAY.getLocation());
		RenderSystem.translatef((window.getScaledWidth() - menuWidth) / 2f, 10, 0);
		blit(0, 0, 0, 0, menuWidth, menuHeight, 16, 16);


		BlockRendererDispatcher blockRenderer = MC.getBlockRendererDispatcher();
		IRenderTypeBuffer.Impl buffer = MC.getRenderTypeBuffers()
			.getBufferSource();
		int scale = 10;
		RenderSystem.translated(18, 15, 10);
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
			RenderType renderType = RenderTypeLookup.getRenderType(state);
			IVertexBuilder vb = buffer.getBuffer(renderType);
			blockRenderer.getModelForState(state);

			RenderSystem.pushMatrix();
			RenderSystem.scaled(scale, scale, scale);
			// RenderSystem.rotatef( + 45,  22.5f,  + 45, 8);
			RenderSystem.rotatef(180, 0, 0, 0);
			RenderSystem.rotatef(+45, -22.5f, +45, -6.5f);

			Vec3d rgb = ColorHelper.getRGB(Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0));

			MC.getTextureManager()
				.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
			blockRenderer.getBlockModelRenderer()
				.renderModel(ms.getLast(), vb, state, MC.getBlockRendererDispatcher()
						.getModelForState(state), (float) rgb.x, (float) rgb.y, (float) rgb.z,
					15728880, OverlayTexture.NO_OVERLAY, VirtualEmptyModelData.INSTANCE);
			buffer.finish();
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			RenderSystem.translated(0, 0, 10);
			String weight = String.valueOf(entry.getValue());
			fr.renderString(weight, 2 - fr.getStringWidth(weight), -4, 16777215, true, ms.getLast().getMatrix(), buffer, false, 0, 15728880);
			buffer.finish();
			RenderSystem.popMatrix();

			RenderSystem.translated(16, 0, 0);
			xOffset++;

			if (xOffset == 12) {
				RenderSystem.translated(-16 * xOffset, 16, 0);
				xOffset = 0;
			}
		}

		RenderSystem.disableAlphaTest();
		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
	}
}
