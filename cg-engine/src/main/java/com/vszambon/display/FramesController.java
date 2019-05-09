package com.vszambon.display;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FramesController {

	private int frameCounter = 0;

	private double maxFPS;

	private int fps;

	private double loopStartTime;
	private double elapsed;
	private double previous;

	private double secsPerUpdate;
	private double steps = 0.0f;

	private boolean step = false;

	public FramesController(double targetFPS, double targetUPS) {
		this.maxFPS = targetFPS;
		this.secsPerUpdate = 1 / targetUPS;
		this.previous = glfwGetTime();
	}

	public double getMaxFPS() {
		return maxFPS;
	}

	public void setMaxFPS(int maxFPS) {
		this.maxFPS = maxFPS;
	}

	public double getSecsPerUpdate() {
		return secsPerUpdate;
	}

	public void setSecsPerUpdate(int secsPerUpdate) {
		this.secsPerUpdate = secsPerUpdate;
	}

	public double getFps() {
		return fps;
	}

	public void step() {
		if (!step)
			step = true;
		
		frameCounter++;

		loopStartTime = glfwGetTime();
		elapsed = loopStartTime - previous;
		previous = loopStartTime;
		steps += elapsed;
	}

	public void constantUpdater(ConstantUpdate constant) throws Exception {
		if (!step)
			throw new Exception("Should make a step first.");

		while (steps >= secsPerUpdate) {
			constant.constUpdate();
			steps -= secsPerUpdate;
		}
		
	}

	public void sync() throws Exception {
		if (!step)
			throw new Exception("Should make a step first.");
		double loopSlot = 1f / maxFPS;
		double endTime = loopStartTime + loopSlot;
		
		while (glfwGetTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}
		fpsCounter();
		step = false;
	}

	private void fpsCounter() {
		double time = glfwGetTime();
		if (time - this.loopStartTime > 0) {
			this.fps = (int)(this.frameCounter / (time - this.loopStartTime));
			this.frameCounter = 0;
		}
	}
}
