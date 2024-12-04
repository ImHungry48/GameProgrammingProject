/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author alais
 */
public class ClassroomA3Scene extends AbstractAppState {
    
    private SceneLoader sceneLoader;
    private DialogBoxUI dialogBoxUI;
    private Player player;
    private SceneManager sceneManager;
    private GameState gameState;
    private GameManager gameManager;
    
    public ClassroomA3Scene(GameManager gameManager) {
        this.sceneLoader = gameManager.getSceneLoader();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.player = gameManager.getPlayer();
        this.sceneManager = gameManager.getSceneManager();
        this.gameState = gameManager.getGameState();
        this.gameManager = gameManager;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        System.out.println("Initializing classroom A1");
        super.initialize(stateManager, app);
        
        if (gameState == null) {
            System.err.println("GameState is null! Using default values.");
        }
        
        player.getPlayerNode().setLocalTranslation(
            gameState.getPlayerPosition() != null ? gameState.getPlayerPosition() : new Vector3f(0, 0, 0)
        );
        player.getYawNode().setLocalRotation(
            gameState.getPlayerOrientation() != null ? gameState.getPlayerOrientation() : new Quaternion()
        );
        
    }
    
    private void loadScene() {
        sceneLoader.loadScene("Scenes/ClassroomA3Scene.j3o", this::setupScene);
    }
    
    private void setupScene() {
    }
    
    @Override
    public void update(float tpf) {
    }
    
    private boolean isNearExitPoint(Vector3f playerPosition) {
        
        // Create the exit point
        Vector3f exitPoint = new Vector3f(0.0f, 0.0f, 0.0f);
        
        // Check if the player is close to an exit point
        BoundingBox exitBox = new BoundingBox(exitPoint, 1f, 2f, 1f);
        return exitBox.contains(playerPosition);
    }
    
}