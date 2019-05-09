package com.vszambon.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class Mesh {

	private final int vaoId;

	private final List<Integer> vbos;

	private int vboCount;

	private final int numOfVertex;

	private Texture texture;
	
	private Vector3f color;

	public Mesh(float[] positions, float[] texture,float[] normals, int[] indices) {
		this.vaoId = createVAO();
		this.vbos = new ArrayList<>();
		this.vboCount = 0;
		this.numOfVertex = indices.length;

		createVBO(vboCount++, positions, 3);
		createVBO(vboCount++, texture, 2);
		createIndexVBO(vboCount++, indices);

		unbindVBOs();
		unbindVAO();
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getNumOfVertex() {
		return numOfVertex;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	private int createVAO() {
		int vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		return vaoID;
	}

	private void createVBO(int attributeNumber, float[] data, int size) {
		int vboID = glGenBuffers();
		vbos.add(vboID);
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		var buffer = storeDataInFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, size, GL_FLOAT, true, 0, 0);
		freeBuffer(buffer);
	}

	private void createIndexVBO(int attributeNumber, int[] data) {
		int vboID = glGenBuffers();
		vbos.add(vboID);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		var buffer = storeDataInIntBuffer(data);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		freeBuffer(buffer);
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		var buffer = MemoryUtil.memAllocFloat(data.length);
		return buffer.put(data).flip();
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		var buffer = MemoryUtil.memAllocInt(data.length);
		return buffer.put(data).flip();
	}

	private void freeBuffer(FloatBuffer buffer) {
		MemoryUtil.memFree(buffer);
	}

	private void freeBuffer(IntBuffer buffer) {
		MemoryUtil.memFree(buffer);
	}

	private void unbindVAO() {
		glBindVertexArray(0);
	}

	private void unbindVBOs() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public void draw() {
		// Activate firs texture bank
		glActiveTexture(GL_TEXTURE0);
		// Bind the texture
		if(texture != null) texture.bind();

		// Draw the mesh
		glBindVertexArray(getVaoId());
		for (int i = 0; i < vbos.size(); i++) {
			glEnableVertexAttribArray(i);
		}

		glDrawElements(GL_TRIANGLES, this.numOfVertex, GL_UNSIGNED_INT, 0);

		// Restore state
		for (int i = 0; i < vbos.size(); i++) {
			glDisableVertexAttribArray(i);
		}
		unbindVAO();
	}

	public void cleanUp() {
		glDisableVertexAttribArray(0);

		// Delete the VBOs
		unbindVBOs();
		for (Integer vbo : vbos) {
			glDeleteBuffers(vbo);
		}
		
		//delete texture
		if(texture != null) texture.cleanup();

		// Delete the VAO
		unbindVAO();
		glDeleteVertexArrays(vaoId);
	}
}
