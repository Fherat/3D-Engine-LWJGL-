package com.vszambon.elements;

import com.vszambon.display.Window;

public interface GameLogic {
	
	void init(Window window) throws Exception;
	
	void constUpdate();

	void update(float interval);
        
	void render(Window window);
	
	void cleanUp();
}
