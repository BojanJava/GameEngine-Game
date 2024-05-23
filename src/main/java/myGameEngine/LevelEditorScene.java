package myGameEngine;

import org.joml.Vector2f;
import components.SpriteRenderer;
import components.Spritesheet;
import components.Sprite;
import util.AssetPool;

public class LevelEditorScene extends Scene {
	
	public LevelEditorScene() {
		
	}
	
	@Override
	public void init() {
		loadResources();
		
		this.camera = new Camera(new Vector2f());
		
		Spritesheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheet.png");
		
		GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));   
		obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
		this.addGameObjectToScene(obj1);
		
		GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));   
		obj2.addComponent(new SpriteRenderer(sprites.getSprite(5)));
		this.addGameObjectToScene(obj2);
	}
	
	private void loadResources() {
		AssetPool.getShader("assets/shader/default.glsl");
		
		AssetPool.addSpritesheet("assets/images/spriteSheet.png",
								 new Spritesheet(AssetPool.getTexture("assets/images/spriteSheet.png"),
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
