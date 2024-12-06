package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
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

/**
 *
 * @author abarbe23
 */
public class ClassroomScene extends AbstractAppState {
    private SimpleApplication app;
    private InputManager inputManager;
    private Node rootNode;
    private AppStateManager stateManager;
    private GameState gameState;
    private GameManager gameManager;

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
    
    // Jumpscare
    private boolean jumpscareTriggered = false;
    private float jumpscareElapsed = 0;
    private float jumpscareDuration = 2.0f; // Total duration of the jumpscare
    private Spatial targetModel; // The model we are zooming in on
    private Vector3f originalModelPosition; // To reset the model
    private Vector3f originalCameraPosition; // To reset the camera after the zoom
    private float originalFOV = 45f; // Original field of view
    private float targetFOV = 5f; // Zoomed-in field of view
    private AudioNode jumpscareSound; // Jumpscare audio
    private boolean jumpscareStarted = false;
    
    private Quaternion originalYawRotation;
    private Quaternion originalPitchRotation;
    
    private Geometry staticOverlay; // Geometry for static overlay
    private boolean staticTriggered = false; // Trigger for static effect
    private float staticTimeElapsed = 0; // Timer for static effect
    private float staticDuration = 2.0f; // Duration for the static effect
    private SceneManager sceneManager;

    public ClassroomScene(GameManager gameManager) {
        this.sceneLoader = gameManager.getSceneLoader();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.player = gameManager.getPlayer();
        this.sceneManager = gameManager.getSceneManager();
        this.gameState = gameManager.getGameState();
        this.gameManager = gameManager;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.inputManager = this.app.getInputManager();
        this.rootNode = this.app.getRootNode();
        this.stateManager = stateManager;

        initializeFadeOverlay();
        initializeStaticOverlay();
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
    
    private void initializeStaticOverlay() {
        int screenWidth = app.getContext().getSettings().getWidth();
        int screenHeight = app.getContext().getSettings().getHeight();

        Quad quad = new Quad(screenWidth, screenHeight);
        staticOverlay = new Geometry("StaticOverlay", quad);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", app.getAssetManager().loadTexture("Textures/StaticOverlay/Static-1.png"));
        mat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha); // Optional blending
        staticOverlay.setMaterial(mat);

        staticOverlay.setQueueBucket(RenderQueue.Bucket.Gui);
        staticOverlay.setLocalTranslation(0, 0, 1); // Overlay on top of the GUI

        app.getGuiNode().attachChild(staticOverlay);

        // Start with static hidden
        staticOverlay.setCullHint(Spatial.CullHint.Always);
    }


    public void loadScene() {
        sceneLoader.loadScene("Scenes/IntroScene.j3o", this::setupScene);
    }

    private void setupScene() {

        // Find spawn point and set player position if necessary
        Spatial spawnPoint = sceneLoader.getRootNode().getChild("Classroom_A1_Spawn");
        if (spawnPoint != null) {
            // Set player's position to the spawn point
            this.player.setPosition(spawnPoint.getWorldTranslation());
            
            // Set player's rotation to match the spawn point's rotation
            this.player.setCameraRotation(spawnPoint.getWorldRotation());
        }
        // Set up lights and other scene elements
        setupLighting();
        setupJumpscareScene();
        setupAudio();
    }
    
    private void setupLighting() {
        PointLight pl = new PointLight();
        rootNode.addLight(pl);
    }
    
    private void setupJumpscareScene() {
        this.targetModel = sceneLoader.getRootNode().getChild("Model_Point");
        
        if (targetModel != null) {
            this.originalModelPosition = targetModel.getWorldTranslation().clone();
        } else {
            System.err.println("Target model for jumpscare not found! :(");
        }
        
        this.originalFOV = 45f; // Default perspective FOV
        app.getCamera().setFrustumPerspective(originalFOV,
                (float) app.getContext().getSettings().getWidth() /
                (float) app.getContext().getSettings().getHeight(),
                1f, 1000f);
        
        // Store the original camera rotations
        this.originalYawRotation = player.getYawNode().getLocalRotation().clone();
        this.originalPitchRotation = player.getPitchNode().getLocalRotation().clone();
    }

