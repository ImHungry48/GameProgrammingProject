// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import mygame.GameInputManager.ActionHandler;
import mygame.GameInputManager.AnalogHandler;

/**
 *
 * @author mike0
 * Game Manager that maps the controls, set the scene up, and populate the game with interactable objects
 */
public class GameManager extends SimpleApplication implements ActionHandler, AnalogHandler, GameInputManager.MovementHandler {    
    // Maintain the game controls
    private GameInputManager gameInputManager;
    // Maintain the state of the game
    private GameState gameState;
    
    // Event System
    private EventSystem eventSystem;
    
    protected Player player;
    
    // For Bathroom Sanity Mechanics
    private Spatial bathroomModel; 
    protected BoundingBox bathroomBounds;
    
    // Mesh potentially
    private static Box mesh = new Box(Vector3f.ZERO, 1, 1, 1);
    
    // Objects that are populated in our game
    private ArrayList<Geometry> good_interact_geoms;
    private ArrayList<Geometry> bad_interact_geoms;
    private Node pitchNode;
    private Node yawNode;
    private final Vector3f respawnPosition = new Vector3f(3.9238453f, 0f, 0f);
    
    // Flashlight
    private FlashLight flashlight;
    
    // Dialog box
    private DialogBox dialogBox;
    
    private AnimateModel animateModel;

