package com.vszambon.display;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetDropCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Point;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.vszambon.input.KeyboardHandler;
import com.vszambon.input.MouseHandler;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Window {

	private static Window instance;

	private static boolean glfwState = false;

	private GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

	private String name;

	private Long glfwID;

	private int width;
	private int height;

	private Point pos = new Point();

	private boolean resized = false;

	private boolean shouldClose = false;

	private int winMode;
	public static final int WIN_MODE_FULL = 0;
	public static final int WIN_MODE_WINDOWED = 1;

	private String dropedFilePath;

	private boolean vSync;

	private WindowCallbacksDefinition callbacks;

	private KeyboardHandler keyHandler;
	private MouseHandler mouseHandler;

	private final int FPS;
	private final int UPS;

	private Window(int width, int height, int mode, String name, boolean vsync, int fps, int ups) {
		if (isInstancied()) {
			throw new InstantiationError("This is a singleton object. This instantiation try isn't allowed!.");
		}

		this.width = width;
		this.height = height;
		this.winMode = mode;
		this.name = name;
		this.vSync = vsync;
		this.FPS = fps;
		this.UPS = ups;

	}

	private Window(int width, int height, int mode, String name, boolean vsync, int fps, int ups,
			WindowCallbacksDefinition callbacks) {
		if (instance != null) {
			throw new InstantiationError("This is a singleton object. This instantiation try isn't allowed!.");
		}

		this.width = width;
		this.height = height;
		this.winMode = mode;
		this.name = name;
		this.vSync = vsync;
		this.callbacks = callbacks;
		this.FPS = fps;
		this.UPS = ups;

	}

	public boolean isInstancied() {
		return (instance != null);
	}

	public static Window getInstance(int width, int height, int mode, String name, boolean vsync, int fps, int ups) {
		if (instance == null) {
			instance = new Window(width, height, mode, name, vsync, fps, ups);
		}
		return instance;
	}

	public static Window getInstance(int width, int height, int mode, String name, boolean vsync, int fps, int ups,
			WindowCallbacksDefinition callbacks) {
		if (instance == null) {
			instance = new Window(width, height, mode, name, vsync, fps, ups, callbacks);
		}
		return instance;
	}

	private void startGLFW() {
		if (!glfwState) {
			glfwSetErrorCallback(errorCallback);
			glfwState = glfwInit();
			if (!glfwState) {
				throw new IllegalStateException("Unable to initialize GLFW");
			}
		}
	}

	public static void terminateGLFW() {
		if (glfwState) {
			glfwTerminate();
			glfwSetErrorCallback(null).free();
			glfwState = false;
			System.out.println("GLFW Terminated!");
			System.out.println("Error Callback free!");
		}
	}

	private void create() {

		glfwID = glfwCreateWindow(width, height, name, (this.winMode == 0) ? glfwGetPrimaryMonitor() : NULL, NULL);

		if (glfwID == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		System.out.println("Window \"" + name + "\" created Succesfully!");
	}

	public void init() {

		startGLFW();

		setDefaultHints();

		create();
		
		try {
			setIcon("/textures/icon.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		center();

		show();

		if (callbacks == null)
			setDefaultCallbacks();
		else
			callbacks.define();

		this.keyHandler = new KeyboardHandler(getGlfwID());

		this.keyHandler.addCommand(GLFW_KEY_ESCAPE, () -> {
			this.shouldClose = true;
		});
		
		this.mouseHandler = new MouseHandler(getGlfwID());

		// Make the OpenGL context current
		glfwMakeContextCurrent(this.glfwID);

		if (this.vSync) {
			// Enable v-sync
			glfwSwapInterval(1);
			System.out.println("V-Sync active!");
		}

		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		glEnable(GL_DEPTH_TEST);
	}

	public void update() {
		glfwSwapBuffers(this.glfwID);
		glfwPollEvents();
	}

	public void destroy() {
		if (instance != null) {
			hide();
			glfwFreeCallbacks(this.glfwID);
			glfwDestroyWindow(this.glfwID);
			this.glfwID = null;
			instance = null;
			System.out.println("Window Destroyed!");
		} else {
			System.out.println("No Window instancied");
		}
		terminateGLFW();
	}

	public void show() {
		glfwShowWindow(this.glfwID);
	}

	public void hide() {
		glfwHideWindow(this.glfwID);
	}

	public void center() {
		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(this.glfwID, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			this.pos.x = (vidmode.width() - pWidth.get(0)) / 2;
			this.pos.y = (vidmode.height() - pHeight.get(0)) / 2;

			// Center the window
			glfwSetWindowPos(this.glfwID, this.pos.x, this.pos.y);
		}
		// -----------------
	}

	public void handleInput() {
		getKeyHandler().execute();
		getMouseHandler().execute();
	}

	public void setPosition(int posx, int posy) {
		glfwSetWindowPos(this.glfwID, posx, posy);
	}

	private void setDefaultCallbacks() {
		glfwSetWindowPosCallback(this.glfwID, (window, xpos, ypos) -> {
			this.pos.x = xpos;
			this.pos.y = ypos;
		});

		glfwSetFramebufferSizeCallback(this.glfwID, (window, winWidth, winHeight) -> {
			this.width = winWidth;
			this.height = winHeight;
			setResized(true);
		});

		glfwSetDropCallback(this.glfwID, (window, count, paths) -> {
			PointerBuffer charPointers = MemoryUtil.memPointerBuffer(paths, count);
			for (int i = 0; i < count; i++) {
				dropedFilePath = MemoryUtil.memUTF8(charPointers.get(i));
				System.err.println(dropedFilePath);
			}
		});
	}

	private void setDefaultHints() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		String osName = System.getProperty("os.name");
		if (osName.contains("Mac") || osName.contains("Linux")) {
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		}
	}

	private void setIcon(String file) throws IOException {

		PNGDecoder pngDecoder;

		pngDecoder = new PNGDecoder(this.getClass().getResourceAsStream(file));

		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * pngDecoder.getWidth() * pngDecoder.getHeight());

		pngDecoder.decode(buffer, 4 * pngDecoder.getWidth(), Format.RGBA);
		buffer.flip();

		glfwSetWindowIcon(this.glfwID, GLFWImage.malloc(1).position(0).width(pngDecoder.getWidth())
				.height(pngDecoder.getHeight()).pixels(buffer));

	}

	public void setClearColor(float r, float g, float b, float alpha) {
		glClearColor(r, g, b, alpha);
	}

	public boolean isResized() {
		return resized;
	}

	public void setResized(boolean state) {
		this.resized = state;
	}

	public String getName() {
		return name;
	}

	public Long getGlfwID() {
		return glfwID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Point getPos() {
		return pos;
	}
	
	public int getFPS() {
		return this.FPS;
	}
	
	public int getUPS() {
		return this.UPS;
	}

	public String getDropedFilePath() {
		return dropedFilePath;
	}

	public void setWinMode(int winMode) {
//		this.winMode = winMode;
	}

	public int getWinMode() {
		return winMode;
	}

	public KeyboardHandler getKeyHandler() {
		return keyHandler;
	}

	public boolean isvSync() {
		return vSync;
	}

	public boolean shouldClose() {
		return shouldClose;
	}

	public void setShouldClose(boolean shouldClose) {
		this.shouldClose = shouldClose;
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}

}
