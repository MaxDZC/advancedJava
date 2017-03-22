package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.opengl.Display;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {
    
    public static void main(String[] args) {
        MasterRenderer renderer;
        TerrainTexture backgroundTexture, rTexture, gTexture, bTexture, blendMap;
        TerrainTexturePack texturePack;
        Loader loader;
        Player player;
        TexturedModel staticModel;
		RawModel bunny;
		TexturedModel bun;
        Camera camera;
        Light light;
        Terrain terrain;
        Random random;
        float x, y, z;
        List<Entity> entities;
        int i;    
        ModelTexture fernTextureAtlas;
        List<GuiTexture> guis;
        
        DisplayManager.createDisplay();
        loader = new Loader();

        // path
        backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        // aRoad
        rTexture = new TerrainTexture(loader.loadTexture("mud"));
        // middle
        gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        // road2
        bTexture = new TerrainTexture(loader.loadTexture("path"));           
        
        texturePack = new TerrainTexturePack(backgroundTexture, rTexture, 
                gTexture, bTexture);
        blendMap = new TerrainTexture(loader.loadTexture("straightMap"));
        
        ModelData data = OBJFileLoader.loadOBJ("tree");
                
        RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords()
                , data.getNormals(), data.getIndices());
        
        staticModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel grassyModel = new TexturedModel(
                OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel poly = new TexturedModel(
                OBJLoader.loadObjModel("lowPolyTree", loader), new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel flower = new TexturedModel(
                OBJLoader.loadObjModel("fern", loader), new ModelTexture(loader.loadTexture("flower")));
                
        fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(
                OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
        
        grassyModel.getTexture().setHasTransparency(true);
        grassyModel.getTexture().setUseFakeLighting(true);
        fern.getTexture().setHasTransparency(true);
        
        terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");         

        entities = new ArrayList<>();
        random = new Random();
        
        for(i = 0; i < 600; i++) {
        	if(i % 20 == 0) {
        		x = random.nextFloat() * 800 - 400;
        		z = random.nextFloat() * -600;
        		y = terrain.getHeightOfTerrain(x, z);
        		    
                entities.add(new Entity(fern, random.nextInt(4), 
                        new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
        	}
        	if(i % 5 == 0){
        		x = random.nextFloat() * 800 - 400;
        		z = random.nextFloat() * -600;
        		y = terrain.getHeightOfTerrain(x, z);
        		
                entities.add(new Entity(grassyModel, 
                        new Vector3f(x, y, z), 0, random.nextFloat() * 360, 
                        0, random.nextFloat() * -0.1f + 0.6f));
                
        		x = random.nextFloat() * 800 - 400;
        		z = random.nextFloat() * -600;
        		y = terrain.getHeightOfTerrain(x, z);
                
                entities.add(new Entity(staticModel, 
                        new Vector3f(x, y, z), 0, 0, 0, random.nextFloat() * 1 + 4));     
                
        		x = random.nextFloat() * 800 - 400;
        		z = random.nextFloat() * -600;
        		y = terrain.getHeightOfTerrain(x, z);
                
                entities.add(new Entity(poly, 
                        new Vector3f(x, y, z), 0, 0, 0, 0.8f));
                
        		x = random.nextFloat() * 800 - 400;
        		z = random.nextFloat() * -600;
        		y = terrain.getHeightOfTerrain(x, z);
                
                entities.add(new Entity(flower, 
                        new Vector3f(x, y, z), 0, 0, 0, 1)); 
        	}               
        }
    //    texture = staticModel.getTexture();
      //  texture.setShineDamper(10);
      //  texture.setReflectivity(0.5f);
        
    //    entity = new Entity(staticModel, new Vector3f(0, 0, -25), 
      //          0, 0, 0, 1);
        light = new Light(new Vector3f(20000, 40000, 2000), new Vector3f(1, 1, 1));
        
        renderer = new MasterRenderer();
        
        bunny = OBJLoader.loadObjModel("person", loader);
        bun = new TexturedModel(bunny, new ModelTexture(loader.loadTexture("playerTexture")));
        
        player = new Player(bun, new Vector3f(300, 0, -200), 0, 180, 0, 0.6f);
        camera = new Camera(player);
        
       
        guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), 
        		new Vector2f(-0.7f, 0.85f), new Vector2f(0.3f, 0.6f));
        
        guis.add(gui);
        
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        
        while(!Display.isCloseRequested()) {
       //     entity.increaseRotation(0, 1, 0);
            camera.move();
            player.move(terrain);
            renderer.processEntity(player); 
            
            renderer.processTerrain(terrain);
            
            for(Entity e : entities) {
                renderer.processEntity(e);
            }
            
         //   renderer.processEntity(entity);
            
            renderer.render(light, camera);
            guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }
        
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
    
}
