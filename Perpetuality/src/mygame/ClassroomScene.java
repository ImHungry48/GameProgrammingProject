package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;

public class ClassroomScene extends AbstractAppState {
    private SimpleApplication app;
    private InputManager inputManager;
    private Node rootNode;
    private AppStateManager stateManager;

    private SceneLoader sceneLoader;
    private DialogBoxUI dialogBoxUI;

    // Transition fade
    private Geometry fadeOverlay;
    private Material fadeMaterial;
    private float fadeAlpha = 1.0f; // Start fully opaque
    private boolean fadeInComplete = false;

    // Transition time
    private float elapsedTime = 0;
    private boolean resetElapsed = false;
    private boolean transitionTriggered = false;
    
    // Player
    private Player player;
    
    // Enemy
    private AnimateModel animateModel;
    
    // Camera handling
    private boolean dialogShown1 = false;
    private boolean dialogShown2 = false;
    private boolean dialogShown3 = false;
    private boolean dialogShown4 = false;
    private boolean dialogShown5 = false;
    private boolean lookingDown = false;
    private boolean lookingUp = false;
    private float rotationTimeElapsed = 0;

    public ClassroomScene(SceneLoader sceneLoader, DialogBoxUI dialogBoxUI, Player player) {
        this.sceneLoader = sceneLoader;
        this.dialogBoxUI = dialogBoxUI;
        this.player = player;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.inputManager = this.app.getInputManager();
        this.rootNode = this.app.getRootNode();
        this.stateManager = stateManager;

        initializeFadeOverlay();
        loadScene();
        setupCam();
        disablePlayerMovement();
    }

    private void initializeFadeOverlay() {
        int screenWidth = app.getContext().getSettings().getWidth();
        int screenHeight = app.getContext().getSettings().getHeight();

        Quad quad = new Quad(screenWidth, screenHeight);
        fadeOverlay = new Geometry("FadeOverlay", quad);

        fadeMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        fadeMaterial.setColor("Color", new ColorRGBA(0, 0, 0, fadeAlpha));
        fadeMaterial.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
        fadeMaterial.getAdditionalRenderState().setDepthTest(false);
        fadeMaterial.getAdditionalRenderState().setDepthWrite(false);

        fadeOverlay.setMaterial(fadeMaterial);
        fadeOverlay.setQueueBucket(RenderQueue.Bucket.Gui);
        fadeOverlay.setCullHint(Spatial.CullHint.Never);
        fadeOverlay.setLocalTranslation(0, 0, 1);

        app.getGuiNode().attachChild(fadeOverlay);
    }

    public void loadScene() {
        sceneLoader.loadScene("Scenes/Classroom_A1.j3o", this::setupScene);
    }

    private void setupScene() {

        // Find spawn point and set player position if necessary
        Spatial spawnPoint = sceneLoader.getRootNode().getChild("Classroom_A1_Spawn");
        if (spawnPoint != null) {
            // Set player's position to the spawn point
            this.player.setPosition(spawnPoint.getWorldTranslation());
            
            // Set player's rotation to match the spawn point's rotation
            System.out.println("Spawnpoint translation: " + spawnPoint.getWorldTranslation() + "\nSpawnpoint rotation: " + spawnPoint.getWorldRotation());
            this.player.setCameraRotation(spawnPoint.getWorldRotation());
        } else {
            System.err.println("Player spawn point not found in scene!");
        }

        // Set up lights and other scene elements
        setupLighting();
    }
    
    private void setupLighting() {
        PointLight pl = new PointLight();
        rootNode.addLight(pl);
    }

    @Override
    public void update(float tpf) {
        elapsedTime += tpf;

        if (!fadeInComplete) {
            updateFadeIn(tpf);
        } else if (!transitionTriggered) {
            // Example sequence tracker (optional)
            if (elapsedTime > 10 && !dialogShown1) {
                dialogBoxUI.showDialog("Alright students. You have one hour to complete the exam. Keep your eyes on your own paper.");
                dialogShown1 = true; // Flag to ensure this executes only once
            }

            if (elapsedTime > 15 && !dialogShown2) {
                // Start camera rotation
                lookingDown = true;
                rotationTimeElapsed = 0;
                
                dialogBoxUI.hideDialog();
                dialogBoxUI.showDialog("[I can't believe this is happening. I've barely studied for this.]");
                dialogShown2 = true; // Ensure this executes only once
            }

            // Handle camera rotation downward
            if (lookingDown) {
                rotationTimeElapsed += tpf;
                if (rotationTimeElapsed < 5.0f) { // Rotate for 1 second
                    rotateCameraToLookDown(tpf);
                } else {
                    lookingDown = false;
                    lookingUp = true;
                    rotationTimeElapsed = 0;
                }
            }

            // Handle camera rotation upward
            if (lookingUp) {
                rotationTimeElapsed += tpf;
                if (rotationTimeElapsed < 3.0f) { // Rotate back up for 1 second
                    rotateCameraToLookUp(tpf);
                } else {
                    lookingUp = false;
                }
            }

            if (elapsedTime > 30 && !dialogShown3) {
                dialogBoxUI.hideDialog();
                dialogBoxUI.showDialog("Your exam will begin soon. Remember, no talking. If you are caught cheating, you will be disqualified.");
                dialogShown3 = true;
            }

            if (elapsedTime > 35 && !dialogShown4) {
                // Start camera rotation
                lookingDown = true;
                rotationTimeElapsed = 0;
                
                dialogBoxUI.hideDialog();
                dialogBoxUI.showDialog("[I can't even read this.]");
                dialogShown4 = true;
            }
            
            // Handle camera rotation downward
            if (lookingDown) {
                rotationTimeElapsed += tpf;
                if (rotationTimeElapsed < 5.0f) { // Rotate for 1 second
                    rotateCameraToLookDown(tpf);
                } else {
                    lookingDown = false;
                    lookingUp = true;
                    rotationTimeElapsed = 0;
                }
            }

            // Handle camera rotation upward
            if (lookingUp) {
                rotationTimeElapsed += tpf;
                if (rotationTimeElapsed < 3.0f) { // Rotate back up for 1 second
                    rotateCameraToLookUp(tpf);
                } else {
                    lookingUp = false;
                }
            }

            if (elapsedTime > 50 && !dialogShown5) {
                dialogBoxUI.hideDialog();
                dialogBoxUI.showDialog("I'm gonna be sick.");
                dialogShown5 = true;
            }

            if (elapsedTime > 55 && !transitionTriggered) {
                transitionTriggered = true;
            }
        }

        if (transitionTriggered) {
            if (!resetElapsed) {
                elapsedTime = 0;
                resetElapsed = true; // Ensure reset happens only once
            }

            if (elapsedTime > 5) {
                // Add transition logic here
            }
        }
    }

