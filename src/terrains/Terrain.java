package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain {
    
    private static final float SIZE = 800;
    private static final float MAX_HEIGHT = 0;
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;
    
    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;
    
    private float[][] heights;
    
    public Terrain(int gridX, int gridZ, Loader loader, 
            TerrainTexturePack texturePack, TerrainTexture blendMap,
            String heightMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader, heightMap);
    }
    
    public float getHeightOfTerrain(float worldX, float worldZ){
    	float terrainX, terrainZ, gridSquareSize;
    	float xCoord, zCoord, answer;
    	int gridX, gridZ;
    	
    	terrainX = worldX - x;
    	terrainZ = worldZ - z;
    	gridSquareSize = SIZE / ((float) heights.length - 1);
    	gridX = (int) Math.floor(terrainX / gridSquareSize);
    	gridZ = (int) Math.floor(terrainZ / gridSquareSize);
    	
    	if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
    		return 0;
    	}
    	
    	xCoord = (terrainX % gridSquareSize)/gridSquareSize;
    	zCoord = (terrainZ % gridSquareSize)/gridSquareSize;
    	
		if (xCoord <= (1-zCoord)) {
			answer = Maths
					.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths
					.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
    	
		return answer;
    }
       
    private RawModel generateTerrain(Loader loader, String heightMap){
    	BufferedImage image;
    	int VERTEX_COUNT;
        int count, i, j, pointer, gz, gx;
        int topLeft, topRight, bottomLeft, bottomRight;
        float height;
        float[] vertices;
        float[] normals;
        float[] textureCoords; 
        int[] indices;
        int vertexPointer;
        Vector3f normal;
        
        image = null;
        try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        VERTEX_COUNT = image.getHeight();
        
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        
        count = VERTEX_COUNT * VERTEX_COUNT;
        vertices = new float[count * 3];
        normals = new float[count * 3];
        textureCoords = new float[count*2];
        indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        
        vertexPointer = 0;        
        for(i = 0; i < VERTEX_COUNT; i++){
            for(j = 0; j < VERTEX_COUNT; j++){
                
            	vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
            	height = getHeight(j, i, image);
            	heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                
                normal = calculateNormal(j, i, image);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        
        pointer = 0;
        for(gz = 0; gz < VERTEX_COUNT - 1; gz++){
            for(gx = 0; gx < VERTEX_COUNT - 1; gx++){
                topLeft = (gz*VERTEX_COUNT)+gx;
                topRight = topLeft + 1;
                bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                bottomRight = bottomLeft + 1;

                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }
    
    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
    	float heightL;
    	float heightR;
    	float heightD;
    	float heightU;
    	Vector3f normal;
    	
    	heightL = getHeight(x - 1, z, image);
    	heightR = getHeight(x + 1, z, image);
    	heightD = getHeight(x, z - 1, image);
    	heightU = getHeight(x, z + 1, image);
    	
    	normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
    	normal.normalise();
    	
    	return normal;
    }
    
    private float getHeight(int x, int y, BufferedImage image) {
    	float height;
    	
    	if(x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight()) {
    		return 0;
    	}
    	
    	height = image.getRGB(x, y);
    	height += MAX_PIXEL_COLOUR/2f;
    	height /= MAX_PIXEL_COLOUR/2f;
    	height *= MAX_HEIGHT;
    	
    	return height;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

}