    // Field for Game Logic
    @Override
    public void simpleInitApp() {
        
        setDisplayFps(false);

        setDisplayStatView(false);
        
        // Detach FlyCamAppState to prevent it from interfering with cursor visibility
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        
        // Initialize player spatial
        Spatial playerSpatial = assetManager.loadModel("/Models/male_base_mesh/male_base_mesh.j3o");
        
        

        // Initialize the player with its model and attach to root node
        player = new Player(playerSpatial);
        player.getPlayerNode().setLocalTranslation(0,0.5f,0);
        rootNode.attachChild(player.getPlayerNode());

        // Disable default flyCam behavior and make the camera follow the camera node
        flyCam.setEnabled(false);
        
        // Hide the cursor to capture mouse movement
        inputManager.setCursorVisible(false);
        inputManager.setMouseCursor(null); // Hides system cursor

        // Create a yaw node and attach it to the player's node
        yawNode = new Node("YawNode");
        player.getPlayerNode().attachChild(yawNode);
        yawNode.setLocalTranslation(0, this.player.getPlayerHeight() / 2, 0); // Adjust to the height of the player's "eyes"

        // Create a pitch node and attach it to the yaw node
        pitchNode = new Node("PitchNode");
        yawNode.attachChild(pitchNode);
        
        // Create a CameraNode and attach it to the pitchNode
        CameraNode camNode = new CameraNode("CameraNode", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera); // Control the camera based on the spatial's transforms
        pitchNode.attachChild(camNode);
        camNode.setLocalTranslation(0, 0, 0.1f); // TODO: Adjust as needed to align with the eyes

        // Since we don't have a camNode, we need to simulate attaching the camera to the pitchNode
        // While we can't attach 'cam' directly to the scene graph, we'll store the pitchNode for camera synchronization
        this.pitchNode = pitchNode; // Add 'pitchNode' as a field in GameManager
        
        animateModel = new AnimateModel(assetManager, rootNode);
        
        // Set up GameInputManager for camera controls
        gameInputManager = new GameInputManager(inputManager, yawNode, pitchNode, animateModel);
        gameInputManager.setActionHandler(this);
        gameInputManager.setAnalogHandler(this);
        gameInputManager.setMovementHandler(this);
        gameInputManager.initInputMappings(); 

        // Synchronize the camera with the camera node in simpleUpdate
        //cam.setLocation(cameraNode.getWorldTranslation());
        //cam.lookAt(player.getPlayerNode().getWorldTranslation(), Vector3f.UNIT_Y);

        // Create the flash light
        flashlight = new FlashLight(assetManager, inputManager, viewPort, rootNode);
        
        // Initalize dialog box
        dialogBox = new DialogBox(assetManager, inputManager, guiNode, viewPort, rootNode);
        
        Geometry interactableBox = new Geometry("InteractableBox", new Box(0.1f, 0.1f, 0.1f));        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        interactableBox.setMaterial(mat);
        interactableBox.setLocalTranslation(0, 1, -10);
        
        rootNode.attachChild(interactableBox);
        
        // Potential Cache Issue
        good_interact_geoms = new ArrayList<>();
        bad_interact_geoms = new ArrayList<>();
        
        Vector3f loc1 = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50) 
        );
        
        Vector3f loc2 = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50) 
        );
        
        /* SCENE LOADING */
        SceneLoader sceneLoader = new SceneLoader(assetManager, rootNode);
        sceneLoader.loadScene("Scenes/Bathroom.j3o");
  
        // Submision3: Set Background
        viewPort.setBackgroundColor(ColorRGBA.fromRGBA255(21,34,56,1));
        // Submission3: Dark Light for our game
        setUpLight();

        // Load the bathroom model from the scene
        bathroomModel = rootNode.getChild("Bathroom"); 
        if (bathroomModel != null && bathroomModel.getWorldBound() instanceof BoundingBox) {
            this.bathroomBounds = (BoundingBox) bathroomModel.getWorldBound();
            // Log bounding box details for debugging
            System.out.println("Bathroom Center: " + bathroomBounds.getCenter());
            System.out.println("Bathroom Extents: " + bathroomBounds.getXExtent() + ", " +
                    bathroomBounds.getYExtent() + ", " + bathroomBounds.getZExtent());
        }

        // Initialize the player bounds as a bounding box centered on the camera location
        float playerBoxHalfExtent = 1f; // Adjust the size as needed
        this.player.playerBounds = new BoundingBox(cam.getLocation(), playerBoxHalfExtent, playerBoxHalfExtent, playerBoxHalfExtent);

        
        /* EVENT SYSTEM */
        this.eventSystem = new EventSystem(this.player, this.bathroomBounds, this.respawnPosition);
        this.eventSystem.loadEvents();
        
        gameState = new GameState(this);
        stateManager.attach(gameState);
    } 
    
    // Submission 3: NEW
    private void setUpLight() {
        
        PointLight pl = new PointLight(new Vector3f(1.5510116f, 2.1516128f, -8.777508f));
        pl.setColor(ColorRGBA.White.mult(0.3f));
        pl.setRadius(3.0f);
        rootNode.addLight(pl);
        
    }
       

    @Override
    public void simpleUpdate(float tpf) {

        this.player.playerBounds.setCenter(cam.getLocation());
        
        if (bathroomModel != null && this.player.playerBounds != null) {
            if (this.player.playerBounds.intersects((BoundingBox) bathroomModel.getWorldBound())) {
                gameState.increaseHealth(1);
            }
        }
        
        flashlight.update(cam.getLocation(), cam.getDirection());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void handleRotate(float intensity, float tpf) {
        // implement action here
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        rootNode.collideWith(ray, results);
        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();
            // implement action here
            if (target.getName().equals("Good Box")) {
                target.rotate(tpf,tpf,tpf);
                System.out.println("You are selecting the good box");
                // for future work, should showcase description of the target to user
            } else if (target.getName().equals("Bad Box")) {
                target.rotate(tpf,tpf,tpf);
                System.out.println("Hmm, you are selecting the bad box");
                // for future work, should showcase description of the target to user
            }

            //----------------------------------

            //Potential Cache issue
            // Some other potentially neutral objects
            if (good_interact_geoms.contains(target)) {
                // the good geom rotates as if being selected, but no effects on health yet (just like an inspection)
                // future Implementation: might have a text box pop out with instructions about the interaction with the object
                target.rotate(tpf,tpf,tpf);
                System.out.println("This could be helpful?");
            } else if(bad_interact_geoms.contains(target)) {
                target.rotate(tpf,tpf,tpf);
                System.out.println("This could be harmful?");
            }
        } else {
            System.out.println("Selection: Nothing");
        }
        // System.out.println("You triggered: "+name);
    }

    @Override
    public void onRotate(float intensity, float tpf) {
        handleRotate(intensity, tpf);
    }
    
    @Override
    public void onMove(String name, float value, float tpf) {
        handleMovement(name, value, tpf);
    }
    
    // Implement movement logic
    private void handleMovement(String name, float value, float tpf) {
        Node playerNode = player.getPlayerNode();
        if (playerNode != null) {
            // Use yawNode's rotation to determine movement direction
            Vector3f dir = yawNode.getWorldRotation().mult(Vector3f.UNIT_Z).normalizeLocal();
            Vector3f left = yawNode.getWorldRotation().mult(Vector3f.UNIT_X).normalizeLocal();
            float moveSpeed = 5f; // Adjust as needed

            switch (name) {
                case GameInputManager.MAPPING_FORWARD:
                    playerNode.move(dir.mult(value * moveSpeed));
                    break;
                case GameInputManager.MAPPING_BACKWARD:
                    playerNode.move(dir.mult(-value * moveSpeed));
                    break;
                case GameInputManager.MAPPING_LEFT:
                    playerNode.move(left.mult(value * moveSpeed));
                    break;
                case GameInputManager.MAPPING_RIGHT:
                    playerNode.move(left.mult(-value * moveSpeed));
                    break;
            }
        }
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    @Override
    public void destroy() {
        flashlight.cleanup();
        dialogBox.cleanup();
        super.destroy();
    }
    
}