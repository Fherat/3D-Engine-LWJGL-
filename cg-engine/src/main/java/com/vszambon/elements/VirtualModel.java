package com.vszambon.elements;

import org.joml.Vector3f;

import com.vszambon.render.Mesh;

public class VirtualModel {

	private final Mesh mesh;

	private final Vector3f position;
	private final Vector3f rotation;
	private float scale;

	public VirtualModel(Mesh mesh) {
		this.mesh = mesh;
		this.position = new Vector3f();
		this.rotation = new Vector3f();
		this.scale = 1;
	}

	public Mesh getMesh() {
		return this.mesh;
	}

	public Vector3f getPosition() {
		return this.position;
	}

	public Vector3f getRotation() {
		return this.rotation;
	}

	public float getScale() {
		return this.scale;
	}

	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public void setRotation(float x, float y, float z) {
		this.rotation.x = x%360;
		this.rotation.y = y%360;
		this.rotation.z = z%360;
	}

	public void increaseRotation(float x, float y, float z) {
		this.rotation.x = (this.rotation.x+(x%360))%360;
		this.rotation.y = (this.rotation.y+(y%360))%360;
		this.rotation.z = (this.rotation.z+(z%360))%360;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
}
