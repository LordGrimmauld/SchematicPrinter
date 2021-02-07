package mod.grimmauld.schematicprinter.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderTypes extends RenderState {
	protected static final CullState DISABLE_CULLING = new RenderTypes.NoCullState();
	private static final RenderType OUTLINE_SOLID;

	static {
		OUTLINE_SOLID = RenderType.makeType("outline_solid", DefaultVertexFormats.ENTITY, 7, 256, true, false, RenderType.State.getBuilder().texture(new TextureState(ExtraTextures.BLANK.getLocation(), false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true));
	}

	public RenderTypes() {
		super("", () -> {}, () -> {});
	}

	public static RenderType getOutlineTranslucent(ResourceLocation texture, boolean cull) {
		RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new TextureState(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).cull(cull ? CULL_ENABLED : DISABLE_CULLING).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true);
		return RenderType.makeType("outline_translucent" + (cull ? "_cull" : ""), DefaultVertexFormats.ENTITY, 7, 256, true, true, rendertype$state);
	}

	public static RenderType getOutlineSolid() {
		return OUTLINE_SOLID;
	}

	protected static class NoCullState extends CullState {
		public NoCullState() {
			super(false);
		}

		public void setupRenderState() {
			RenderSystem.disableCull();
		}
	}
}
