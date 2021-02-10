package mod.grimmauld.schematicprinter.client.overlay.selection.tools;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SelectConfig;
import mod.grimmauld.schematicprinter.client.palette.PaletteManager;
import mod.grimmauld.schematicprinter.client.printer.BlockInformation;
import mod.grimmauld.schematicprinter.render.SuperRenderTypeBuffer;
import mod.grimmauld.schematicprinter.util.outline.Outline;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

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

	public AbstractSelectTool(ITextComponent description, int color) {
		super(description);
		this.color = color;
		outline = null;
	}

	@Nullable
	protected abstract Outline getUpdatedOutline();

	@Override
	public void renderActive(MatrixStack ms, SuperRenderTypeBuffer buffer) {
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
