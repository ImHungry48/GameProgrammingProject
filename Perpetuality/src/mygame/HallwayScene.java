/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author alais
 */
public class HallwayScene extends AbstractAppState {

    private final GameManager app;
    private final AppStateManager stateManager;
    private final Node rootNode;
    private final Player player;
    private final DialogBoxUI dialogBoxUI;
    private final SceneLoader sceneLoader;
    private final SceneManager sceneManager;
    private final GameInputManager gameInputManager;
    
    private static final Vector3f EXIT_POINT_CLASSROOM_A1 = new Vector3f(-2.7319417f, 0.0f, 9.236806f);
    private static final Vector3f EXIT_POINT_CLASSROOM_A2 = new Vector3f(-2.5790946f, 0.0f, 19.545742f);
    private static final Vector3f EXIT_POINT_CLASSROOM_A3 = new Vector3f(-2.639412f, 0.0f, 29.787582f);
    private static final Vector3f EXIT_POINT_BATHROOM = new Vector3f(-3.1274738f, 0.0f, -9.922217f);

    
    public HallwayScene(GameManager gameManager) {
        this.app = gameManager;
        this.stateManager = gameManager.getStateManager();
        this.rootNode = gameManager.getRootNode();
        this.player = gameManager.getPlayer();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.sceneLoader = gameManager.getSceneLoader();
        this.sceneManager = gameManager.getSceneManager();
        this.gameInputManager = gameManager.getGameInputManager();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.loadScene();
        setupExitTriggers();
        System.out.println("Initializing");
    }
    
    public void loadScene() {
        sceneLoader.loadScene("Scenes/Hallway.j3o", this::setupScene);
    }
    
    private void setupScene() {
        
    }
    
    private void setupExitTriggers() {
        // Exit to Classroom A1
        gameInputManager.setupExitTrigger(new ExitTrigger(
            EXIT_POINT_CLASSROOM_A1,
            () -> {
                System.out.println("Entering Classroom A1...");
                sceneManager.switchScene("ClassroomA1");
            },
            () -> {
                System.out.println("Not near Classroom A1 exit point.");
            }
        ));

        // Exit to Classroom A2
        gameInputManager.setupExitTrigger(new ExitTrigger(
            EXIT_POINT_CLASSROOM_A2,
            () -> {
                System.out.println("Entering Classroom A2...");
                sceneManager.switchScene("ClassroomA2");
            },
            () -> {
                System.out.println("Not near Classroom A2 exit point.");
            }
        ));

        // Exit to Classroom A3
        gameInputManager.setupExitTrigger(new ExitTrigger(
            EXIT_POINT_CLASSROOM_A3,
            () -> {
                System.out.println("Entering Classroom A3...");
                sceneManager.switchScene("ClassroomA3");
            },
            () -> {
                System.out.println("Not near Classroom A3 exit point.");
            }
        ));

        // Exit back to Bathroom (if applicable)
        gameInputManager.setupExitTrigger(new ExitTrigger(
            EXIT_POINT_BATHROOM,
            () -> {
                System.out.println("Entering Bathroom...");
                sceneManager.switchScene("Bathroom");
            },
            () -> {
                System.out.println("Not near Bathroom exit point.");
            }
        ));
    }
   
    
    @Override
    public void update(float tpf) {
    }
    
        private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("EnterBathroom") && isPressed) {
                if (isNearExitPoint(player.getPlayerNode().getWorldTranslation(), "Bathroom")) {
                    System.out.println("Exiting room...");
                    sceneManager.switchScene("Hallway");
                } else {
                    System.out.println("Not near an exit point.");
                }
            }
            
            if (name.equals("EnterClassroomA1") && isPressed) {
                if (isNearExitPoint(player.getPlayerNode().getWorldTranslation(), "ClassroomA1")) {
                    System.out.println("Exiting room...");
                    sceneManager.switchScene("ClassroomA1");
                } else {
                    System.out.println("Not near an exit point.");
                }
            }
            
            if (name.equals("EnterClassroomA2") && isPressed) {
                if (isNearExitPoint(player.getPlayerNode().getWorldTranslation(), "ClassroomA2")) {
                    System.out.println("Exiting room...");
                    sceneManager.switchScene("ClassroomA2");
                } else {
                    System.out.println("Not near an exit point.");
                }
            }
            
            if (name.equals("EnterClassroomA3") && isPressed) {
                if (isNearExitPoint(player.getPlayerNode().getWorldTranslation(), "ClassroomA2")) {
                    System.out.println("Exiting room...");
                    sceneManager.switchScene("ClassroomA2");
                } else {
                    System.out.println("Not near an exit point.");
                }
            }
        }
    };
    
    private boolean isNearExitPoint(Vector3f playerPosition, String room) {
        // Create a stub exit point
        Vector3f exitPoint = new Vector3f(0, 0, 0);
        
        switch (room) {
            case "Bathroom": exitPoint = new Vector3f(0, 0, 0);
            case "ClassroomA1": exitPoint = new Vector3f(0, 0, 0);
            case "ClassroomA2": exitPoint = new Vector3f(0, 0, 0);
            case "ClassroomA3": exitPoint = new Vector3f(0, 0, 0);
            case "Cafeteria": exitPoint = new Vector3f(0, 0, 0);
        }

        // Check if the player is close to an exit point
        BoundingBox exitBox = new BoundingBox(exitPoint, 1f, 2f, 1f);
        return exitBox.contains(playerPosition);
    }
    
    @Override
    public void cleanup() {
        gameInputManager.clearExitTriggers();
    }
}
