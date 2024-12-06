package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import mygame.EventManagement.EventSystem;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.math.FastMath;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author abarbe23
 */
public class BathroomScene extends AbstractAppState {
    private SceneManager sceneManager;
    private Player player;
    private GameState gameState;
    private GameInputManager gameInputManager;
    private EventSystem eventSystem;
    private GameManager gameManager;
    
    private SceneLoader sceneLoader;
    private DialogBoxUI dialogBoxUI;
    private boolean flashlightPickedUp = false;
    
    private Node rootNode;
    private BulletAppState bulletAppState;
    private List<RigidBodyControl> environmentColliders = new ArrayList<>();
    
    private final Vector3f SPAWNPOINT = new Vector3f(1.5810981f, 2f, -0.9333563f);
    private final Vector3f EXIT_POINT_HALLWAY = new Vector3f(2.3731039f, 0.2084856f, 0.040968657f);

    public BathroomScene(GameManager gameManager) {
        this.gameManager = gameManager;
        this.sceneLoader = gameManager.getSceneLoader();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.player = gameManager.getPlayer();
        this.gameState = gameManager.getGameState();
        this.gameInputManager = gameManager.getGameInputManager();
        this.eventSystem = gameManager.getEventSystem(); 
        this.sceneManager = gameManager.getSceneManager();
        this.rootNode = gameManager.getRootNode();
        this.bulletAppState = gameManager.getBulletAppState();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        gameInputManager.enable();
        eventSystem.startListening();

        // Load the scene
        this.loadScene();

        createEnvironmentColliders();

        // Set the player's spawn point
        setPlayerSpawnPoint();

        // Set up the exit trigger using GameInputManager
        gameInputManager.setupExitTrigger(new ExitTrigger(
            EXIT_POINT_HALLWAY,
            () -> {
                sceneManager.switchScene("Hallway");
                dialogBoxUI.hideDialog();
            },
            () -> {
            }
        ));

        eventSystem.startListening();
        setupScene();
    }

    public void loadScene() {
        sceneLoader.loadScene("Scenes/Bathroom.j3o", this::setupScene);
    }

    private void setupScene() {
        this.dialogBoxUI.hideDialog();
        
        // Display initial dialogue
        dialogBoxUI.showDialog("Did I get sick again?", 1.0f, false);
    }

    @Override
    public void update(float tpf) {
        if (!flashlightPickedUp) {
            checkFlashlightInteraction();
        }
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

    private void checkFlashlightInteraction() {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(sceneLoader.getRootNode().getLocalTranslation(), Vector3f.UNIT_Z);
        sceneLoader.getRootNode().collideWith(ray, results);

        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();
            if ("Flashlight".equals(target.getName())) {
                dialogBoxUI.showDialog("You picked up the flashlight.", 1.0f, false);
                flashlightPickedUp = true;
                sceneLoader.getRootNode().detachChild(target);
            }
        }
    }

    public void createEnvironmentColliders() {
        // Stall Collider
        createEnvironmentCollider(
            new Vector3f(-0.04477644f, 0.64549947f, -1.2924877f),
            new Vector3f(1.8017983f, 1.1318405f, 0.7680437f),
            new Vector3f(0f, 0f, 0f)
        );

        // Sink Collider
        createEnvironmentCollider(
            new Vector3f(0.052668333f, 0.7697344f, 0.79848015f),
            new Vector3f(1.576805f, 0.70603347f, 0.4376222f),
            new Vector3f(0f, 0f, 0f)
        );

        // Wall
        createEnvironmentCollider(
            new Vector3f(0.7037773f, 0.6948494f, 1.0015589f),
            new Vector3f(3.6600187f, 1.5327122f, 0.081855536f),
            new Vector3f(0f, 0f, 0f)
        );

        // Wall
        createEnvironmentCollider(
            new Vector3f(0.7446282f, 0.6948494f, -1.8535419f),
            new Vector3f(3.6600187f, 1.5327122f, 0.081855536f),
            new Vector3f(0f, 0f, 0f)
        );

        // Wall
        createEnvironmentCollider(
            new Vector3f(-1.0330709f, 0.75781786f, -0.2756611f),
            new Vector3f(2.4091172f, 1.5327122f, 0.081855536f),
            new Vector3f(0f, 90f, 0f)
        );

        // Wall
        createEnvironmentCollider(
            new Vector3f(2.4014149f, 0.75781786f, -0.47148216f),
            new Vector3f(2.7994726f, 1.5327122f, 0.081855536f),
            new Vector3f(0f, 90f, 0f)
        );

        // Floor
        createEnvironmentCollider(
            new Vector3f(0.8183949f, -0.48795676f, -0.40012658f),
            new Vector3f(3.9599795f, 1f, 2.607613f),
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
