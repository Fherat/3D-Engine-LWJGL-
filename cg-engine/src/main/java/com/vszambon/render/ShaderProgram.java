package com.vszambon.render;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public class ShaderProgram {

	private final int programId;

	private int vertexShaderId;

	private int fragmentShaderId;

	private final Map<String, Integer> uniforms;

	public ShaderProgram(String... shaders) throws Exception {
		this.programId = createProgram();
		for (int i = 0; i < shaders.length; i++) {
			loadShaderToProgram(shaders[i]);
		}
		this.uniforms = new HashMap<>();
	}

	private int createProgram() throws Exception {
		var id = glCreateProgram();
		if (id == 0) {
			throw new Exception("Failed to create program!");
		}
		System.out.println("Shader program started succesfully!");
		return id;
	}

	private void createVertexShader(String code) throws Exception {
		this.vertexShaderId = createShader(code, GL_VERTEX_SHADER);
	}

	private void createFragmentShader(String code) throws Exception {
		this.fragmentShaderId = createShader(code, GL_FRAGMENT_SHADER);
	}

	public int createShader(String code, int type) throws Exception {
		var shaderId = glCreateShader(type);
		if (shaderId == 0) {
			throw new Exception("Failed to create Shader!");
		}

		glShaderSource(shaderId, code);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			throw new Exception("Failed to compile shader code: " + glGetShaderInfoLog(shaderId, 1024));
		}

		glAttachShader(this.programId, shaderId);

		return shaderId;
	}

	public void link() throws Exception {
		glLinkProgram(this.programId);

		if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			throw new Exception("Fail to link program: " + glGetProgramInfoLog(programId, 1024));
		}

		// detach shaders if attached
		if (vertexShaderId != 0) {
			glDetachShader(programId, vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			glDetachShader(programId, fragmentShaderId);
		}

		glValidateProgram(programId);

		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
			System.out.println("Fail to validade the program code: " + glGetShaderInfoLog(programId, 1024));
		}

	}

	public void createUniform(String uniformName) throws Exception {
		int location = glGetUniformLocation(programId, uniformName); // <- get uniform location specified in shader code
		if (location < 0) {
			throw new Exception("Uniform not found: " + uniformName); // <- no uniform with given name found in shader
																		// code
		}
		uniforms.put(uniformName, location);
	}

	public void setUniform(String uniformName, Matrix4f matrix) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			var buffer = stack.mallocFloat(16); // <- size of 4x4 matrix
			matrix.get(buffer); // <- store matrix values into buffer
			glUniformMatrix4fv(this.uniforms.get(uniformName), false, buffer);
		}
	}

	public void setUniform(String uniformName, int value) {
		glUniform1i(this.uniforms.get(uniformName), value);
	}

	public void start() {
		glUseProgram(programId);
	}

	public void finish() {
		glUseProgram(0);
	}

	public void cleanUp() {
		finish();
		if (vertexShaderId != 0) {
			glDeleteShader(vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			glDeleteShader(fragmentShaderId);
		}
		if (programId != 0) {
			glDeleteProgram(programId);
		}
	}

	private void loadShaderToProgram(String fileName) throws Exception {
		StringBuilder result = new StringBuilder();
		String line;
		Long start = System.currentTimeMillis();
		try (InputStream in = ShaderProgram.class.getResourceAsStream(fileName);
				BufferedReader buff = new BufferedReader(new InputStreamReader(in,"UTF-8"))) {
			while((line = buff.readLine()) != null) {
				result.append(line).append("\n");
			}
			buff.close();
		}
		if (fileName.contains(".vs")) {
			createVertexShader(result.toString());
		}
		if (fileName.contains(".fs")) {
			createFragmentShader(result.toString());
		}
		System.out.println("Time: " + (System.currentTimeMillis() - start));
		System.out.println(fileName + " loaded Succesfully!");
	}

}
