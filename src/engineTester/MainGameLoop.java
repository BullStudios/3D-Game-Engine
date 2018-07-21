package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		// *********TERRAIN TEXTURE STUFF***********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		// *****************************************

		RawModel model = OBJLoader.loadObjModel("tree", loader);

		TexturedModel tree = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("flower")));

		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
		fernTexture.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTexture);

		TexturedModel bobble = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader), new ModelTexture(loader.loadTexture("lowPolyTree")));

		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);

		Light light = new Light(new Vector3f(0, 10000, -10000), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		

		MasterRenderer renderer = new MasterRenderer();

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < 500; i++) {
			if (i % 7 == 0) {
				float x = random.nextFloat() * 800 - 400;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));

				x = random.nextFloat() * 800 - 400;
				z = random.nextFloat() * -600;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1.8f));

				x = random.nextFloat() * 800 - 400;
				z = random.nextFloat() * -600;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(flower, new Vector3f(x, y, z), 0, 0, 0, 2.3f));
			}
			if (i % 3 == 0) {
				float x = random.nextFloat() * 800 - 400;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(bobble, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));

				x = random.nextFloat() * 800 - 400;
				z = random.nextFloat() * -600;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(tree, new Vector3f(x, y, z), 0, 0, 0, random.nextFloat() * 1 + 4));
			}

		}

		RawModel playerModel = OBJLoader.loadObjModel("player", loader);
		TexturedModel playerTexturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));

		Player player = new Player(playerTexturedModel, new Vector3f(100, 5, -150), 0, 180, 0, 0.6f);
		Camera camera = new Camera(player);

		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}
}
