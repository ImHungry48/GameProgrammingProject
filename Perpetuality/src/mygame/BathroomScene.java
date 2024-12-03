package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import mygame.EventManagement.EventSystem;

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
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        System.out.println("Initializing bathroom");
        
        gameInputManager.enable();
        eventSystem.startListening();
        
        // Load the scene
        this.loadScene();
        
        if (gameState == null) {
            System.err.println("GameState is null! Using default values.");
        }

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

        eventSystem.startListening();
        
        System.out.println("Setting up scene dialog");
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
    
}
