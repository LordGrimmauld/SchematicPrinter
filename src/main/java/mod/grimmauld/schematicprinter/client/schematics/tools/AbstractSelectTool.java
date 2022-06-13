package mod.grimmauld.schematicprinter.client.schematics.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectItem;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.SelectConfig;
import mod.grimmauld.sidebaroverlay.render.ExtraTextures;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.outline.Outline;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractSelectTool extends SelectItem {
	protected final int color;
	@Nullable
	protected Outline outline;

	public AbstractSelectTool(Component description, int color) {
		super(description);
		this.color = color;
		outline = null;
	}

	@Nullable
	protected abstract Outline getUpdatedOutline();

	@Override
	public void renderActive(PoseStack ms, SuperRenderTypeBuffer buffer) {
		super.renderActive(ms, buffer);
		outline = getUpdatedOutline();

		if (outline == null)
			return;
		outline.getParams()
			.colored(color)
			.withFaceTexture(ExtraTextures.CHECKERED)
			.lineWidth(1 / 16f);
		outline.render(ms, buffer);
		outline.getParams()
			.clearTextures();
	}

	protected Stream<BlockInformation> putBlocksInBox(Supplier<Optional<BlockState>> stateGen) {
		if (stateGen == BuildToolStateSupplier.FILL_FROM_PALETTE && PaletteManager.PALETTE.isEmpty())
			return Stream.empty();
		return getPositions().flatMap(pos -> stateGen.get()
			.map(Stream::of).orElseGet(Stream::empty).map(state -> new BlockInformation(pos, state)));
	}

	protected abstract Stream<BlockPos> getPositions();

	protected void invalidateOutline(SelectConfig<?> config) {
		outline = null;
	}
}
