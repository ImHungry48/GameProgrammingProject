/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.asset.AssetManager;
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

    public SceneLoader(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
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
