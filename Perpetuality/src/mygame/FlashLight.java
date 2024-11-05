package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.shadow.SpotLightShadowRenderer;

public class FlashLight {

    private SpotLight flashLight;
    private SpotLightShadowRenderer shadowRenderer;
    private boolean flashlightOn = false;
    private final InputManager inputManager;
    private final ViewPort viewPort;
    private final Node rootNode;
    private static final String TOGGLE_FLASHLIGHT = "ToggleFlashlight";

    // Constructor
    public FlashLight(AssetManager assetManager, InputManager inputManager, ViewPort viewPort, Node rootNode) {
        this.inputManager = inputManager;
        this.viewPort = viewPort;
        this.rootNode = rootNode;

        // Initialize the flashlight
        flashLight = new SpotLight();
        flashLight.setSpotRange(70f);
        flashLight.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD);
        flashLight.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD);
        flashLight.setColor(ColorRGBA.White.mult(2.0f));  // Adjust brightness

        // Initialize shadow renderer for the flashlight
        shadowRenderer = new SpotLightShadowRenderer(assetManager, 1024);
        shadowRenderer.setLight(flashLight);

        // Set up flashlight toggle input
        inputManager.addMapping(TOGGLE_FLASHLIGHT, new KeyTrigger(KeyInput.KEY_F));
        inputManager.addListener(actionListener, TOGGLE_FLASHLIGHT);
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(TOGGLE_FLASHLIGHT) && !isPressed) {
                toggleFlashlight();
            }
        }
    };

    private void toggleFlashlight() {
        flashlightOn = !flashlightOn;
        if (flashlightOn) {
            viewPort.addProcessor(shadowRenderer);
            rootNode.addLight(flashLight);
        } else {
            viewPort.removeProcessor(shadowRenderer);
            rootNode.removeLight(flashLight);
        }
    }

    // Update method to sync flashlight position and direction with the camera
    public void update(Vector3f cameraLocation, Vector3f cameraDirection) {
        if (flashlightOn) {
            flashLight.setPosition(cameraLocation);
            flashLight.setDirection(cameraDirection);
        }
    }

    // Cleanup method for removing mappings and listeners
    public void cleanup() {
        inputManager.deleteMapping(TOGGLE_FLASHLIGHT);
        inputManager.removeListener(actionListener);
        rootNode.removeLight(flashLight);
        viewPort.removeProcessor(shadowRenderer);
    }
}