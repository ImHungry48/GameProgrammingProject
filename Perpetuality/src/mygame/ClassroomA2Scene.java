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
public class ClassroomA2Scene extends AbstractAppState {
    
    private SceneLoader sceneLoader;
    private DialogBoxUI dialogBoxUI;
    private Player player;
    private SceneManager sceneManager;
    private GameState gameState;
    private GameManager gameManager;
    private GameInputManager gameInputManager;
    
    private final Vector3f EXIT_POINT_HALLWAY = new Vector3f(-3.3919864f, 0.8689593f, -3.9807236f);
    
    public ClassroomA2Scene(GameManager gameManager) {
        this.gameManager = gameManager;
        this.sceneLoader = gameManager.getSceneLoader();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.player = gameManager.getPlayer();
        this.gameState = gameManager.getGameState();
        this.gameInputManager = gameManager.getGameInputManager();
        this.sceneManager = gameManager.getSceneManager();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        System.out.println("Initializing Classroom A1");
        super.initialize(stateManager, app);
        
        if (gameState == null) {
            System.err.println("GameState is null! Using default values.");
        }
        
        gameInputManager.enable();
        
        player.getPlayerNode().setLocalTranslation(
            gameState.getPlayerPosition() != null ? gameState.getPlayerPosition() : new Vector3f(0, 0, 0)
        );
        player.getYawNode().setLocalRotation(
            gameState.getPlayerOrientation() != null ? gameState.getPlayerOrientation() : new Quaternion()
        );
        
        // Set up the exit trigger using GameInputManager
        gameInputManager.setupExitTrigger(new ExitTrigger(
            EXIT_POINT_HALLWAY,
            () -> {
                System.out.println("Entering Hallway...");
                sceneManager.switchScene("Hallway");
            },
            () -> {
                System.out.println("Not near Hallway exit point.");
            }
        ));
        
        this.loadScene();
        
    }
    
    private void loadScene() {
        sceneLoader.loadScene("Scenes/ClassroomA2.j3o", this::setupScene);
    }
    
    private void setupScene() {
    }
    
    @Override
    public void update(float tpf) {
    }
    
}
