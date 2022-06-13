package mod.grimmauld.schematicprinter.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.grimmauld.schematicprinter.util.FileHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static mod.grimmauld.schematicprinter.util.TextHelper.translationComponent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StringPromptScreen extends UtilScreen {
	private final Component title;
	private final Component abortLabel = translationComponent("screen.discard");
	private final Component confirmLabel = translationComponent("screen.confirm");
	private final Component folder = translationComponent("screen.open_folder");
	private final Consumer<String> onFinish;
	private EditBox nameField;
	private IconButton confirm;
	private IconButton abort;
	private IconButton folderButton;

	public StringPromptScreen(Consumer<String> onFinish, Component title) {
		this.onFinish = onFinish;
		this.title = title;
	}

	@Override
	public void init() {
		super.init();
		GuiTextures background = GuiTextures.SCHEMATIC_PROMPT;
		setWindowSize(background.width, background.height + 30);

		nameField = new EditBox(font, guiLeft + 49, guiTop + 26, 131, 10, TextComponent.EMPTY);
		nameField.setTextColor(-1);
		nameField.setTextColorUneditable(-1);
		nameField.setBordered(false);
		nameField.setMaxLength(35);
		nameField.changeFocus(true);

		abort = new IconButton(guiLeft + 180, guiTop + 53, GuiIcons.I_TRASH);
		abort.setToolTip(abortLabel);
		widgets.add(abort);

		confirm = new IconButton(guiLeft + 158, guiTop + 53, GuiIcons.I_CONFIRM);
		confirm.setToolTip(confirmLabel);
		widgets.add(confirm);

		folderButton = new IconButton(guiLeft + 21, guiTop + 21, GuiIcons.I_OPEN_FOLDER);
		folderButton.setToolTip(folder);
		widgets.add(folderButton);

		widgets.add(confirm);
		widgets.add(abort);
		widgets.add(nameField);
	}

	@Override
	public void renderWindow(PoseStack ms) {
		GuiTextures.SCHEMATIC_PROMPT.draw(ms, this, guiLeft, guiTop);
		font.drawShadow(ms, title, guiLeft + (sWidth / 2f) - (font.width(title.getContents()) / 2f), guiTop + 3,
			0xffffff);
	}

	@Override
	public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			confirm();
			return true;
		}
		if (keyCode == 256 && this.shouldCloseOnEsc()) {
			this.removed();
			return true;
		}
		return nameField.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (confirm.isHoveredOrFocused()) {
			confirm();
			return true;
		}
		if (abort.isHoveredOrFocused() && MC.player != null) {
			MC.player.closeContainer();
			return true;
		}
		if (folderButton.isHoveredOrFocused()) {
			Util.getPlatform()
				.openFile(Paths.get(FileHelper.schematicFilePath + "/")
					.toFile());
		}
		return super.mouseClicked(x, y, button);
	}

	private void confirm() {
		onFinish.accept(nameField.getValue());
		if (MC.player != null)
			MC.player.closeContainer();
	}
}
