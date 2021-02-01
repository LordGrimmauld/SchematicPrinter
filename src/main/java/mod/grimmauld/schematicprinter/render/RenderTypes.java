package mod.grimmauld.schematicprinter.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllSpecialTextures;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

public class RenderTypes extends RenderState {
	protected static final CullState DISABLE_CULLING = new RenderTypes.NoCullState();
	private static final RenderType OUTLINE_SOLID;
	private static final RenderType GLOWING_SOLID;
	private static final RenderType GLOWING_TRANSLUCENT;
	private static final RenderType ITEM_PARTIAL_SOLID;
	private static final RenderType ITEM_PARTIAL_TRANSLUCENT;

	static {
		OUTLINE_SOLID = RenderType.makeType("outline_solid", DefaultVertexFormats.ENTITY, 7, 256, true, false, RenderType.State.getBuilder().texture(new TextureState(AllSpecialTextures.BLANK.getLocation(), false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true));
		GLOWING_SOLID = getGlowingSolid(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
		GLOWING_TRANSLUCENT = getGlowingTranslucent(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
		ITEM_PARTIAL_SOLID = RenderType.makeType("item_solid", DefaultVertexFormats.ENTITY, 7, 256, true, false, RenderType.State.getBuilder().texture(new TextureState(PlayerContainer.LOCATION_BLOCKS_TEXTURE, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true));
		ITEM_PARTIAL_TRANSLUCENT = RenderType.makeType("entity_translucent", DefaultVertexFormats.ENTITY, 7, 256, true, true, RenderType.State.getBuilder().texture(new TextureState(PlayerContainer.LOCATION_BLOCKS_TEXTURE, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).cull(DISABLE_CULLING).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true));
	}

	public RenderTypes() {
		super(null, null, null);
	}

	public static RenderType getOutlineTranslucent(ResourceLocation texture, boolean cull) {
		RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new TextureState(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).cull(cull ? CULL_ENABLED : DISABLE_CULLING).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true);
		return RenderType.makeType("outline_translucent" + (cull ? "_cull" : ""), DefaultVertexFormats.ENTITY, 7, 256, true, true, rendertype$state);
	}

	public static RenderType getGlowingSolid(ResourceLocation texture) {
		RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new TextureState(texture, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true);
		return RenderType.makeType("glowing_solid", DefaultVertexFormats.ENTITY, 7, 256, true, false, rendertype$state);
	}

	public static RenderType getGlowingTranslucent(ResourceLocation texture) {
		RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new TextureState(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_DISABLED).alpha(DEFAULT_ALPHA).cull(DISABLE_CULLING).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true);
		return RenderType.makeType("glowing_translucent", DefaultVertexFormats.ENTITY, 7, 256, true, true, rendertype$state);
	}

	public static RenderType getItemPartialSolid() {
		return ITEM_PARTIAL_SOLID;
	}

	public static RenderType getItemPartialTranslucent() {
		return ITEM_PARTIAL_TRANSLUCENT;
	}

	public static RenderType getOutlineSolid() {
		return OUTLINE_SOLID;
	}

	public static RenderType getGlowingSolid() {
		return GLOWING_SOLID;
	}

	public static RenderType getGlowingTranslucent() {
		return GLOWING_TRANSLUCENT;
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
