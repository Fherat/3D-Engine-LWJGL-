package com.vszambon.render;

import java.math.BigDecimal;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.vszambon.elements.VirtualModel;

public class Transformations {

	private final Matrix4f projMatrix;

	private final Matrix4f viewMatrix;

	private final Matrix4f modelViewMatrix;

	public Transformations() {
		this.projMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.modelViewMatrix = new Matrix4f();
	}

	public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
		final var aspectRatio = width / height;
		this.projMatrix.identity().perspective(fov, aspectRatio, zNear, zFar);
		return this.projMatrix;
	}

	public Matrix4f getViewMatrix(Camera camera) {
		final Vector3f camPosition = camera.getPosition();
		final Vector3f focalPosition = camera.getFocalPosition();
		final Vector3f up = new Vector3f(0, 1, 0);

		this.viewMatrix.identity().setLookAt(camPosition, focalPosition, up);
		return this.viewMatrix;
	}

	public Matrix4f getModelViewMatrix(VirtualModel model, Matrix4f viewMatrix) {
		final Vector3f rotation = model.getRotation();

		final BigDecimal rotx = new BigDecimal(Math.toRadians(-rotation.x));
		final BigDecimal roty = new BigDecimal(Math.toRadians(-rotation.y));
		final BigDecimal rotz = new BigDecimal(Math.toRadians(-rotation.z));

		this.modelViewMatrix.identity()
				.translate(model.getPosition())
				.rotateX(rotx.floatValue())
				.rotateY(roty.floatValue())
				.rotateZ(rotz.floatValue())
				.scale(model.getScale());

		final Matrix4f view = new Matrix4f(viewMatrix); // makes a copy, so we dont acumulate mul

		return view.mul(this.modelViewMatrix);
	}

}
