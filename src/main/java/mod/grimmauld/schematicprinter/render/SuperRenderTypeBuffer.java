package mod.grimmauld.schematicprinter.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.util.LazyValue;
import net.minecraft.util.Util;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.SortedMap;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SuperRenderTypeBuffer implements IRenderTypeBuffer {
	static final LazyValue<SuperRenderTypeBuffer> instance = new LazyValue<>(SuperRenderTypeBuffer::new);
	final SuperRenderTypeBufferPhase earlyBuffer = new SuperRenderTypeBufferPhase();
	final SuperRenderTypeBufferPhase defaultBuffer = new SuperRenderTypeBufferPhase();
	final SuperRenderTypeBufferPhase lateBuffer = new SuperRenderTypeBufferPhase();

	public SuperRenderTypeBuffer() {
	}

	public static SuperRenderTypeBuffer getInstance() {
		return instance.getValue();
	}

	public IVertexBuilder getBuffer(RenderType type) {
		return this.defaultBuffer.getBuffer(type);
	}

	public IVertexBuilder getLateBuffer(RenderType type) {
		return this.lateBuffer.getBuffer(type);
	}

	public void draw() {
		RenderSystem.disableCull();
		this.earlyBuffer.finish();
		this.defaultBuffer.finish();
		this.lateBuffer.finish();
	}

	private static class SuperRenderTypeBufferPhase extends Impl {
		static final RegionRenderCacheBuilder blockBuilders = new RegionRenderCacheBuilder();

		protected SuperRenderTypeBufferPhase() {
			super(new BufferBuilder(256), createEntityBuilders());
		}

		static SortedMap<RenderType, BufferBuilder> createEntityBuilders() {
			return Util.make(new Object2ObjectLinkedOpenHashMap<>(), (map) -> {
				map.put(Atlases.getSolidBlockType(), blockBuilders.getBuilder(RenderType.getSolid()));
				assign(map, RenderTypes.getOutlineSolid());
				map.put(Atlases.getCutoutBlockType(), blockBuilders.getBuilder(RenderType.getCutout()));
				map.put(Atlases.getBannerType(), blockBuilders.getBuilder(RenderType.getCutoutMipped()));
				map.put(Atlases.getTranslucentBlockType(), blockBuilders.getBuilder(RenderType.getTranslucent()));
				assign(map, Atlases.getShieldType());
				assign(map, Atlases.getBedType());
				assign(map, Atlases.getShulkerBoxType());
				assign(map, Atlases.getSignType());
				assign(map, Atlases.getChestType());
				assign(map, RenderType.getTranslucentNoCrumbling());
				assign(map, RenderType.getGlint());
				assign(map, RenderType.getEntityGlint());
				assign(map, RenderType.getWaterMask());
				ModelBakery.DESTROY_RENDER_TYPES.forEach((p_228488_1_) -> assign(map, p_228488_1_));
			});
		}

		private static void assign(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, RenderType type) {
			map.put(type, new BufferBuilder(type.getBufferSize()));
		}
	}
}
