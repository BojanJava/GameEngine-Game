package myGameEngine;

import org.joml.Vector2f;
import org.joml.Vector4f;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import imgui.ImVec2;
import components.Rigidbody;
import components.Sprite;
import util.AssetPool;

public class LevelEditorScene extends Scene {
	
	private GameObject obj1;
	private Spritesheet sprites;
	SpriteRenderer obj1Sprite;
	
	public LevelEditorScene() {
		
	}
	
	@Override
	public void init() {
		loadResources();
		this.camera = new Camera(new Vector2f());
		sprites = AssetPool.getSpritesheet("assets/images/spriteSheetT.png");
		if(levelLoaded) {
			this.activeGameObject = gameObjects.get(0);
			return;
		}
		
		
		obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
		obj1Sprite = new SpriteRenderer();
		obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
		obj1.addComponent(obj1Sprite);
		obj1.addComponent(new Rigidbody());
		this.addGameObjectToScene(obj1);
		this.activeGameObject = obj1;
		
		GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 1);   
		SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
		Sprite obj2Sprite = new Sprite();
		obj2Sprite.setTexture(AssetPool.getTexture("assets/images/squareG.png"));
		obj2SpriteRenderer.setSprite(obj2Sprite);
		obj2.addComponent(obj2SpriteRenderer);
		this.addGameObjectToScene(obj2);
		
	}
	
	private void loadResources() {
		AssetPool.getShader("assets/shader/default.glsl");
		
		AssetPool.addSpritesheet("assets/images/spriteSheetT.png",
								 new Spritesheet(AssetPool.getTexture("assets/images/spriteSheetT.png"),
								 16, 16, 6, 0)); 
		AssetPool.getTexture("assets/images/squareG.png");
	}

	@Override
	public void update(float dt) {
		
		for(GameObject go : this.gameObjects) {
			go.update(dt);
		}
		
		this.renderer.render();
	}
	
	@Override
	public void imgui() {
		ImGui.begin("Test Window");
		
		ImVec2 windowPos = new ImVec2();
		ImGui.getWindowPos(windowPos);
		ImVec2 windowSize = new ImVec2();
		ImGui.getWindowSize(windowSize);
		ImVec2 itemSpacing = new ImVec2();
		ImGui.getStyle().getItemSpacing(itemSpacing);
		
		float windowX2 = windowPos.x + windowSize.x;		// Rightmost coordinate of the window on the screen.   
		for(int i = 0; i < sprites.size(); i++) {
			Sprite sprite = sprites.getSprite(i);
			float spriteWidth = sprite.getWidth() * 4;
			float spriteHeight = sprite.getHeight() * 4;
			int id = sprite.getTexId();
			Vector2f[] texCoords = sprite.getTexCoords();
			
			ImGui.pushID(i);
			if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {   
				System.out.println("Button " + i + "clicked");
			}
			ImGui.popID();
			
			ImVec2 lastButtonPos = new ImVec2();
			ImGui.getItemRectMax(lastButtonPos);
			float lastButtonX2 = lastButtonPos.x;
			float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
			if(i + 1 < sprites.size() && nextButtonX2 < windowX2) {
				ImGui.sameLine();
			}
		}
		
		ImGui.end();
	}
	
}
