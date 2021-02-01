package mod.grimmauld.schematicprinter.client.schematics;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.grimmauld.schematicprinter.render.SuperByteBuffer;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.TERenderHelper;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SchematicRenderer {
	private final Map<RenderType, SuperByteBuffer> bufferCache = new HashMap<>(getLayerCount());
	private final Set<RenderType> usedBlockRenderLayers = new HashSet<>(getLayerCount());
	private final Set<RenderType> startedBufferBuilders = new HashSet<>(getLayerCount());
	private boolean active;
	private boolean changed = false;
	private SchematicWorld schematic;
	private BlockPos anchor;

	public SchematicRenderer() {
	}

	private static int getLayerCount() {
		return RenderType.getBlockRenderTypes().size();
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
		if (this.active) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.world != null && mc.player != null && this.changed) {
				this.redraw(mc);
				this.changed = false;
			}
		}
	}

	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		if (this.active) {
			buffer.getBuffer(RenderType.getSolid());
			for (RenderType layer : RenderType.getBlockRenderTypes()) {
				if (this.usedBlockRenderLayers.contains(layer)) {
					SuperByteBuffer superByteBuffer = this.bufferCache.get(layer);
					superByteBuffer.renderInto(ms, buffer.getBuffer(layer));
				}
			}

			TERenderHelper.renderTileEntities(this.schematic, this.schematic.getRenderedTileEntities(), ms, new MatrixStack(), buffer);
		}
	}

	private void redraw(Minecraft minecraft) {
		this.usedBlockRenderLayers.clear();
		this.startedBufferBuilders.clear();
		SchematicWorld blockAccess = this.schematic;
		BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
		Map<RenderType, BufferBuilder> buffers = new HashMap<>();
		MatrixStack ms = new MatrixStack();
		BlockPos.getAllInBox(blockAccess.getBounds()).forEach((localPos) -> {
			ms.push();
			ms.translate(localPos.getX(), localPos.getY(), localPos.getZ());
			BlockPos pos = localPos.add(this.anchor);
			BlockState state = blockAccess.getBlockState(pos);
			for (RenderType blockRenderLayer : RenderType.getBlockRenderTypes()) {
				if (RenderTypeLookup.canRenderInLayer(state, blockRenderLayer)) {
					ForgeHooksClient.setRenderLayer(blockRenderLayer);
					if (!buffers.containsKey(blockRenderLayer)) {
						buffers.put(blockRenderLayer, new BufferBuilder(DefaultVertexFormats.BLOCK.getIntegerSize()));
					}

					BufferBuilder bufferBuilder = buffers.get(blockRenderLayer);
					if (this.startedBufferBuilders.add(blockRenderLayer)) {
						bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
					}

					if (minecraft.world != null && blockRendererDispatcher.renderModel(state, pos, blockAccess, ms, bufferBuilder, true, minecraft.world.rand, EmptyModelData.INSTANCE)) {
						this.usedBlockRenderLayers.add(blockRenderLayer);
					}
				}
			}

			ForgeHooksClient.setRenderLayer(null);
			ms.pop();
		});
		for (RenderType layer : RenderType.getBlockRenderTypes()) {
			if (this.startedBufferBuilders.contains(layer)) {
				BufferBuilder buf = buffers.get(layer);
				buf.finishDrawing();
				this.bufferCache.put(layer, new SuperByteBuffer(buf));
			}
		}

	}
}
