// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
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
import mygame.EventManagement.Event;
import mygame.EventManagement.EventSystem;
import mygame.GameInputManager.ActionHandler;
import mygame.GameInputManager.AnalogHandler;

/**
 *
 * @author mike0
 * Game Manager that maps the controls, set the scene up, and populate the game with interactable objects
 */
public class GameManager extends SimpleApplication implements ActionHandler, AnalogHandler, GameInputManager.MovementHandler{    
    // Maintain the game controls
    private GameInputManager gameInputManager;
    // Maintain the state of the game
    private GameState gameState;
    // Inventory system to manage sanity
    private InventorySystem inventory;
    
    // Sanity Bar UI
    private SanityBarUI sanityBarUI;
    
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
    private final Vector3f respawnPosition = new Vector3f(0f, 0f, 0f);

    // Field for Game Logic
    @Override
    public void simpleInitApp() {
        
        // Detach FlyCamAppState to prevent it from interfering with cursor visibility
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        
        // Initialize player spatial
        Spatial playerSpatial = assetManager.loadModel("/Models/male_base_mesh/male_base_mesh.j3o");

        // Initialize the player with its model and attach to root node
        player = new Player(playerSpatial);
        player.getPlayerNode().setLocalTranslation(0,1,0);
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
        
        // Set up GameInputManager for camera controls
        gameInputManager = new GameInputManager(inputManager, yawNode, pitchNode);
        gameInputManager.setActionHandler(this);
        gameInputManager.setAnalogHandler(this);
        gameInputManager.setMovementHandler(this);
        gameInputManager.initInputMappings();

        // Synchronize the camera with the camera node in simpleUpdate
        //cam.setLocation(cameraNode.getWorldTranslation());
        //cam.lookAt(player.getPlayerNode().getWorldTranslation(), Vector3f.UNIT_Y);

        // Potential Cache Issue
        good_interact_geoms = new ArrayList<>();
        bad_interact_geoms = new ArrayList<>();
        
        // Just as an idea, maybe grades should be the threshold for which we have different status
        
        // Fill the scene with some randomly positioned interactable but all on the y = 0 plane
        makeGoodCubes(15);
        makeBadCubes(15);
        
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
        
        
        // Collectable objects for now
        rootNode.attachChild(myBox("Good Box",
                loc1, ColorRGBA.Green));
        
        
        rootNode.attachChild(myBox("Bad Box", 
                loc2, ColorRGBA.Red));

        // Initialize the game state
        gameState = new GameState();
        stateManager.attach(gameState);
      
        inventory = new InventorySystem();
        
        // To make camera (the character) runs faster 
        // TODO: Disabled for first-person testing since it's redundant
        // flyCam.setMoveSpeed(50f);
        
        /* SCENE LOADING */
        SceneLoader sceneLoader = new SceneLoader(assetManager, rootNode);
        sceneLoader.loadScene("Scenes/Bathroom.j3o");
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1.0f, -0.5f).normalizeLocal());  // Direction of the light
        sun.setColor(ColorRGBA.fromRGBA255(255, 255, 255, 5));  // Color of the light
        rootNode.addLight(sun);  // Attach the light to the rootNode
        // Yellow light: 245, 205, 86
        
        // General Light
        DirectionalLight lamp = new DirectionalLight();
        lamp.setDirection(new Vector3f(0, 10f, 0f).normalizeLocal());  // Direction of the light
        lamp.setColor(ColorRGBA.fromRGBA255(255, 255, 255, 5));  // Color of the light
        rootNode.addLight(lamp);  // Attach the light to the rootNode

        // Load the bathroom model from the scene
        bathroomModel = rootNode.getChild("Bathroom"); // Replace with the name of your bathroom node
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

        sanityBarUI = new SanityBarUI(this);
        stateManager.attach(sanityBarUI);
        
        /* EVENT SYSTEM */
        this.eventSystem = new EventSystem(this.player, this.bathroomBounds, this.respawnPosition);
        this.eventSystem.loadEvents();
        
        // Attach a cursor to the screen
        attachCenterMark();
    } 
    
    // Crosshair for our game :)
    private void attachCenterMark() {
        Geometry c = myBox("center mark", 
            Vector3f.ZERO, ColorRGBA.White);
        c.scale(4); 
        c.setLocalTranslation( settings.getWidth()/2,
        settings.getHeight()/2, 0 ); 
        guiNode.attachChild(c); // attach to 2D user interface
    }
    
    // Interactable cubes that can increase sanity
    private void makeGoodCubes(int number) {
        for (int i = 0; i < number; i++) {
            Vector3f loc = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50)
            );
            
            // Add the good geom
            Geometry goodgeom = myBox("GBox" + i, loc, ColorRGBA.Yellow);
            goodgeom.addControl(new ChangeHealthBarControl(cam, rootNode));
            rootNode.attachChild(goodgeom);
            
            // Add the goodgeom to good_geoms
            // Potential Cache ISSUE
            good_interact_geoms.add(goodgeom);
            
        }
    }
    
    // Interactable cubes that can increase sanity
    private void makeBadCubes(int number) {
        for (int i = 0; i < number; i++) {
            Vector3f loc = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50) 
            );
            
            // Add the bad geom
            Geometry badgeom = myBox("BBox" + i, loc, ColorRGBA.Pink);
            badgeom.addControl(new ChangeHealthBarControl(cam, rootNode));
            rootNode.attachChild(badgeom);
            
            // Add the badgeom to bad_geoms
            // Potential Cache ISSUE
            bad_interact_geoms.add(badgeom);
        }
    }
        
    // Box constructor
    public Geometry myBox(String name, Vector3f loc, ColorRGBA color) {
        Geometry local_geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        local_geom.setMaterial(mat);
        local_geom.setLocalTranslation(loc);
        return local_geom;
    }
    
    
    // ActionListener to handle actions
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                switch (name) {
                    case "Change Health":
                        handleChangeHealth();
                        break;
                    case "Use Item 1":
                        if (inventory.checkItemExists(1)) {
                            gameState.applyHealth(inventory.useItem(1));
                        }
                        break;
                    case "Use Item 2":
                        if (inventory.checkItemExists(2)) {
                            gameState.applyHealth(inventory.useItem(2));
                        }
                        break;
                    case "Use Item 3":
                        if (inventory.checkItemExists(3)) {
                            gameState.applyHealth(inventory.useItem(3));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };    

    @Override
    public void simpleUpdate(float tpf) {
        
        // Update the sanity bar based on the GameState’s health value
        sanityBarUI.setSanity(gameState.getHealth());
        
        if (gameState.getHealth() <= 0) {
            // System.out.println("It is easier to die than live, huh?");
            
            try {
                // Trigger respawn event based on current state (altered or normal)
                Event respawnEvent = eventSystem.getEventByName("RespawnNormal");
                if (respawnEvent != null) {
                    eventSystem.triggerEvent("RespawnNormal");
                } else {
                    throw new NullPointerException();
                }
            } catch (NullPointerException a) {
                //System.out.println("The bathroom is null.");
            } catch (Exception e) {
                System.out.println("Failed to respawn.");
            }
        }
        
        this.player.playerBounds.setCenter(cam.getLocation());
        
        if (bathroomModel != null && this.player.playerBounds != null) {
            if (this.player.playerBounds.intersects((BoundingBox) bathroomModel.getWorldBound())) {
                gameState.increaseHealth(1);
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /* ActionHandler Methods */
    @Override
    public void onChangeHealth() {
        handleChangeHealth();
    }

    // Manage health when an object is interacted
    private void handleChangeHealth() {
        // implement action here
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        rootNode.collideWith(ray, results);


        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();
            // implement action here
            
            // Good Box and Bad Box are collectable objects that will be gone once clicked
            if (target.getName().equals("Good Box")) {
                gameState.increaseHealth(20);
                inventory.addItem(new Item("Apple", "A delicious apple",10));
                rootNode.detachChild(target); // you have collected the apple, so it's gone
            } else if (target.getName().equals("Bad Box")) {
                gameState.decreaseHealth(10);
                rootNode.detachChild(target);
            }
            
            // Good_geoms and Bad_geoms are currently more like neutral objects that are not collectable but interactable
            if (good_interact_geoms.contains(target)) {
                gameState.increaseHealth(3);
                target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                good_interact_geoms.remove(target); // delete the obj instance as a good geom
                // 
            } else if(bad_interact_geoms.contains(target)) {
                gameState.decreaseHealth(1);
                target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                bad_interact_geoms.remove(target); // delete the obj instance as a bad geom
            }
        }
    }

    @Override
    public void onUseItem(int itemNumber) {
        if (inventory.checkItemExists(itemNumber)) {
            gameState.applyHealth(inventory.useItem(itemNumber));
        }
    }
    
    public void handleRotate(float intensity, float tpf) {
        // implement action here
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        rootNode.collideWith(ray, results);
        /* For printing out result, but don't need this
        for (int i = 0; i < results.size(); i++){
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String target = results.getCollision(i).getGeometry().getName();
            System.out.println("Selection: #" + i + ": " + target +
            " at " + pt + ", " + dist + " WU away.");
        } */ 
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
                    System.out.println("Player is moving forward.");
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
    
}