package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import mygame.EventManagement.EventSystem;
import mygame.SceneLoader;

/**
 *
 * @author abarbe23
 */
public class BathroomScene extends AbstractAppState {
    private SceneManager sceneManager;
    private Player player;
    private GameState gameState;
    private InventorySystem inventory;
    private GameInputManager gameInputManager;
    private EventSystem eventSystem;
    
    private SceneLoader sceneLoader;
    private DialogBoxUI dialogBoxUI;
    private boolean flashlightPickedUp = false;

    public BathroomScene(SceneLoader sceneLoader, SceneManager sceneManager, DialogBoxUI dialogBoxUI,
            Player player, GameState gameState, InventorySystem inventory,
            GameInputManager gameInputManager, EventSystem eventSystem) {
        this.sceneLoader = sceneLoader;
        this.dialogBoxUI = dialogBoxUI;
        this.player = player;
        this.gameState = gameState;
        this.inventory = inventory;
        this.gameInputManager = gameInputManager;
        this.eventSystem = eventSystem;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        System.out.println("Initializing bathroom");
        super.initialize(stateManager, app);
        
        if (!stateManager.hasState(inventory)) {
            stateManager.attach(inventory);
        }
        
        gameInputManager.enable();
        
        eventSystem.startListening();
        
        System.out.println("Loading bathroom");
        
        // Load the scene
        sceneLoader.loadScene("Scenes/Bathroom.j3o", this::setupScene);
        
        System.out.println("Restoring player position");
        
        if (gameState == null) {
            System.err.println("GameState is null! Using default values.");
            gameState = new GameState(); // Fallback to a new game state
        }

        player.getPlayerNode().setLocalTranslation(
            gameState.getPlayerPosition() != null ? gameState.getPlayerPosition() : new Vector3f(0, 0, 0)
        );
        player.getYawNode().setLocalRotation(
            gameState.getPlayerOrientation() != null ? gameState.getPlayerOrientation() : new Quaternion()
        );

        gameInputManager.initInputMappings();
        gameInputManager.enable();

        eventSystem.startListening();
        
        System.out.println("Loading scene");
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
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("ExitRoom") && isPressed) {
                if (isNearExitPoint(player.getPlayerNode().getWorldTranslation())) {
                    //sceneManager.switchScene(currentScene, new Hallway());
                }
            }
        }
    };
    
    private boolean isNearExitPoint(Vector3f playerPosition) {
        // Check if the player is close to an exit point
        //BoundingBox exitBox = new BoundingBox(exitPoint.getWorldTranslation(), 1f, 2f, 1f);
        //return exitBox.contains(playerPosition);
        return true;
    }
}
