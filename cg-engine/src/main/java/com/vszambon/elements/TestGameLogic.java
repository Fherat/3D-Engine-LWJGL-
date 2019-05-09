package com.vszambon.elements;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_SUBTRACT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import com.vszambon.display.Window;
import com.vszambon.render.Camera;
import com.vszambon.render.Mesh;
import com.vszambon.render.Renderer;
import com.vszambon.render.Texture;

public class TestGameLogic implements GameLogic {

	private int direction = 0;

	private float color = 0.0f;

	private final Renderer renderer;

	private final Camera camera;

	private final Vector3f camInteraction;
	private int yaw = 0;

	private final List<VirtualModel> gameItems;

	private VirtualModel player;

	private float zoom;

	public TestGameLogic() {
		this.renderer = new Renderer();
		this.camera = new Camera();
		this.camInteraction = new Vector3f();
		this.gameItems = new ArrayList<>();
		this.zoom = 5;
	}

	@Override
	public void constUpdate() {
		this.color += this.direction * 0.01f;
		if (this.color > 1)
			this.color = 1.0f;
		else if (this.color < 0)
			this.color = 0.0f;
	}

	@Override
	public void init(Window window) throws Exception {
		this.renderer.init(window);

		setInputs(window);

		this.camera.setPosition(0, 0, 0);

		final float[] positions = new float[] {
				// V0
				-0.5f, 0.5f, 0.5f,
				// V1
				-0.5f, -0.5f, 0.5f,
				// V2
				0.5f, -0.5f, 0.5f,
				// V3
				0.5f, 0.5f, 0.5f,
				// V4
				-0.5f, 0.5f, -0.5f,
				// V5
				0.5f, 0.5f, -0.5f,
				// V6
				-0.5f, -0.5f, -0.5f,
				// V7
				0.5f, -0.5f, -0.5f,

				// For text coords in top face
				// V8: V4 repeated
				-0.5f, 0.5f, -0.5f,
				// V9: V5 repeated
				0.5f, 0.5f, -0.5f,
				// V10: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V11: V3 repeated
				0.5f, 0.5f, 0.5f,

				// For text coords in right face
				// V12: V3 repeated
				0.5f, 0.5f, 0.5f,
				// V13: V2 repeated
				0.5f, -0.5f, 0.5f,

				// For text coords in left face
				// V14: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V15: V1 repeated
				-0.5f, -0.5f, 0.5f,

				// For text coords in bottom face
				// V16: V6 repeated
				-0.5f, -0.5f, -0.5f,
				// V17: V7 repeated
				0.5f, -0.5f, -0.5f,
				// V18: V1 repeated
				-0.5f, -0.5f, 0.5f,
				// V19: V2 repeated
				0.5f, -0.5f, 0.5f, };
		final float[] textCoords = new float[] {

				0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f,

				0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f,

				// For text coords in top face
				0.0f, 0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.5f, 1.0f,

				// For text coords in right face
				0.0f, 0.0f, 0.0f, 0.5f,

				// For text coords in left face
				0.5f, 0.0f, 0.5f, 0.5f,

				// For text coords in bottom face
				0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, };

		final int[] indices = new int[] {
				// Front face
				0, 1, 3, 3, 1, 2,
				// Top Face
				8, 10, 11, 9, 8, 11,
				// Right face
				12, 13, 7, 5, 12, 7,
				// Left face
				14, 15, 6, 4, 14, 6,
				// Bottom face
				16, 18, 19, 17, 16, 19,
				// Back face
				4, 6, 7, 5, 4, 7, };
		final float[] normals = new float[] {};
		VirtualModel entity;
		final Texture texture = new Texture("/textures/grassblock.png", false);
		final Mesh mesh = new Mesh(positions, textCoords, normals, indices);
		mesh.setTexture(texture);

		final Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			entity = new VirtualModel(mesh);
			entity.setPosition(-2f + rand.nextInt(5), -2f + rand.nextInt(5), -9f + rand.nextInt(4));
			this.gameItems.add(entity);
		}
		this.player = new VirtualModel(mesh);
		this.player.setPosition(-2f + rand.nextInt(5), -2, -9f + rand.nextInt(4));
		this.gameItems.add(this.player);
	}

	private void setInputs(Window window) {
		// create Inputs
		window.getKeyHandler().addCommand(GLFW_KEY_W, () -> {
			this.camInteraction.z = -1;
		});
		window.getKeyHandler().addCommand(GLFW_KEY_S, () -> {
			this.camInteraction.z = 1;
		});
		window.getKeyHandler().addCommand(GLFW_KEY_A, () -> {
			if (window.getMouseHandler().isActive(GLFW_MOUSE_BUTTON_2))
				this.camInteraction.x = -1;
			else
				this.yaw = -1;
		});
		window.getKeyHandler().addCommand(GLFW_KEY_D, () -> {
			if (window.getMouseHandler().isActive(GLFW_MOUSE_BUTTON_2))
				this.camInteraction.x = 1;
			else
				this.yaw = 1;
		});

		window.getMouseHandler().addCommand(13, () -> {
			this.zoom += 1;
		});
		window.getMouseHandler().addCommand(14, () -> {
			this.zoom -= 1;
		});

		window.getKeyHandler().addCommand(GLFW_KEY_KP_ADD, () -> {
			this.direction = 1;
		});
		window.getKeyHandler().addCommand(GLFW_KEY_KP_SUBTRACT, () -> {
			this.direction = -1;
		});

	}

	@Override
	public void update(float interval) {
		final float posStep = 0.05f;

		this.player.increaseRotation(0, this.yaw, 0);

		final BigDecimal posx = new BigDecimal(Math.sin(Math.toRadians(-this.player.getRotation().y)));
		final BigDecimal posz = new BigDecimal(Math.cos(Math.toRadians(-this.player.getRotation().y)));

		System.out.println("POSX: "+posx.toString());
		System.out.println("POSZ: "+posz.toString());
		if(this.camInteraction.z !=0)
			this.player.setPosition(
					this.player.getPosition().x + (posx.floatValue()*posStep*this.camInteraction.z),
					this.player.getPosition().y,
					this.player.getPosition().z + (posz.floatValue()*posStep*this.camInteraction.z)
					);
		this.camera.moveRelativeTo(this.player.getPosition(),this.zoom%360,25,-this.player.getRotation().y);
		this.camInteraction.set(0, 0, 0);
		this.yaw = 0;
	}

	@Override
	public void render(Window window) {
		window.setClearColor(this.color, this.color, this.color, 1);
		this.renderer.render(window, this.camera, this.gameItems);
	}

	@Override
	public void cleanUp() {
		this.renderer.cleanUp();
		for (final VirtualModel obj : this.gameItems)
			obj.getMesh().cleanUp();
	}

}
