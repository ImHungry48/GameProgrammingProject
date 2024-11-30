// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mygame.EventManagement.EventSystem;
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
    
    private Node pitchNode;
    private Node yawNode;
    private final Vector3f respawnPosition = new Vector3f(3.9238453f, 0f, 0f);
    
    // Dialog box
    private DialogBox dialogBox;
 
    private SceneLoader sceneLoader;
    private ClassroomScene classroomScene;
    private DialogBoxUI dialogBoxUI;
    private SceneManager sceneManager;

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
        player = new Player(playerSpatial, cam);
        player.getPlayerNode().setLocalTranslation(0,0.5f,0);
        rootNode.attachChild(player.getPlayerNode());
        
        yawNode = player.getYawNode();
        pitchNode = player.getPitchNode();

        // Disable default flyCam behavior and make the camera follow the camera node
        flyCam.setEnabled(false);
        
        // Hide the cursor to capture mouse movement
        inputManager.setCursorVisible(false);
        inputManager.setMouseCursor(null); // Hides system cursor
        
        // Initalize dialog box
        dialogBox = new DialogBox(assetManager, inputManager, guiNode, viewPort, rootNode);
        
        // Initialize the player bounds as a bounding box centered on the camera location
        float playerBoxHalfExtent = 1f; // Adjust the size as needed
        this.player.playerBounds = new BoundingBox(cam.getLocation(), playerBoxHalfExtent, playerBoxHalfExtent, playerBoxHalfExtent);
        
        gameInputManager = new GameInputManager(inputManager, player.getYawNode(), player.getPitchNode(), null);
        gameInputManager.setActionHandler(this);
        gameInputManager.setAnalogHandler(this);
        gameInputManager.setMovementHandler(this);
        
        this.eventSystem = new EventSystem(player, null, respawnPosition);
        
        /* SCENE LOADING */

        // Initialize SceneLoader
        this.sceneLoader = new SceneLoader(assetManager, rootNode);

        // Initialize DialogBox
        this.dialogBoxUI = new DialogBoxUI(this);
        this.dialogBoxUI.initialize(stateManager, this);
        this.stateManager.attach(dialogBoxUI);
        
        this.sceneManager = new SceneManager(this);
        ClassroomScene classroomScene = new ClassroomScene(this);
        BathroomScene bathroomScene = new BathroomScene(this);
        HallwayScene hallwayScene = new HallwayScene(this);
        
        sceneManager.addScene("Classroom", classroomScene);
        sceneManager.addScene("Bathroom", bathroomScene);
        sceneManager.addScene("Hallway", hallwayScene);
        
        // Load the first scene (Classroom)
        sceneManager.switchScene("Classroom");
        
        // Attach a cursor to the screen
        // attachCenterMark();
    }  
    
    public AppStateManager getStateManager() {
        return this.stateManager;
    }
    
    public Node getRootNode() {
        return this.rootNode;
    }
    
    public DialogBoxUI getDialogBoxUI() {
        return this.dialogBoxUI;
    }
    
    public GameInputManager getGameInputManager() {
        return this.gameInputManager;
    }
    
    public EventSystem getEventSystem() {
        return this.eventSystem;
    }
    
    public GameState getGameState() {
        return this.gameState;
    }
    
    public SceneLoader getSceneLoader() {
        return this.sceneLoader;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public SceneManager getSceneManager() {
        return this.sceneManager;
    }
    
    

    @Override
    public void simpleUpdate(float tpf) {
        
        // Update the classroom scene if it's active
        if (classroomScene != null) {
            this.classroomScene.update(tpf);
        }
        
        this.player.playerBounds.setCenter(cam.getLocation());
        
        if (bathroomModel != null && this.player.playerBounds != null) {
            if (this.player.playerBounds.intersects((BoundingBox) bathroomModel.getWorldBound())) {
                gameState.increaseHealth(1);
            }
        }
        
        //flashlight.update(cam.getLocation(), cam.getDirection());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void handleRotate(float intensity, float tpf) {
        
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
    
    @Override
    public void destroy() {
        dialogBox.cleanup();
        super.destroy();
    }
    
}