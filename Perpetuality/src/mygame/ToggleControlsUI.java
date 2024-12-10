package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

public class ToggleControlsUI extends AbstractAppState {
    private SimpleApplication app;
    private Node guiNode;

    private Geometry background;
    private BitmapText controlsText;
    private BitmapText hintText;
    private boolean isVisible = false;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();

        // Create UI elements
        createBackground();
        createControlsText();
        createHintText();

        // Map the "C" key to toggle visibility
        app.getInputManager().addMapping("ToggleControls", new KeyTrigger(KeyInput.KEY_C));
        app.getInputManager().addListener(toggleListener, "ToggleControls");
    }

    private final ActionListener toggleListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("ToggleControls") && !isPressed) {
                toggleVisibility();
            }
        }
    };

    private void createBackground() {
        // Create a black rectangle for the background
        Quad quad = new Quad(400, 150); // Adjust size as needed
        background = new Geometry("ControlsBackground", quad);

        Material backgroundMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        backgroundMat.setColor("Color", new ColorRGBA(0, 0, 0, 0.7f)); // Black with slight transparency
        background.setMaterial(backgroundMat);

        // Position background in the bottom-left corner
        background.setLocalTranslation(10, 60, 0); // Adjust to avoid overlap with hint text
        guiNode.attachChild(background);
        background.setCullHint(Spatial.CullHint.Always); // Initially hidden
    }

    private void createControlsText() {
        BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        controlsText = new BitmapText(font, false);
        controlsText.setSize(font.getCharSet().getRenderedSize());
        controlsText.setText(getControlsText());
        controlsText.setLocalTranslation(30, 190, 1); // Adjust to align with background
        guiNode.attachChild(controlsText);
        controlsText.setCullHint(Spatial.CullHint.Always); // Initially hidden
    }

    private void createHintText() {
        BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        hintText = new BitmapText(font, false);
        hintText.setSize(font.getCharSet().getRenderedSize());
        hintText.setText("(CONTROL (C))");
        hintText.setLocalTranslation(20, 50, 1); // Bottom-left corner
        guiNode.attachChild(hintText);
    }

    private void toggleVisibility() {
        isVisible = !isVisible;
        if (isVisible) {
            showControlsText();
            hideHintText();
        } else {
            hideControlsText();
            showHintText();
        }
    }

    private void showControlsText() {
        background.setCullHint(Spatial.CullHint.Never); // Show background
        controlsText.setCullHint(Spatial.CullHint.Never); // Show controls
    }

    private void hideControlsText() {
        background.setCullHint(Spatial.CullHint.Always); // Hide background
        controlsText.setCullHint(Spatial.CullHint.Always); // Hide controls
    }

    private void showHintText() {
        if (hintText.getParent() == null) {
            guiNode.attachChild(hintText);
        }
    }

    private void hideHintText() {
        if (hintText.getParent() != null) {
            guiNode.detachChild(hintText);
        }
    }

    private String getControlsText() {
        return """
                Controls:
                Left Click: Collect consumables and pages
                Right Click: Toggle flashlight on/off
                R: Recharge flashlight with battery
                Q: Exit room/door (transition between scenes)
                WASD: Move player
                """;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        app.getInputManager().deleteMapping("ToggleControls");
        guiNode.detachChild(background);
        guiNode.detachChild(controlsText);
        guiNode.detachChild(hintText);
    }
}