package com.vszambon.engine;

import java.awt.MouseInfo;
import java.awt.Point;

import com.vszambon.display.FramesController;
import com.vszambon.display.Window;
import com.vszambon.elements.GameLogic;

public class GameEngine implements Runnable {

	private final Window window;
	
	private final FramesController frameController;

	private final Thread gameLoop;

	private final GameLogic logic;

	public GameEngine(Window window, GameLogic logic) {
		this.window = window;
		this.frameController = new FramesController(window.getFPS(), window.getUPS());
		this.logic = logic;
		gameLoop = new Thread(this, "Game test");
	}

	public void start() {
		String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) {
			gameLoop.run();
		} else {
			gameLoop.start();
		}
	}

	public void init() throws Exception {
		window.init();
		logic.init(window);
	}

	public void gameLoop() throws Exception {

		while (!window.shouldClose()) {
			
//			Point mouse = MouseInfo.getPointerInfo().getLocation();
//			window.setPosition(mouse.x, mouse.y);
			window.handleInput();

			frameController.step();

			frameController.constantUpdater(() -> {
				logic.constUpdate();
			});
			
			logic.update(0);

			logic.render(window);
			
			window.update();

			if(!window.isvSync()) frameController.sync();

//			System.out.println(frameController.getFps());
			
		}

		window.destroy();
	}
	
	private void cleanUp() {
		logic.cleanUp();
	}

	@Override
	public void run() {

		try {
			init();
			gameLoop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            cleanUp();
        }

	}

}
