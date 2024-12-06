/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
/* ---------New For Particle---------- */
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import java.util.ArrayList;
import java.util.List;
//import com.jme3.post.filters.FogFilter;
/* ------------------- */

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
    private AssetManager assetManager;
    private Cube cube;
    private BulletAppState bulletAppState;
    private List<RigidBodyControl> environmentColliders = new ArrayList<>();
    private static final Vector3f SPAWNPOINT = new Vector3f(5.267465f, 1.0222735f, 4.0206456f);
    
    // NEW FIELD FOR PARTICLE
//    private ParticleEmitter dustEmitter;
    private float angle = 0;
    
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
        this.assetManager = gameManager.getAssetManager();
        this.bulletAppState = gameManager.getBulletAppState();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        System.out.println("Initializing bathroom");

        gameInputManager.enable();

        // Load the scene
        this.loadScene();

        createEnvironmentColliders();

        // Set the player's spawn point
        setPlayerSpawnPoint();

        // Set up the exit trigger using GameInputManager
        gameInputManager.setupExitTrigger(new ExitTrigger(
            EXIT_POINT_HALLWAY,
            () -> {
                System.out.println("Entering Hallway...");
                sceneManager.switchScene("Hallway");
                if (this.bulletAppState == null) {
                    System.out.println("[BathroomScene] Bullet app state is null.");
                }
                dialogBoxUI.hideDialog();
            },
            () -> {
                System.out.println("Not near Hallway exit point.");
            }
        ));

        setupScene();
        
        bulletAppState.setDebugEnabled(true);
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

//            page = new Page(pageSpatial);

        } else {
            System.err.println("Page model 'ExamPage' not found in the scene!");
        }
        
        setUpLighting();
        
        // NEW FOR PARTICLE
        // Descriptive name for emitter, and keep 20 particles of type triangle ready
//        dustEmitter = new ParticleEmitter("dust emitter", Type.Triangle, 20);
//        
//        // Set material
//        Material dustMat = new Material(gameManager.getAssetManager(),"Common/MatDefs/Misc/Particle.j3md");
//        dustEmitter.setMaterial(dustMat);
//        
//        // Load smoke.png into the Texture property of the material
//        dustMat.setTexture("Texture",gameManager.getAssetManager().loadTexture("Effects/smoke.png"));
//        
//        // Help with segmenting the image for smoke.png
//        dustEmitter.setImagesX(2);
//        dustEmitter.setImagesY(2);
//        
//        // Make dust cloud more swirly and random
//        dustEmitter.setSelectRandomImage(true);
//        dustEmitter.setRandomAngle(true);
//        dustEmitter.getParticleInfluencer().setVelocityVariation(1f); // 1f means emits particle in all directions 360 degrees
//        
//        // Attach emitter to a node
//        rootNode.attachChild(dustEmitter);
//        
//        // Can control various features of the dust
//        dustEmitter.setStartSize(1);
//        dustEmitter.setEndSize(3);
//        dustEmitter.setStartColor(ColorRGBA.LightGray);
//        dustEmitter.setEndColor(ColorRGBA.Yellow);

    }
    
    private void setUpLighting() {
        PointLight pointlight = new PointLight();
        pointlight.setColor(ColorRGBA.DarkGray);
        pointlight.setPosition(new Vector3f(0f, 2f, 0f));
    }
    
    @Override
    public void update(float tpf) {
        // make the emitter fly in horizontal circles
//        float player_x = gameState.getPlayerPosition().x;
//        float player_y = gameState.getPlayerPosition().y;
//        float player_z = gameState.getPlayerPosition().z;
        
        angle += tpf;
        angle %= FastMath.TWO_PI;
        // radius is currently 3
        float x = FastMath.cos(angle) * 3;
        float y = FastMath.sin(angle) * 3;
//        dustEmitter.setLocalTranslation(0, 1, 0);
        
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
    
    public void createEnvironmentColliders() {
        // DeskArea
        createEnvironmentCollider(
            new Vector3f(2.0988624f, 1.3285756f, 0.77927125f),
            new Vector3f(1.0f, 3.439918f, 9.865414f),
            new Vector3f(0f, 0f, 0f)
        );

        // WallWithDoor
        createEnvironmentCollider(
            new Vector3f(4.5885897f, 1.1795019f, 5.3769536f),
            new Vector3f(3.7648754f, 2.7353182f, 1.0f),
            new Vector3f(0f, 0f, 0f)
        );

        // Floor
        createEnvironmentCollider(
            new Vector3f(4.164594f, -0.47742724f, 0.7498214f),
            new Vector3f(4.61777f, 1.0f, 9.149669f),
            new Vector3f(0f, 0f, 0f)
        );

        // WallWithChalkboard
        createEnvironmentCollider(
            new Vector3f(6.9043407f, 1.3285756f, 0.77927125f),
            new Vector3f(1.0f, 3.439918f, 9.865414f),
            new Vector3f(0f, 0f, 0f)
        );

        // Teacher's Desk
        createEnvironmentCollider(
            new Vector3f(5.2345686f, 0.5697317f, 0.8517339f),
            new Vector3f(0.68210435f, 0.8068805f, 1.0977038f),
            new Vector3f(0f, 0f, 0f)
        );

        // Desk1
        createEnvironmentCollider(
            new Vector3f(3.2204885f, 0.43792105f, 0.8517339f),
            new Vector3f(1.0f, 0.68156374f, 0.9222435f),
            new Vector3f(0f, 0f, 0f)
        );

        // Desk2
        createEnvironmentCollider(
            new Vector3f(3.2204885f, 0.43792105f, 2.4195294f),
            new Vector3f(0.92100644f, 0.68156374f, 0.76498115f),
            new Vector3f(0f, 0f, 0f)
        );

        // Desk3
        createEnvironmentCollider(
            new Vector3f(3.2204885f, 0.43792105f, 4.0206456f),
            new Vector3f(1.0f, 0.68156374f, 0.9222435f),
            new Vector3f(0f, 0f, 0f)
        );

        // Desk4
        createEnvironmentCollider(
            new Vector3f(3.2204885f, 0.43792105f, -0.6676743f),
            new Vector3f(0.9114051f, 0.68156374f, 0.7387882f),
            new Vector3f(0f, 0f, 0f)
        );

        // Desk5
        createEnvironmentCollider(
            new Vector3f(3.2375674f, 0.43792105f, -2.3155034f),
            new Vector3f(0.86851525f, 0.68156374f, 0.8293736f),
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
