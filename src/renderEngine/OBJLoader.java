package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class OBJLoader {
    
    public static RawModel loadObjModel(String fileName, Loader loader) {
        FileReader fr;
        String line;
        int vertexPointer;
        int i;
        String[] currentLine;
        String[] vertex1;
        String[] vertex2;
        String[] vertex3;
        float[] verticesArray;
        float[] normalsArray;
        float[] textureArray;
        int[] indicesArray;
        BufferedReader reader;
        List<Vector3f> vertices;
        List<Vector3f> normals;
        List<Vector2f> textures;
        List<Integer> indices;
        Vector3f vertex;
        Vector2f texture;
        Vector3f normal;
        
        fr = null;
        textureArray = null;
        normalsArray = null;
        
        try {
            fr = new FileReader(new File("res/"+fileName+".obj"));
        } catch (FileNotFoundException ex) {
            System.err.println("Couldn't load file!");
            ex.printStackTrace();
        }
        
        reader = new BufferedReader(fr);
        vertices = new ArrayList<>();
        textures = new ArrayList<>();
        normals = new ArrayList<>();
        indices = new ArrayList<>();
             
        try {
            while(true) {
                line = reader.readLine();
                currentLine = line.split(" ");
                if(line.startsWith("v ")){
                    
                    vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                    Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                    
                } else if(line.startsWith("vt ")) {
                    
                    texture = new Vector2f(Float.parseFloat(currentLine[1]), 
                            Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                    
                } else if(line.startsWith("vn ")) {
                    
                    normal = new Vector3f(Float.parseFloat(currentLine[1]), 
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                    
                } else if(line.startsWith("f ")) {
                    
                    textureArray = new float[vertices.size()*2];
                    normalsArray = new float[vertices.size()*3];
                    break;
                }
            }
            
            while(line != null) {
                if(!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                
                currentLine = line.split(" ");
                vertex1 = currentLine[1].split("/");
                vertex2 = currentLine[2].split("/");
                vertex3 = currentLine[3].split("/");
                
                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);            
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
                
                line = reader.readLine();
            }
            reader.close();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        verticesArray = new float[vertices.size()*3];
        indicesArray = new int[indices.size()];
        
        vertexPointer = 0;
        for(Vector3f v: vertices) {
            verticesArray[vertexPointer++] = v.x;
            verticesArray[vertexPointer++] = v.y;
            verticesArray[vertexPointer++] = v.z;
        }
        
        for(i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        
        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
    }
    
    private static void processVertex(String[] vertexData, List<Integer> indices, 
            List<Vector2f> textures, List<Vector3f> normals, 
            float[] textureArray, float[] normalsArray){
        int currentVertexPointer;
        Vector2f currentTex;
        Vector3f currentNorm;
        
        currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        
        currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer*2] = currentTex.x;
        textureArray[currentVertexPointer*2 + 1] = 1 - currentTex.y;
        
        currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
        
    }
}
