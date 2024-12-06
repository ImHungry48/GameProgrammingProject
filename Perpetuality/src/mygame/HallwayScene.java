/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.List;
//import com.jme3.post.filters.FogFilter;

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
    private final GameState gameState;
    private BulletAppState bulletAppState;
    private List<RigidBodyControl> environmentColliders = new ArrayList<>();
    
    private static final Vector3f SPAWNPOINT = new Vector3f(-2.9842114f, 0.0f, -11.511498f);
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
        this.gameState = gameManager.getGameState();
        this.bulletAppState = gameManager.getBulletAppState();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        if (this.bulletAppState == null) {
            System.out.println("[HallwayScene] this.bulletAppState is null");
        }
        this.loadScene();
        this.createEnvironmentColliders();
        this.setPlayerSpawnPoint();
        this.setupScene();
        setupExitTriggers();
        System.out.println("Initializing");
    }
    
    public void loadScene() {
        sceneLoader.loadScene("Scenes/Hallway.j3o", this::setupScene);
    }
    
    private void setupScene() {
        this.createSkybox();
        
        // Fog Filter Setup
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
//        FogFilter fogFilter = new FogFilter();
//
//        // Customize fog settings
//        fogFilter.setFogDistance(50f); // Adjust based on hallway dimensions
//        fogFilter.setFogDensity(0.5f); // Adjust for desired effect
//        fogFilter.setFogColor(ColorRGBA.Gray); // Match fog color to scene ambiance
//
//        fpp.addFilter(fogFilter);
//        app.getViewPort().addProcessor(fpp);
    }
    
    private void createSkybox() {
        // Load the textures for the skybox
        Texture west = app.getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_west.jpeg");
        Texture east = app.getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_east.jpeg");
        Texture north = app.getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_north.jpeg");
        Texture south = app.getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_south.jpeg");
        Texture up = app.getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_up.jpeg");
        Texture down = app.getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_down.jpeg");

        // Use SkyFactory to create a sky
        Spatial sky = SkyFactory.createSky(
            app.getAssetManager(),
            west, east, north, south, up, down
        );

        // Attach the sky to the root node
        rootNode.attachChild(sky);
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
    
    private void setPlayerSpawnPoint() {
        if (gameState == null || gameState.getPlayerPosition() == null) {
            // Set to the bathroom spawn point
            player.getCharacterControl().warp(SPAWNPOINT);

            // Optionally, set the player's view direction
            player.getCharacterControl().setViewDirection(new Vector3f(0, 0, 1)); // Adjust as needed
        } else {
            // Set to the saved game state position
            player.getCharacterControl().warp(gameState.getPlayerPosition());

            // Restore the player's orientation if saved
            if (gameState.getPlayerOrientation() != null) {
                player.getYawNode().setLocalRotation(gameState.getPlayerOrientation());
            }
        }
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
                    sceneManager.switchScene("ClassroomA3");
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
            case "Bathroom": exitPoint = this.EXIT_POINT_BATHROOM;
            case "ClassroomA1": exitPoint = this.EXIT_POINT_CLASSROOM_A1;
            case "ClassroomA2": exitPoint = this.EXIT_POINT_CLASSROOM_A2;
            case "ClassroomA3": exitPoint = this.EXIT_POINT_CLASSROOM_A3;
        }

        // Check if the player is close to an exit point
        BoundingBox exitBox = new BoundingBox(exitPoint, 1f, 2f, 1f);
        return exitBox.contains(playerPosition);
    }
    
    public void createEnvironmentColliders() {
        // Floor
        createEnvironmentCollider(
            new Vector3f(-1.1832775f, -0.15856981f, 18.57429f),
            new Vector3f(27.93188f, 0.19212198f, 62.875908f),
            new Vector3f(0f, 0f, 0f)
        );

        // WallWithDoors
        createEnvironmentCollider(
            new Vector3f(-1.980099f, 1.1526537f, 17.165985f),
            new Vector3f(0.18981946f, 3.311129f, 60.54934f),
            new Vector3f(0f, 0f, 0f)
        );

        // WallWithWindows
        createEnvironmentCollider(
            new Vector3f(-4.1734133f, 1.1526537f, 15.579739f),
            new Vector3f(0.18981946f, 3.311129f, 51.392628f),
            new Vector3f(0f, 0f, 0f)
        );

        // LeftHallEnding
        createEnvironmentCollider(
            new Vector3f(-8.079243f, 1.4753025f, -13.299509f),
            new Vector3f(15.226651f, 3.2489543f, 1f),
            new Vector3f(0f, 0f, 0f)
        );

        // RightHallEnding
        createEnvironmentCollider(
            new Vector3f(-8.079243f, 1.4753025f, -13.299509f),
            new Vector3f(15.226651f, 3.2489543f, 1f),
            new Vector3f(0f, 0f, 0f)
        );

        // LeftHallOutwardEnding
        createEnvironmentCollider(
            new Vector3f(-15.74729f, 1.525302f, -11.511498f),
            new Vector3f(1.4911032f, 3.2167864f, 2.627026f),
            new Vector3f(0f, 0f, 0f)
        );

        // RightHallOutwardEnding
        createEnvironmentCollider(
            new Vector3f(-15.74729f, 1.525302f, 44.236794f),
            new Vector3f(1.4911032f, 3.2167864f, 4.8471875f),
            new Vector3f(0f, 0f, 0f)
        );

        // LeftInnerWindow
        createEnvironmentCollider(
            new Vector3f(-10.587201f, 1.4753025f, -9.687234f),
            new Vector3f(11.518579f, 3.2489543f, 1f),
            new Vector3f(0f, 0f, 0f)
        );

        // RightInnerWindow
        createEnvironmentCollider(
            new Vector3f(-9.774382f, 1.4753025f, 41.339714f),
            new Vector3f(11.518579f, 3.2489543f, 1f),
            new Vector3f(0f, 0f, 0f)
        );

    }
    
    private void createEnvironmentCollider(Vector3f translation, Vector3f scale, Vector3f rotationDegrees) {
        // Create a box collision shape with half extents (scale * 0.5f)
        Vector3f halfExtents = scale.mult(0.5f);
        CollisionShape boxShape = new BoxCollisionShape(halfExtents);

        // Create a rigid body control with mass 0 (static object)
        RigidBodyControl rigidBodyControl = new RigidBodyControl(boxShape, 0);

        // Set the physics location and rotation
        rigidBodyControl.setPhysicsLocation(translation);

        Quaternion rotation = new Quaternion();
        rotation.fromAngles(
            FastMath.DEG_TO_RAD * rotationDegrees.x,
            FastMath.DEG_TO_RAD * rotationDegrees.y,
            FastMath.DEG_TO_RAD * rotationDegrees.z
        );
        rigidBodyControl.setPhysicsRotation(rotation);

        // Add the control to the physics space
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        
        environmentColliders.add(rigidBodyControl);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        
        for (RigidBodyControl collider : environmentColliders) {
            bulletAppState.getPhysicsSpace().remove(collider);
        }
        environmentColliders.clear();
        
        gameInputManager.clearExitTriggers();
    }
}
