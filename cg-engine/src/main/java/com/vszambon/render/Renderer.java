package com.vszambon.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.List;

import org.joml.Matrix4f;

import com.vszambon.display.Window;
import com.vszambon.elements.VirtualModel;

public class Renderer {

	private ShaderProgram sprogram;

	private float fov = (float) Math.toRadians(60.0);
	private float zNear = 0.01f;
	private float zFar = 1000.f;

	private Transformations tranformations;

	private Matrix4f projectionMatrix;

	public Renderer() {
		this.tranformations = new Transformations();
	}

	public void init(Window window) throws Exception {
		this.sprogram = new ShaderProgram("/shaders/vertex.vs", "/shaders/fragment.fs");
		this.sprogram.link();

		this.sprogram.createUniform("projectionMatrix");
		this.sprogram.createUniform("modelViewMatrix");
		this.sprogram.createUniform("texture_sampler");

		this.projectionMatrix = tranformations.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), zNear,
				zFar);

		window.setClearColor(0, 0, 0, 1);
	}

	private void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void render(Window window, Camera camera, List<VirtualModel> models) {
		clear();

		if (window.isResized()) {
			glViewport(0, 0, window.getWidth(), window.getHeight());
			projectionMatrix = tranformations.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), zNear,
					zFar);
			window.setResized(false);
		}

		sprogram.start();

		sprogram.setUniform("projectionMatrix", projectionMatrix);

		sprogram.setUniform("texture_sampler", 0);

		var viewMatrix = tranformations.getViewMatrix(camera);

		for (VirtualModel model : models) {
			var modelViewMatrix = tranformations.getModelViewMatrix(model, viewMatrix);
			sprogram.setUniform("modelViewMatrix", modelViewMatrix);
			model.getMesh().draw();
		}

		sprogram.finish();

	}

	public void cleanUp() {
		if (sprogram != null) {
			sprogram.cleanUp();
		}
	}

}
