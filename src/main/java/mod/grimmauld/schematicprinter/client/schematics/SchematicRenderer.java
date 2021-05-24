package mod.grimmauld.schematicprinter.client.schematics;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.util.TERenderHelper;
import mod.grimmauld.sidebaroverlay.render.SuperByteBuffer;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class SchematicRenderer {
	private final Map<RenderType, SuperByteBuffer> bufferCache = new HashMap<>(getLayerCount());
	private final Set<RenderType> usedBlockRenderLayers = new HashSet<>(getLayerCount());
	private final Set<RenderType> startedBufferBuilders = new HashSet<>(getLayerCount());
	private boolean active;
	private boolean changed;
	private SchematicWorld schematic;
	private BlockPos anchor;

	public SchematicRenderer() {
		changed = false;
	}

	private static int getLayerCount() {
		return RenderType.getBlockRenderTypes()
			.size();
	}

	public void display(SchematicWorld world) {
		this.anchor = world.anchor;
		this.schematic = world;
		this.active = true;
		this.changed = true;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void tick() {
		if (!active)
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.world == null || mc.player == null || !changed)
			return;

		redraw(mc);
		changed = false;
	}

	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		if (!active)
			return;
		buffer.getBuffer(RenderType.getSolid());
		for (RenderType layer : RenderType.getBlockRenderTypes()) {
			if (!usedBlockRenderLayers.contains(layer))
				continue;
			SuperByteBuffer superByteBuffer = bufferCache.get(layer);
			superByteBuffer.renderInto(ms, buffer.getBuffer(layer));
		}
		TERenderHelper.renderTileEntities(schematic, schematic.getRenderedTileEntities(), ms, new MatrixStack(),
			buffer);
	}

	private void redraw(Minecraft minecraft) {
		usedBlockRenderLayers.clear();
		startedBufferBuilders.clear();

		final SchematicWorld blockAccess = schematic;
		final BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();

		List<BlockState> blockstates = new LinkedList<>();
		Map<RenderType, BufferBuilder> buffers = new HashMap<>();
		MatrixStack ms = new MatrixStack();

		BlockPos.getAllInBox(blockAccess.getBounds())
			.forEach(localPos -> {
				ms.push();
				ms.translate(localPos.getX(), localPos.getY(), localPos.getZ());
				BlockPos pos = localPos.add(anchor);
				BlockState state = blockAccess.getBlockState(pos);

				for (RenderType blockRenderLayer : RenderType.getBlockRenderTypes()) {
					if (!RenderTypeLookup.canRenderInLayer(state, blockRenderLayer))
						continue;
					ForgeHooksClient.setRenderLayer(blockRenderLayer);
					if (!buffers.containsKey(blockRenderLayer))
						buffers.put(blockRenderLayer, new BufferBuilder(DefaultVertexFormats.BLOCK.getIntegerSize()));

					BufferBuilder bufferBuilder = buffers.get(blockRenderLayer);
					if (startedBufferBuilders.add(blockRenderLayer))
						bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
					if (blockRendererDispatcher.renderModel(state, pos, blockAccess, ms, bufferBuilder, true,
						minecraft.world.rand, EmptyModelData.INSTANCE)) {
						usedBlockRenderLayers.add(blockRenderLayer);
					}
					blockstates.add(state);
				}

				ForgeHooksClient.setRenderLayer(null);
				ms.pop();
			});

		// finishDrawing
		for (RenderType layer : RenderType.getBlockRenderTypes()) {
			if (!startedBufferBuilders.contains(layer))
				continue;
			BufferBuilder buf = buffers.get(layer);
			buf.finishDrawing();
			bufferCache.put(layer, new SuperByteBuffer(buf));
		}
	}

}
