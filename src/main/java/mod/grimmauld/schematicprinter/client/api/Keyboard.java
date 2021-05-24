package mod.grimmauld.schematicprinter.client.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static org.lwjgl.glfw.GLFW.*;

@OnlyIn(Dist.CLIENT)
public enum Keyboard {
	LALT(GLFW_KEY_LEFT_ALT), // 342
	O(79),
	ENTER(GLFW_KEY_ENTER), // 257
	CTRL(GLFW_KEY_LEFT_CONTROL), // 341r
	ESC(GLFW_KEY_ESCAPE);

	public static final int PRESS = 1;
	public static final int HOLD = 2;
	public static final int RELEASE = 0;

	private final int keycode;

	Keyboard(int keycode) {
		this.keycode = keycode;
	}

	public static boolean isKeyDown(int key) {
		return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), key);
	}

	public boolean isKeyDown() {
		return isKeyDown(keycode);
	}

	public int getKeycode() {
		return keycode;
	}
}
