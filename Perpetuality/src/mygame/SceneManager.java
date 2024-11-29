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

    public void switchScene(AbstractAppState newScene) {
        if (currentScene != null) {
            app.getStateManager().detach(currentScene);
        }
        
        app.getStateManager().attach(newScene);
        currentScene = newScene;
    }
    
    public void switchToBathroomScene() {
        BathroomScene bathroomScene = new BathroomScene(
        this.sceneLoader,
        this,
        this.dialogBoxUI,
        this.player,
        this.gameState,
        this.gameManager.getInventory(),
        this.gameManager.getGameInputManager(),
        this.gameManager.getEventSystem());
        
        app.getStateManager().detach(currentScene);
        app.getStateManager().attach(bathroomScene);
        currentScene = bathroomScene;
    }

}
