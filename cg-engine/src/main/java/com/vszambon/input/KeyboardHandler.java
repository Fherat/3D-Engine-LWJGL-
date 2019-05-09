package com.vszambon.input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class KeyboardHandler extends InputHandler {

	public KeyboardHandler(long glfwWindow) {
		super(glfwWindow);
		
		setDefauldCommand(new Command() {
			@Override
			public void execute() {
//				System.out.println("this key isn't set");
			}
		});
		
		// Define what happens when a key is pressed in the given window
		glfwSetKeyCallback(getGLFWindow(), (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_UNKNOWN)
				return;
			if (action == GLFW_REPEAT || action == GLFW_PRESS) {
				addToActiveList(key);// handler functions as a dynamic switch
			}
			if (action == GLFW_RELEASE) {
				removeFromActiveList(key);
			}
		});
		// ------------------
	}
}
