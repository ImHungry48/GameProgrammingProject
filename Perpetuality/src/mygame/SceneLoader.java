/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author alaisha
 */
public class SceneLoader {

    private AssetManager assetManager;
    private Node rootNode = null;
    private Spatial currentScene;
    private GameManager gameManager;
    private Cube cube1;
    private Cube cube2;
    private Cube cube3;
    private Cube door;
    
    private Cube potion1;
    private Cube potion2;
    private Cube potion3;
    
    private Robot robot;
    

    public SceneLoader(AssetManager assetManager, Node rootNode, GameManager gameManager) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.gameManager = gameManager;
        Vector3f vector1 = new Vector3f(5.2345686f, 1.25f, 0.8517339f);
        cube1 = new Cube("Pages", gameManager.getAssetManager(), vector1, this.gameManager);
        Vector3f vector2 = new Vector3f(-3.2234416f, 2.0797584f, 30.968933f);
        cube2 = new Cube("Pages", gameManager.getAssetManager(), vector2, this.gameManager);
        cube3 = new Cube("Pages", gameManager.getAssetManager(), vector1, this.gameManager);
        Vector3f potion1Vector = new Vector3f(0.13707986f, 1.0693324f, -0.15020779f);
        potion1 = new Cube("Consumable", gameManager.getAssetManager(), potion1Vector, this.gameManager);
        Vector3f potion2Vector = new Vector3f(-3.60243f, 1.0285867f, 0.9824556f);
        potion2 = new Cube("Consumable", gameManager.getAssetManager(), potion2Vector, this.gameManager);

    }

    // Method to load a scene by path
    public void loadScene(String scenePath, Runnable postLoadAction) {
        // Detach current scene if there's one already loaded
        if (currentScene != null) {
            rootNode.detachChild(currentScene);
        }

        // Load the new scene
        currentScene = assetManager.loadModel(scenePath);

        // Attach it to the root node
        rootNode.attachChild(currentScene);

        // Run post-load action
        if (postLoadAction != null) {
            postLoadAction.run();
        }
        
        System.out.println(scenePath);
        
        // Display cube based on scene is up
        if ("Scenes/ClassroomA1.j3o".equals(scenePath)) {
            rootNode.attachChild(cube1.getNode());
            gameManager.getRobot().moveTo(new Vector3f(-14.9359527f, 1.1378932f, -4.020808674f));
            
        }
        else if ("Scenes/ClassroomA2.j3o".equals(scenePath)) {
            gameManager.getRobot().moveTo(new Vector3f(-10.9359527f, 1.1378932f, -4.020808674f));
        }
        else if ("Scenes/ClassroomA3.j3o".equals(scenePath)) {
            gameManager.getRobot().moveTo(new Vector3f(-8.9359527f, 1.1378932f, -4.020808674f));
            rootNode.attachChild(cube3.getNode());
        }
        // Display cube based on scene is up
        else if ("Scenes/Hallway.j3o".equals(scenePath)) {
            rootNode.detachChild(cube1.getNode());
            rootNode.detachChild(cube3.getNode());
            rootNode.attachChild(cube2.getNode());
            rootNode.attachChild(gameManager.getRobot().getPlayer());
            rootNode.attachChild(potion2.getNode());
            gameManager.getRobot().moveTo(new Vector3f(-11.535556f, 0f, -11.38224f));
        } 
        else if ("Scenes/Bathroom.j3o".equals(scenePath)) {
//            rootNode.attachChild(potion1.getNode());
            rootNode.detachChild(gameManager.getRobot().getPlayer());
        }
        
    }

    // Method to unload the current scene
    public void unloadScene() {
        if (currentScene != null) {
            rootNode.detachChild(currentScene);
            currentScene = null;
        }
    }

    // Add this method to expose the rootNode
    public Node getRootNode() {
        return rootNode;
    }
    
    public Spatial getCurrentScene() {
        return this.currentScene;
    }
}
