package com.vszambon.render;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private String file;

	private final int id;

	public Texture(int id) {
		this.id = id;
	}
	
	public Texture(String file, boolean path) throws IOException {
		this(loadTexture(file, path));
		this.file = file;
	}

	private static int loadTexture(String file, boolean path) throws IOException {

		PNGDecoder pngDecoder;

		if (path) {
			InputStream in = new FileInputStream(file);
			pngDecoder = new PNGDecoder(in);
		} else {
			pngDecoder = new PNGDecoder(Texture.class.getResourceAsStream(file));
		}

		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * pngDecoder.getWidth() * pngDecoder.getHeight());

		pngDecoder.decode(buffer, 4 * pngDecoder.getWidth(), Format.RGBA);
		buffer.flip();

		var id = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, id);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, pngDecoder.getWidth(), pngDecoder.getHeight(), 0, GL_RGBA,
				GL_UNSIGNED_BYTE, buffer);

		glGenerateMipmap(GL_TEXTURE_2D); // used in place of glTexParameteri
		
		System.out.println(file + " " + " loaded Susccesfully!");

		return id;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void cleanup() {
        glDeleteTextures(id);
    }

	public String getFile() {
		return file;
	}

	public int getId() {
		return id;
	}

}
