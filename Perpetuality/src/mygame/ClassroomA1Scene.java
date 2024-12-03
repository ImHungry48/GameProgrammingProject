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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author alais
 */
public class ClassroomA1Scene extends AbstractAppState {
    
    private SceneLoader sceneLoader;
    private DialogBoxUI dialogBoxUI;
    private Player player;
    private SceneManager sceneManager;
    private GameState gameState;
    private GameManager gameManager;
    private GameInputManager gameInputManager;
    private Node rootNode;
    
    private final Vector3f EXIT_POINT_HALLWAY = new Vector3f(-3.3919864f, 0.8689593f, -3.9807236f);
    
    private Page page;
    
    public ClassroomA1Scene(GameManager gameManager) {
        this.gameManager = gameManager;
        this.sceneLoader = gameManager.getSceneLoader();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.player = gameManager.getPlayer();
        this.gameState = gameManager.getGameState();
        this.gameInputManager = gameManager.getGameInputManager();
        this.sceneManager = gameManager.getSceneManager();
        this.rootNode = gameManager.getRootNode();
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
            gameState.getPlayerPosition() != null ? gameState.getPlayerPosition() : new Vector3f(0, 1, 0)
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
        sceneLoader.loadScene("Scenes/ClassroomA1.j3o", this::setupScene);
    }
    
    private void setupScene() {
        Node sceneRoot = this.rootNode;
        Spatial pageSpatial = sceneRoot.getChild("ExamPage");
        
        if (pageSpatial != null) {
            System.out.println("Found page model: " + pageSpatial.getName());
        
            // Print the bounds of the page spatial
            System.out.println("Bounds before update: " + pageSpatial.getWorldBound());

            // Force update the spatial's bounds
            pageSpatial.updateModelBound();
            pageSpatial.updateGeometricState();

            // Check the updated bounds
            System.out.println("Bounds after update: " + pageSpatial.getWorldBound());

            // If bounds are still null, ensure the spatial has a mesh
            if (pageSpatial.getWorldBound() == null) {
                System.err.println("World bounds are still null after update. Check if ExamPage has a valid mesh.");
            }

            page = new Page(pageSpatial, gameState.getInventory());

        } else {
            System.err.println("Page model 'ExamPage' not found in the scene!");
        }
    }
    
    @Override
    public void update(float tpf) {
    }
    
}
