package com.vszambon.render;

import java.math.BigDecimal;

import org.joml.Vector3f;

/**
 * @author fherat
 *
 */
public class Camera {

	private final Vector3f position;
	private final Vector3f rotation;
	private final Vector3f focalPosition;

	public Camera() {
		this.position = new Vector3f(0, 0, 0);
		this.rotation = new Vector3f(0, 0, 0);
		this.focalPosition = new Vector3f(-2, 0, -5);
	}

	public Camera(Vector3f position, Vector3f rotation, Vector3f focalPosition) {
		this.position = position;
		this.rotation = rotation;
		this.focalPosition = focalPosition;
	}

	public Vector3f getPosition() {
		return this.position;
	}

	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public Vector3f getRotation() {
		return this.rotation;
	}

	public void setRotation(float pitch, float yaw, float row) {
		this.rotation.x = pitch%360;
		this.rotation.y = yaw%360;
		this.rotation.z = row%360;
	}

	public Vector3f getFocalPosition() {
		return this.focalPosition;
	}

	/**
	 * <p>
	 * Moves camera to desired position, dislocating from current position, by
	 * providing an offset.
	 * </p>
	 *
	 * @param xoffset left/right movement (perpendicular to where camera is facing)
	 * @param yoffset up/down movement
	 * @param zoffset foward/backward movement (axial to where camera is facing)
	 */
	public void moveRelativeTo(Vector3f pos, float dist, float pitch, float yaw) {

	    final BigDecimal posx = new BigDecimal((pos.x + (dist*Math.cos(Math.toRadians(pitch))*Math.sin(Math.toRadians(yaw)))));
		final BigDecimal posy = new BigDecimal((pos.y + (dist*Math.sin(Math.toRadians(pitch)))));
		final BigDecimal posz = new BigDecimal((pos.z + (dist*Math.cos(Math.toRadians(pitch))*Math.cos(Math.toRadians(yaw)))));

		this.position.x = posx.floatValue();
		this.position.y = posy.floatValue();
	    this.position.z = posz.floatValue();

	    lookAt(pos);
	}

	private void lookAt(Vector3f pos) {
		this.focalPosition.x = pos.x;
		this.focalPosition.y = pos.y;
		this.focalPosition.z = pos.z;
	}

}
