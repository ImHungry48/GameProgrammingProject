package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.SpotLightShadowRenderer;

/**
 * Flashlight System that manages flashlight behavior, health bar, and charge mechanics.
 */
public class FlashlightSystem extends AbstractAppState {
    
    // Constants
    private static final Trigger TRIGGER_FLASHLIGHT = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    private static final String MAPPING_FLASHLIGHT = "FlashLight";
    private static final int SHADOWMAP_SIZE = 1024; // Example shadow map size
    private static final float MAX_HEALTH = 100f; // Maximum charge for the flashlight
    
    // Instance variables
    private SimpleApplication app;
    private Node guiNode;
    private AssetManager assetManager;
    private BitmapFont guiFont;
    private GameState gameState;
    
    private float charge = MAX_HEALTH;  // Current charge level
    private boolean isOn = true;  // Flashlight state
    private SpotLight flashLight;  // SpotLight for flashlight
    private float flashRadius = 70f;  // Flashlight radius
    
    // Health Bar UI components
    private Geometry healthBarBackground;
    private Geometry healthBarForeground;
    private BitmapText healthText;
    private float maxHealthBarWidth = 200; // Maximum width of the health bar
    private float healthBarHeight = 20;    // Height of the health bar

    public FlashlightSystem(GameState gameState) {
        this.gameState = gameState;
    }
    
    /**
     * Initialize the system.
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        // Input mappings for flashlight
        setupInputMappings();

        // Setup flashlight and shadow renderer
        setupFlashlight();
        
        // Initialize health bar UI
        initHealthBar();
    }

    /**
     * Setup input mappings and listeners for flashlight actions.
     */
    private void setupInputMappings() {
        app.getInputManager().addMapping(MAPPING_FLASHLIGHT, TRIGGER_FLASHLIGHT);
        app.getInputManager().addListener(actionListener, MAPPING_FLASHLIGHT);
    }

    /**
     * Setup the flashlight light and shadow renderer.
     */
    private void setupFlashlight() {
        flashLight = new SpotLight();
        flashLight.setSpotRange(flashRadius);
        flashLight.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD);
        flashLight.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD);
        flashLight.setColor(ColorRGBA.Yellow.mult(ColorRGBA.White).mult(2));
        flashLight.setPosition(app.getCamera().getLocation());
        flashLight.setDirection(app.getCamera().getDirection());

        ((SimpleApplication) app).getRootNode().addLight(flashLight);

        // Add shadow renderer
        SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        slsr.setLight(flashLight);
        ((SimpleApplication) app).getViewPort().addProcessor(slsr);
    }

    /**
     * Toggle flashlight state on or off.
     */
    private void toggleFlashLight() {
        if (!isOn && charge > 0) {
            flashLight.setEnabled(true);
            isOn = true;
        } else {
            flashLight.setEnabled(false);
            isOn = false;
        }
    }
    
    /**
     * Action listener for flashlight toggle input.
     */
    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        if (name.equals(MAPPING_FLASHLIGHT) && !isPressed) {
            toggleFlashLight();
        }
    };

    /**
     * Update the flashlight system, including charge and flashlight position.
     */
    @Override
    public void update(float tpf) {
        if (charge <= 0) {
            isOn = false;
        }

        if (isOn && flashLight.isEnabled()) {
            flashLight.setPosition(app.getCamera().getLocation());
            flashLight.setDirection(app.getCamera().getDirection());
            charge -= 0.01;
            updateHealthBar();
        }
    }

    /**
     * Add charge to the flashlight.
     */
    public void addCharge() {
        charge += 20;
        if (charge > 100) {
            charge = 100;
        }
    }

    /**
     * Initialize the health bar UI elements.
     */
    public void initHealthBar() {
        removeExistingHealthBar();

        float padding = 10f;

        // Create health bar background
        createHealthBarBackground(padding);

        // Create health bar foreground
        createHealthBarForeground(padding);

        // Create health text
        createHealthText(padding);
    }

    /**
     * Remove existing health bar components if they exist.
     */
    private void removeExistingHealthBar() {
        if (healthBarBackground != null) {
            healthBarBackground.removeFromParent();
        }
        if (healthBarForeground != null) {
            healthBarForeground.removeFromParent();
        }
        if (healthText != null) {
            healthText.removeFromParent();
        }
    }

    /**
     * Create the health bar background.
     */
    private void createHealthBarBackground(float padding) {
        Quad backgroundQuad = new Quad(maxHealthBarWidth, healthBarHeight);
        healthBarBackground = new Geometry("HealthBarBackground", backgroundQuad);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", ColorRGBA.DarkGray);
        healthBarBackground.setMaterial(bgMat);
        healthBarBackground.setLocalTranslation(app.getCamera().getWidth() - maxHealthBarWidth - padding,
                                                app.getCamera().getHeight() - padding, 0);
        guiNode.attachChild(healthBarBackground);
    }

    /**
     * Create the health bar foreground.
     */
    private void createHealthBarForeground(float padding) {
        Quad foregroundQuad = new Quad(maxHealthBarWidth, healthBarHeight);
        healthBarForeground = new Geometry("HealthBarForeground", foregroundQuad);
        Material fgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fgMat.setColor("Color", ColorRGBA.Green);
        healthBarForeground.setMaterial(fgMat);
        healthBarForeground.setLocalTranslation(app.getCamera().getWidth() - maxHealthBarWidth - padding,
                                                 app.getCamera().getHeight() - padding, 1);
        guiNode.attachChild(healthBarForeground);
    }

    /**
     * Create the health text.
     */
    private void createHealthText(float padding) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        healthText = new BitmapText(guiFont, false);
        healthText.setSize(guiFont.getCharSet().getRenderedSize());
        healthText.setColor(ColorRGBA.White);
        healthText.setText("Charge: " + (int) charge);
        healthText.setLocalTranslation(app.getCamera().getWidth() - 150, app.getCamera().getHeight() - padding, 2);
        guiNode.attachChild(healthText);
    }

    /**
     * Update the health bar based on current charge.
     */
    private void updateHealthBar() {
        float healthRatio = charge / MAX_HEALTH;
        float newWidth = maxHealthBarWidth * healthRatio;

        // Update foreground bar width
        Quad newForegroundQuad = new Quad(newWidth, healthBarHeight);
        healthBarForeground.setMesh(newForegroundQuad);

        // Update foreground color based on health
        Material fgMat = healthBarForeground.getMaterial();
        if (healthRatio > 0.6f) {
            fgMat.setColor("Color", ColorRGBA.Green);
        } else if (healthRatio > 0.3f) {
            fgMat.setColor("Color", ColorRGBA.Yellow);
        } else {
            fgMat.setColor("Color", ColorRGBA.Red);
        }

        // Update health text
        healthText.setText("Charge: " + (int) charge);
    }

    /**
     * Check if the flashlight is on.
     */
    public boolean checkIfOn() {
        return isOn;
    }
}
