package com.vszambon.engine;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.vszambon.display.Window;
import com.vszambon.elements.GameLogic;
import com.vszambon.elements.TestGameLogic;

public class App {

	public static void main(String[] args) {
		
		GameLogic logic = new TestGameLogic();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		GameEngine engine = null;
		if (args.length == 0)
			engine = new GameEngine(Window.getInstance(800, 600, Window.WIN_MODE_WINDOWED, "chubaca", false, 65, 30),
					logic);

		else if (args.length != 0 && args[0].equals("full")) {
			engine = new GameEngine(Window.getInstance((int) screenSize.getWidth(), (int) screenSize.getHeight(),
					Window.WIN_MODE_FULL, "chubaca", false, 65, 30), logic);
		} else if (args.length != 0 && args[0].equals("windowed")) {
			engine = new GameEngine(Window.getInstance(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
					Window.WIN_MODE_WINDOWED, "chubaca", false, 65, 30), logic);
		}

		if (engine != null)
			engine.start();

	}
}
