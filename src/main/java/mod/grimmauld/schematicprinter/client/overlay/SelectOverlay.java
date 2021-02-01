package mod.grimmauld.schematicprinter.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.client.ExtraTextures;
import mod.grimmauld.schematicprinter.client.Keyboard;
import mod.grimmauld.schematicprinter.client.Manager;
import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import mod.grimmauld.schematicprinter.client.overlay.selection.SelectItem;
import mod.grimmauld.schematicprinter.client.overlay.selection.config.SelectConfig;
import mod.grimmauld.schematicprinter.util.KeybindHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static net.minecraft.client.gui.AbstractGui.blit;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectOverlay {
	public static final SelectOverlay EMPTY = new SelectOverlay(null, "");

	private static final Minecraft minecraft = Minecraft.getInstance();
	@Nullable
	private final KeyBinding openKey;
	private final ITextComponent title;
	public boolean canBeOpenedDirectly;
	int menuWidth;
	int menuHeight;
	private boolean visible;
	private int targetY;
	private float movingY;
	private SelectOverlay previous;

	private List<SelectItem> options;
	private int selectedOptionIndex;

	public SelectOverlay(@Nullable KeyBinding openKey, ITextComponent titleIn) {
		this.title = titleIn;
		visible = false;
		options = new ArrayList<>();
		this.openKey = openKey;
		movingY = 0;
		targetY = 0;
		selectedOptionIndex = 0;
		canBeOpenedDirectly = false;
		previous = null;
		adjustTarget();
	}

	public SelectOverlay(@Nullable KeyBinding openKey, String titleIn) {
		this(openKey, new TranslationTextComponent(titleIn));
	}

	public void testAndClose(InputEvent event) {
		if ((Keyboard.ESC.isKeyDown() && Manager.shouldCloseOnEsc) || KeybindHelper.eventActivatesKeybind(event, openKey)) {
			close();
			if (previous != null)
				previous.open(previous.previous);
		}
	}

	public SelectOverlay withOptions(List<SelectItem> options) {
		this.options = options;
		return this;
	}

	public SelectOverlay addOptions(Collection<? extends SelectItem> options) {
		this.options.addAll(options);
		return this;
	}

	public <T extends SelectItem> SelectOverlay addOption(T option) {
		this.options.add(option);
		return this;
	}

	public SelectOverlay configureDirectOpen(boolean canBeOpenedDirectly) {
		this.canBeOpenedDirectly = canBeOpenedDirectly;
		return this;
	}

	public boolean testAndOpen(@Nullable SelectOverlay previous, InputEvent event) {
		if ((canBeOpenedDirectly || previous != null) && !visible && (openKey != null &&
			(event instanceof InputEvent.KeyInputEvent ?
				openKey.isActiveAndMatches(InputMappings.getInputByCode(((InputEvent.KeyInputEvent) event).getKey(), ((InputEvent.KeyInputEvent) event).getScanCode())) :
				event instanceof InputEvent.MouseInputEvent && openKey.matchesMouseKey(((InputEvent.MouseInputEvent) event).getButton())))) {
			open(previous);
			return true;
		}
		return false;
	}

	public void open(@Nullable SelectOverlay previous) {
		if (minecraft.currentScreen != null)
			return;
		this.previous = previous;
		if (previous != null)
			previous.close();
		this.updateContents();
		this.setVisible(true);
		this.options.forEach(SelectItem::onOverlayOpen);
	}

	public void render(RenderGameOverlayEvent.Pre event) {
		draw(event.getPartialTicks());
	}

	private void draw(float partialTicks) {
		if (!visible)
			return;
		MainWindow window = minecraft.getMainWindow();

		int x = window.getScaledWidth() - menuWidth - 10;
		int y = window.getScaledHeight() - menuHeight;

		boolean sideways = false;
		if ((window.getScaledWidth() - 182) / 2 < menuWidth + 20) {
			sideways = true;
			y -= 24;
		}

		RenderSystem.pushMatrix();
		float shift = yShift(partialTicks);
		float sidewaysShift = shift * ((float) menuWidth / (float) menuHeight) + (40 + menuHeight / 4f)
			+ 8;

		RenderSystem.translatef(sideways ? sidewaysShift : 0, sideways ? 0 : shift, 0);

		RenderSystem.enableBlend();
		RenderSystem.color4f(1, 1, 1, 3 / 4f);

		minecraft.getTextureManager().bindTexture(ExtraTextures.GRAY.getLocation());
		blit(x, y, 0, 0, menuWidth, menuHeight, 16, 16);
		RenderSystem.color4f(1, 1, 1, 1);

		int yPos = y + 4;
		int xPos = x + 4;

		FontRenderer font = minecraft.fontRenderer;

		// TODO add Keybinds

		font.drawStringWithShadow(title.getFormattedText(), xPos, yPos, 0xEEEEEE);

		yPos += 4;

		// TODO: Add entry Keybinds

		yPos += 4;
		yPos += font.FONT_HEIGHT;

		for (int i = 0; i < options.size(); i++) {
			ITextComponent desc = options.get(i).getDescription();
			if (i == selectedOptionIndex)
				desc.applyTextStyles(TextFormatting.UNDERLINE, TextFormatting.ITALIC);
			int lines = Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(desc.getFormattedText(), menuWidth - 8).size();
			font.drawSplitString(desc.getFormattedText(), xPos, yPos, menuWidth - 8, 0xEEEEEE);
			yPos += font.FONT_HEIGHT * lines + 2;
		}

		RenderSystem.popMatrix();
	}

	private float yShift(float partialTicks) {
		return (movingY + (targetY - movingY) * 0.2f * partialTicks);
	}

	public void onClientTick() {
		if (movingY != targetY) {
			movingY += (targetY - movingY) * 0.2;
		}
	}

	protected void adjustTarget() {
		targetY = visible ? -14 : 0;
	}

	public void close() {
		setVisible(false);
	}

	public void updateContents() {
		int fontheight = minecraft.fontRenderer.FONT_HEIGHT;

		this.menuWidth = 158;
		this.menuHeight = 4;
		this.menuHeight += 12; // title

		// todo special keybinds

		menuHeight += 4;

		for (SelectItem option : options) {
			menuHeight += 2 + fontheight * minecraft.fontRenderer.listFormattedStringToWidth(option.getDescription().getFormattedText(), menuWidth - 8).size();
		}

		adjustTarget();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		adjustTarget();
	}

	public void select() {
		getActiveSelectItem().ifPresent(selectItem -> selectItem.onEnter(this));
	}

	public Optional<SelectItem> getActiveSelectItem() {
		if (!this.visible || options.isEmpty())
			return Optional.empty();
		return Optional.of(options.get(selectedOptionIndex % options.size()));
	}

	public Optional<SelectConfig> getActiveSelectConfig() {
		if (!this.visible || options.isEmpty())
			return Optional.empty();
		SelectItem item = options.get(selectedOptionIndex % options.size());
		if (!(item instanceof SelectConfig))
			return Optional.empty();
		return Optional.of(((SelectConfig) item));
	}

	public void advanceSelectionIndex(int i) {
		if (!options.isEmpty()) {
			selectedOptionIndex -= i;
			while (selectedOptionIndex < 0)
				selectedOptionIndex += options.size();
			selectedOptionIndex %= options.size();
		}
	}

	public SelectOverlay register() {
		Manager.overlays.add(this);
		return this;
	}

	public Map<String, SelectConfig> getConfigs() {
		Map<String, SelectConfig> configs = new HashMap<>();
		this.options.stream().filter(option -> option instanceof SelectConfig).forEach(option -> configs.put(((SelectConfig) option).key, ((SelectConfig) option)));
		return configs;
	}

	public void onScroll(InputEvent.MouseScrollEvent event) {
		int amount = (int) Math.signum(event.getScrollDelta());
		if (SchematicPrinterClient.TOOL_SELECT.isKeyDown()) {
			this.advanceSelectionIndex(amount);
			event.setCanceled(true);
		} else if (SchematicPrinterClient.TOOL_CONFIG.isKeyDown()) {
			getActiveSelectConfig().ifPresent(selectConfig -> {
				selectConfig.onScrolled(amount);
				event.setCanceled(true);
			});
		}
		if (!event.isCanceled())
			getActiveSelectItem().ifPresent(item -> item.onScroll(event));
	}
}