    private void updateFadeIn(float tpf) {
        fadeAlpha -= tpf / 9.0f; // Adjust fade duration as needed
        if (fadeAlpha <= 0) {
            fadeAlpha = 0;
            fadeInComplete = true;
            fadeOverlay.removeFromParent();
        }
        fadeMaterial.setColor("Color", new ColorRGBA(0, 0, 0, fadeAlpha));
    }

    private void setupCam() {
        app.getFlyByCamera().setEnabled(false);
        inputManager.setCursorVisible(false);
  
    }

    private void transitionToBathroom() {
        //stateManager.detach(this);
        //BathroomScene bathroomScene = new BathroomScene(sceneLoader, dialogBoxUI);
        //stateManager.attach(bathroomScene);
    }
    
    private void disablePlayerMovement() {
        inputManager.deleteMapping("MoveForward");
        inputManager.deleteMapping("MoveBackward");
        inputManager.deleteMapping("MoveLeft");
        inputManager.deleteMapping("MoveRight");
        // Disable other movement mappings

        // Disable mouse look if you have custom mappings
        inputManager.deleteMapping("FLYCAM_Left");
        inputManager.deleteMapping("FLYCAM_Right");
        inputManager.deleteMapping("FLYCAM_Up");
        inputManager.deleteMapping("FLYCAM_Down");
    }

    public void addSpotlights() {
        // Define positions
        Vector3f[] positions = {
            new Vector3f(-1.158581f, 2.4793794f, -2.0459402f), // top right
            // ... other positions
        };

        // Define direction (downward)
        Vector3f direction = new Vector3f(0, -1, 0).normalizeLocal();

        // Add a spotlight for each position
        for (Vector3f position : positions) {
            SpotLight spotLight = new SpotLight();
            spotLight.setPosition(position);
            spotLight.setDirection(direction);
            spotLight.setColor(ColorRGBA.White);
            spotLight.setSpotRange(10f);
            spotLight.setSpotInnerAngle(10f * FastMath.DEG_TO_RAD);
            spotLight.setSpotOuterAngle(30f * FastMath.DEG_TO_RAD);
            rootNode.addLight(spotLight);
        }
    }
    
    private void rotateCameraToLookDown(float tpf) {
        float currentPitch = player.getPitchNode().getLocalRotation().toAngles(null)[0];
        float targetPitch = FastMath.DEG_TO_RAD * 30; // Look down by 30 degrees
        float rotationSpeed = FastMath.DEG_TO_RAD * 60; // Degrees per second

        // Interpolate towards the target pitch
        float newPitch = FastMath.interpolateLinear(tpf * rotationSpeed, currentPitch, targetPitch);

        // Update pitch rotation
        player.getPitchNode().setLocalRotation(new Quaternion().fromAngles(newPitch, 0, 0));
    }
    
    private void rotateCameraToLookUp(float tpf) {
        float currentPitch = player.getPitchNode().getLocalRotation().toAngles(null)[0];
        float targetPitch = 0; // Default level position
        float rotationSpeed = FastMath.DEG_TO_RAD * 60; // Degrees per second

        // Interpolate towards the target pitch
        float newPitch = FastMath.interpolateLinear(tpf * rotationSpeed, currentPitch, targetPitch);

        // Update pitch rotation
        player.getPitchNode().setLocalRotation(new Quaternion().fromAngles(newPitch, 0, 0));
    }



    @Override
    public void cleanup() {
        super.cleanup();
        if (fadeOverlay.getParent() != null) {
            fadeOverlay.removeFromParent();
        }
        app.getFlyByCamera().setEnabled(true);
        // Re-enable any inputs or settings you changed
    }
   
}