    @Override
    public void update(float tpf) {
        transitionToBathroom();
        this.elapsedTime += tpf;

        if (!fadeInComplete) {
            updateFadeIn(tpf);
        } else if (!transitionTriggered) {
            if (elapsedTime > 10 && !dialogShown1) {
                dialogBoxUI.showDialog("Alright students. You have one hour to complete the exam. Keep your eyes on your own paper.", 1.5f, false);
                dialogShown1 = true; // Flag to ensure this executes only once
            }

            if (elapsedTime > 15 && !dialogShown2) {
                // Start camera rotation
                this.lookingDown = true;
                this.rotationTimeElapsed = 0;
                
                dialogBoxUI.hideDialog();
                dialogBoxUI.showDialog("[I can't believe this is happening. I've barely studied for this.]", 1.5f, false);
                dialogShown2 = true; // Ensure this executes only once
            }

            // Handle camera rotation downward
            if (this.lookingDown) {
                this.rotationTimeElapsed += tpf;
                if (this.rotationTimeElapsed < 5.0f) { // Rotate for 1 second
                    rotateCameraToLookDown(tpf);
                } else {
                    this.lookingDown = false;
                    this.lookingUp = true;
                    this.rotationTimeElapsed = 0;
                }
            }

            // Handle camera rotation upward
            if (this.lookingUp) {
                this.rotationTimeElapsed += tpf;
                if (rotationTimeElapsed < 3.0f) { // Rotate back up for 1 second
                    this.rotateCameraToLookUp(tpf);
                } else {
                    this.lookingUp = false;
                }
            }

            if (this.elapsedTime > 20 && !dialogShown3) {
                this.dialogBoxUI.hideDialog();
                this.dialogBoxUI.showDialog("Your exam will begin soon. Remember, no talking. If you are caught cheating, you will be disqualified.", 1.5f, false);
                this.dialogShown3 = true;
            }

            if (elapsedTime > 23 && !dialogShown4) {
                // Start camera rotation
                this.lookingDown = true;
                this.rotationTimeElapsed = 0;
                
                this.dialogBoxUI.hideDialog();
                this.dialogBoxUI.showDialog("[I can't even read this.]", 1.5f, false);
                this.dialogShown4 = true;
            }
            
            // Handle camera rotation downward
            if (this.lookingDown) {
                this.rotationTimeElapsed += tpf;
                if (this.rotationTimeElapsed < 5.0f) { // Rotate for 1 second
                    this.rotateCameraToLookDown(tpf);
                } else {
                    this.lookingDown = false;
                    this.lookingUp = true;
                    this.rotationTimeElapsed = 0;
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

            if (elapsedTime > 30 && !dialogShown5) {
                dialogBoxUI.hideDialog();
                dialogBoxUI.showDialog("I'm gonna be sick.", 1.5f, false);
                dialogShown5 = true;
            }

            if (elapsedTime > 32 && !jumpscareStarted) {
                jumpscareStarted = true; // Set the flag to prevent re-triggering
                dialogBoxUI.hideDialog();
                dialogBoxUI.showDialog("HEY!", 3.0f, true);
                dialogBoxUI.startShake(2.0f, 10.0f);

                jumpscareTriggered = true;
                jumpscareElapsed = 0;
                
                // Play jumpscare sound
                if (jumpscareSound != null) {
                    jumpscareSound.play();
                }
            }
        }
        
        if (jumpscareTriggered) {
            jumpscareElapsed += tpf;

            if (jumpscareElapsed <= jumpscareDuration) {
                // Interpolate the FOV for zoom
                float zoomProgress = jumpscareElapsed / jumpscareDuration;
                float currentFOV = FastMath.interpolateLinear(zoomProgress, originalFOV, targetFOV);

                app.getCamera().setFrustumPerspective(currentFOV,
                    (float) app.getContext().getSettings().getWidth() /
                    (float) app.getContext().getSettings().getHeight(),
                    1f, 1000f);

                // Adjust camera to look at the target model
                adjustCameraToLookAtModel();

                // Shake the target model
                if (targetModel != null) {
                    float offsetX = (float) (Math.random() * 2 - 1) * 0.1f;
                    float offsetY = (float) (Math.random() * 2 - 1) * 0.1f;
                    float offsetZ = (float) (Math.random() * 2 - 1) * 0.1f;

                    targetModel.setLocalTranslation(originalModelPosition.add(offsetX, offsetY, offsetZ));
                }
            } else {
                // Reset after the jumpscare
                dialogBoxUI.hideDialog();
                jumpscareTriggered = false;
                jumpscareElapsed = 0;
                showStaticOverlay();

                app.getCamera().setFrustumPerspective(originalFOV,
                    (float) app.getContext().getSettings().getWidth() /
                    (float) app.getContext().getSettings().getHeight(),
                    1f, 1000f);

                // Reset camera orientation
                player.getYawNode().setLocalRotation(originalYawRotation);
                player.getPitchNode().setLocalRotation(originalPitchRotation);

                if (targetModel != null) {
                    targetModel.setLocalTranslation(originalModelPosition);
                }
            }
        }
        
        if (staticTriggered) {
            staticTimeElapsed += tpf;

            if (staticOverlay != null) {
                animateStatic(tpf);  
            }

            if (staticTimeElapsed > staticDuration) {
                // End the static effect
                hideStaticOverlay();
                stateManager.attach(gameManager.getGameState());
                transitionToBathroom(); // Transition after static effect
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
    
    private void showStaticOverlay() {
        if (staticOverlay != null) {
            staticOverlay.setCullHint(Spatial.CullHint.Never); // Make it visible
            staticTriggered = true; // Start the static effect
            staticTimeElapsed = 0; // Reset the timer
        }
    }
    
    private void hideStaticOverlay() {
        if (staticOverlay != null) {
            staticOverlay.setCullHint(Spatial.CullHint.Always); // Hide the overlay
        }
        staticTriggered = false; // Reset the trigger
    }
    
    private void animateStatic(float tpf) {
        int speed = 10;
        int numberofframes = 10;
        int frame = (int) (staticTimeElapsed * speed) % numberofframes; // TODO: Adjust speed and frame count
        String textureName = "Textures/StaticOverlay/Static-" + (frame + 1) + ".png";
        Material mat = staticOverlay.getMaterial();
        mat.setTexture("ColorMap", app.getAssetManager().loadTexture(textureName));
    }
    
    private void adjustCameraToLookAtModel() {
        // Get the camera's current position
        Vector3f cameraPosition = app.getCamera().getLocation();

        // Get the target model's position
        Vector3f targetPosition = targetModel.getWorldTranslation();

        // Calculate the direction vector from the camera to the target
        Vector3f directionToTarget = targetPosition.subtract(cameraPosition).normalizeLocal();

        // Calculate the yaw and pitch angles
        float yaw = FastMath.atan2(directionToTarget.x, directionToTarget.z);
        float pitch = FastMath.asin(directionToTarget.y);

        // Create quaternions from yaw and pitch
        Quaternion yawRotation = new Quaternion().fromAngles(0, yaw, 0);
        Quaternion pitchRotation = new Quaternion().fromAngles(-pitch, 0, 0);

        // Set the yaw and pitch rotations
        player.getYawNode().setLocalRotation(yawRotation);
        player.getPitchNode().setLocalRotation(pitchRotation);
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
        sceneManager.switchScene("Bathroom");
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
    
    private void setupAudio() {
        jumpscareSound = new AudioNode(app.getAssetManager(), "Sound/mascot-jumpscare.wav", false);
        jumpscareSound.setPositional(false);
        jumpscareSound.setLooping(false);
        jumpscareSound.setVolume(3);
        app.getRootNode().attachChild(jumpscareSound);
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
