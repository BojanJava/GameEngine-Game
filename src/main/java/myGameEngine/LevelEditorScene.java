package myGameEngine;

import org.joml.Vector2f;

import components.SpriteRenderer;
import components.Spritesheet;
import components.Sprite;
import util.AssetPool;

public class LevelEditorScene extends Scene {
	
	private GameObject obj1;
	private Spritesheet sprites;
	
	public LevelEditorScene() {
		
	}
	
	@Override
	public void init() {
		loadResources();
		
		this.camera = new Camera(new Vector2f());
		
		sprites = AssetPool.getSpritesheet("assets/images/spriteSheetT.png");
		
		obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);   
		obj1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/squareR.png"))));
		this.addGameObjectToScene(obj1);
		
		GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 1);   
		obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/squareG.png"))));
		this.addGameObjectToScene(obj2);
		
	}
	
	private void loadResources() {
		AssetPool.getShader("assets/shader/default.glsl");
		
		AssetPool.addSpritesheet("assets/images/spriteSheetT.png",
								 new Spritesheet(AssetPool.getTexture("assets/images/spriteSheetT.png"),
								 16, 16, 6, 0)); 
	}

	@Override
	public void update(float dt) {
		
		for(GameObject go : this.gameObjects) {
			go.update(dt);
		}
		
		this.renderer.render();
	}
	
}
