/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import java.util.HashMap;

/**
 *
 * @author alais
 */
public class SceneManager {

    private final HashMap<String, AbstractAppState> scenes = new HashMap<>();
    
    private SimpleApplication app;
    private AppStateManager stateManager;
    private Node rootNode;
    private AbstractAppState currentScene;
    private GameManager gameManager;
    private Player player;
    private DialogBoxUI dialogBoxUI;
    private GameState gameState;
    private SceneLoader sceneLoader;

    public SceneManager(GameManager gameManager) {
        this.app = gameManager;
        this.stateManager = gameManager.getStateManager();
        this.rootNode = gameManager.getRootNode();
        this.player = gameManager.getPlayer();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.sceneLoader = gameManager.getSceneLoader();
        this.gameManager = gameManager;
    }
    
    public void addScene(String sceneName, AbstractAppState scene) {
        scenes.put(sceneName, scene);
    }

    public void switchScene(String sceneName) {
        if (currentScene != null) {
            app.getStateManager().detach(currentScene);
        }
        
        AbstractAppState newScene = scenes.get(sceneName);
        
        if (newScene != null) {
            this.currentScene = newScene;
            stateManager.attach(newScene);
        } else {
            System.err.println("Scene: " + sceneName + " not found!");
        }
        
    }
    
    public Node getRootNode() {
        return this.rootNode;
    }
    
}
