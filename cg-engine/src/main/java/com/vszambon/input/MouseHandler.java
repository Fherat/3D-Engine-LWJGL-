package com.vszambon.input;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.awt.MouseInfo;
import java.awt.Point;

public class MouseHandler extends InputHandler {

	private double posx;
	private double posy;

	private boolean insideWindow;
	
	private int scroll = 0;

	public MouseHandler(long glfwWindow) {
		super(glfwWindow);

		setDefauldCommand(new Command() {
			@Override
			public void execute() {
//				Point p = MouseInfo.getPointerInfo().getLocation();
//				System.out.println(p.getX() + "||" + p.getY());
//				System.out.println("this key isn't set");
			}
		});

		glfwSetMouseButtonCallback(glfwWindow, (window, button, action, mods) -> {
			if (action == GLFW_PRESS || action == GLFW_REPEAT) {
				addToActiveList(button);
			}
			if (action == GLFW_RELEASE) {
				removeFromActiveList(button);
			}
		});

		glfwSetScrollCallback(glfwWindow, (window, xoofset, yoffset) -> {
			if (yoffset < 0) {
				addToActiveList(13); //scroll UP
			} else if (yoffset > 0) {
				addToActiveList(14); //scroll DOWN
			}
		});

		glfwSetCursorPosCallback(glfwWindow, (window, xpos, ypos) -> {
			this.setPosx(xpos);
			this.setPosy(ypos);
		});

		glfwSetCursorEnterCallback(glfwWindow, (windowHandle, entered) -> {
			this.insideWindow = entered;
		});

	}
	
	public synchronized void execute() {
		super.execute();
		removeFromActiveList(13);
		removeFromActiveList(14);
	}

	public void hideCursor() {
		glfwSetInputMode(getGLFWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);// makes the cursor invisible.
	}

	public void showCursor() {
		glfwSetInputMode(getGLFWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}

	public boolean isInsideWindow() {
		return this.insideWindow;
	}

	public double getPosx() {
		return posx;
	}

	private void setPosx(double posx) {
		this.posx = posx;
	}

	public double getPosy() {
		return posy;
	}

	private void setPosy(double posy) {
		this.posy = posy;
	}

	public int getScroll() {
		return scroll;
	}

	public void setScroll(int scroll) {
		this.scroll = scroll;
	}

}
