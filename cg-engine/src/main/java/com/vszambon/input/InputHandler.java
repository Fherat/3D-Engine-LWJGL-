package com.vszambon.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InputHandler {

	private long glfwWindow;

	private final Map<Integer, Command> caseCommands;

	private final List<Integer> active;

	private Command defaultCommand = null;

	public InputHandler(long glfwWindow) {
		this.glfwWindow = glfwWindow;
		this.caseCommands = new HashMap<Integer, Command>();
		this.active = new ArrayList<>();
	}

	public void setDefauldCommand(Command command) {
		if (defaultCommand == null) {
			this.defaultCommand = command;
		}
	}

	public void addCommand(Integer key, Command command) {
		if (this.caseCommands.containsKey(key))
			this.caseCommands.remove(key);
		this.caseCommands.put(key, command);
	}

	private Command getCommandById(Integer key) {
		if (this.caseCommands.containsKey(key))
			return this.caseCommands.get(key);
		return this.defaultCommand;
	}

	public void on(Integer key) {
		Command command = getCommandById(key);
		command.execute();
	}

	public void addToActiveList(int key) {
		this.active.add(key);
	}

	public void removeFromActiveList(int key) {
		Iterator<Integer> it = this.active.iterator();
		while (it.hasNext()) {
			if (it.next().equals(key))
				it.remove();
		}
	}
	
	public boolean isActive(int key) {
		Iterator<Integer> it = this.active.iterator();
		while (it.hasNext()) {
			if (it.next().equals(key))
				return true;
		}
		return false;
	}

	public synchronized void execute() {
		this.active.forEach(el -> on(el));
	}

	public long getGLFWindow() {
		return this.glfwWindow;
	}

}
