package myGameEngine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

public class Window {
	
	private int width;
	private int height;
	private String title;
	private long glfwWindow;
	private ImGuiLayer imguiLayer;
	
	public float r;
	public float g;
	public float b;
	public float a;
	
	private static Window window = null;		// Singleton.

	private static Scene currentScene;
	
	private Window() {					

		this.width = 1024;
		this.height = 768;
		this.title = "Unknown";	
		r = 1;
		g = 1;
		b = 1;
		a = 1;
	}
	
	public static void changeScene(int newScene) {
		
		switch(newScene) {
			case 0:
				currentScene = new LevelEditorScene();
				currentScene.init();
				currentScene.start();
				break;
			case 1:
				currentScene = new LevelScene();
				currentScene.init();
				currentScene.start();
				break;
			default:
				assert false : "Unknown scene" + newScene;
				break;
		}
	}
	
	public static Window get() {
		
		if(window == null) {
			window = new Window();
		}	
		
		return window;
	}
	
	public static Scene getScene() {
		return get().currentScene;
	}
	
	public void run() {
		
		System.out.println("LWJGL " + Version.getVersion() + " Active");   
		
		initialize();
		loop();
		
		// We use C bindings with LWJGL, so we allocate
		// memory ourself, so we need to free the memory,
		// once we exit the loop. (OS should do that, but just in case)   
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		
		// Terminate GLFW and free the error callback.
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public void initialize() {
		// Setup an error callback. (something like terminal)
		
		// create printing method, where the errors will be printed to.   
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW.
		if(!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW.");   
		}
		// Configure GLFW.
		glfwDefaultWindowHints();		// Hints - Re-sizable, visible...
		// Not visible until we are done creating the window.
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		
		// Create the window.
		
		// Memory address for this window.
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);   
		
		if(glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create the GLFW window.");
		}
		// Lambda.
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
			Window.setWidth(newWidth);
			Window.setHeight(newHeight);
		});
		
		// Make the OpenGL context current.
		glfwMakeContextCurrent(glfwWindow);
		// Enable v-sync.
		// Swap every single frame, no restrictions. So we go as fast as we can. 
		glfwSwapInterval(1);
		
		// Make the window visible.
		glfwShowWindow(glfwWindow);
		
		// This line is critical for LWJGL's inter-operation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		this.imguiLayer = new ImGuiLayer(glfwWindow);
		this.imguiLayer.initImGui();
		
		Window.changeScene(0);
	}
	
	public void loop() {
		
		float beginTime = (float)glfwGetTime();
		float endTime;
		float dt = -1.0f;
		
		while(!glfwWindowShouldClose(glfwWindow)) {
			// Poll events into our key listeners.
			glfwPollEvents();
			
			glClearColor(r, g, b, a);
			// Flush above color to our entire screen.
			glClear(GL_COLOR_BUFFER_BIT);
			
			if(dt >= 0) {
				currentScene.update(dt);
			}
			
			this.imguiLayer.update(dt, currentScene);
			glfwSwapBuffers(glfwWindow);
			
			endTime = (float)glfwGetTime();
			dt = endTime - beginTime;        // Delta time.
			beginTime = endTime;
		}
	}
	
	public static int getWidth() {
		return get().width;
	}
	
	public static int getHeight() {
		return get().height;
	}
	
	public static void setWidth(int newWidth) {
		get().width = newWidth;
	}
	
	public static void setHeight(int newHeight) {
		get().height = newHeight;
	}
	
}
