/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;

/**
 *
 * @author alais
 */
public class SceneManager {
    private SimpleApplication app;
    private AppStateManager stateManager;
    private Node rootNode;

    public SceneManager(SimpleApplication app, AppStateManager stateManager, Node rootNode) {
        this.app = app;
        this.stateManager = stateManager;
        this.rootNode = rootNode;
    }

    public void switchScene(AbstractAppState currentScene, AbstractAppState nextScene) {
        // Detach the current scene
        stateManager.detach(currentScene);
        
        // Clean up the rootNode
        rootNode.detachAllChildren();
        
        // Attach the next scene
        stateManager.attach(nextScene);
    }
}
