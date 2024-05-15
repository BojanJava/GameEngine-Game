package myGameEngine;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import renderer.Shader;
import renderer.Texture;
import util.Time;

public class LevelEditorScene extends Scene {
	
	// Since we are passing data from the CPU to the GPU we
	// need, sort of, identifiers, so we can know what we are 
	// working with when we are talking to the GPU.
	private int vertexID;
	private int fragmentID;
	private int shaderProgram;
	
	private float[] vertexArray = {
		// position (x, y, z)     // color (r, g, b, a)       // UV coordinates 
		100.5f, 0.5f,   0.0f, 	  1.0f, 0.0f, 0.0f, 1.0f,     1, 1,        	// Bottom right. (0)
	    0.5f,   100.5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,	  0, 0,      	// Top left.     (1)
		100.5f, 100.5f, 0.0f,     1.0f, 0.0f, 1.0f, 1.0f,     1, 0,      	// Top right.    (2)
	    0.5f,   0.5f,   0.0f,     1.0f, 1.0f, 0.0f, 1.0f,     0, 1,         // Bottom left.  (3)
		// (z - depth (3d))
		// (a - alpha - specifies the opacity for a color)
		// (opacity (opaque) - non-transparency (non-transparent)).
	};
	
	private int[] elementArray = {
		// IMPORTANT: Must be in counter-clockwise order.
		2, 1, 0,    // Top right triangle.
		0, 1, 3     // Bottom left triangle.
	};
	
	private int vaoID;    // Vertex Array Object.				    		    		
	private int vboID;    // Vertex Buffer Object.
	private int eboID;    // Element Buffer Object.
	
	private Shader defaultShader;
	private Texture testTexture;
	
	public LevelEditorScene () {
		
	}
	
	@Override
	public void init() {
		
		this.camera = new Camera(new Vector2f(-200, -300));
		defaultShader = new Shader("assets/shader/default.glsl");
		defaultShader.compile();
		this.testTexture = new Texture("assets/images/testImage15.png");
		
		// Generate VAO, VBO and EBO buffer objects, and send to GPU.
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		// Create a float buffer of vertices.
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip();
		
		// Create VBO, upload the vertex buffer.
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		// Create the indices and upload.
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();
		
		eboID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
		
		// Add the vertex attribute pointers. (Tell the GPU arrangement in our vertexArray).
		int positionsSize = 3;
		int colorSize = 4;
		int uvSize = 2;
		int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
		
		// First element in parentheses, 0, because we set location=0 in 'default.glsl' for position.
		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);
		
		// First element in parentheses, 1, because we set location=1 in 'default.glsl' for color.
		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);   
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);   
		glEnableVertexAttribArray(2);
		
	}

	@Override
	public void update(float dt) {
		
		// camera.position.x -= dt * 50.0f;
		// camera.position.y -= dt * 20.0f;
		
		defaultShader.use();
		
		// Upload texture to shader.
		defaultShader.uploadTexture("TEX_SAMPLER", 0);
		glActiveTexture(GL_TEXTURE0);
		testTexture.bind();
		
		defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
		defaultShader.uploadMat4f("uView", camera.getViewMatrix());
		defaultShader.uploadFloat("uTime", Time.getTime());
		// Bind the VAO that we are using.
		glBindVertexArray(vaoID);
		
		// Enable the vertex attribute pointers.
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
		
		// Un-bind everything.
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		// 0 here means bind nothing.
		glBindVertexArray(0);
		
		defaultShader.detach();
	}
	
}
