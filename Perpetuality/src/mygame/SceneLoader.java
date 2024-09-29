/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

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
    public void loadScene(String scenePath) {
        // Detach current scene if there's one already loaded
        if (currentScene != null) {
            rootNode.detachChild(currentScene);
        }

        // Load the new scene
        currentScene = assetManager.loadModel(scenePath);

        // Attach it to the root node
        rootNode.attachChild(currentScene);
    }

    // Method to unload the current scene
    public void unloadScene() {
        if (currentScene != null) {
            rootNode.detachChild(currentScene);
            currentScene = null;
        }
    }

    // Optional: Method to transition between scenes smoothly
    public void transitionToScene(String newScenePath) {
        // Fade-outs, loading screens, or background loading here
        unloadScene();
        loadScene(newScenePath);
    }
}
