package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

public class DialogBox {

    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final Node guiNode;
    private final ViewPort viewPort;
    private final Node rootNode;  // Add rootNode field

    private BitmapText dialogText;
    private Picture dialogFrame;
    private boolean dialogOpen;
    private static final Trigger TRIGGER_DIALOG = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private static final String MAPPING_DIALOG = "Dialog";

    // Constructor
    public DialogBox(AssetManager assetManager, InputManager inputManager, Node guiNode, ViewPort viewPort, Node rootNode) {
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.guiNode = guiNode;
        this.viewPort = viewPort;
        this.rootNode = rootNode;  // Initialize rootNode
        this.dialogOpen = false;

        initializeDialogComponents();
        setupInputMapping();
    }

    private void initializeDialogComponents() {
        // Initialize dialog text
        dialogText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"), false);
        dialogText.setSize(24);
        dialogText.setColor(ColorRGBA.White);

        // Initialize dialog frame
        dialogFrame = new Picture("DialogFrame");
        dialogFrame.setImage(assetManager, "Interface/dialogbox.jpg", false);
        dialogFrame.setWidth(1500);
        dialogFrame.setHeight(1000);
    }

    private void setupInputMapping() {
        inputManager.addMapping(MAPPING_DIALOG, TRIGGER_DIALOG);
        inputManager.addListener(actionListener, MAPPING_DIALOG);
    }

    // Show the dialog with specified text
    public void showDialog(String text) {
        if (!dialogOpen) {
            dialogText.setText(text);
            dialogText.setLocalTranslation(viewPort.getCamera().getWidth() / 2 - dialogText.getLineWidth() / 2,
                                           viewPort.getCamera().getHeight() / 2 + dialogText.getLineHeight(), 0);

            dialogFrame.setLocalTranslation(viewPort.getCamera().getWidth() / 2 - dialogFrame.getWidth() / 2,
                                            viewPort.getCamera().getHeight() / 2 - dialogFrame.getHeight() / 2, -1);

            guiNode.attachChild(dialogText);
            guiNode.attachChild(dialogFrame);
            dialogOpen = true;
        }
    }

    // Hide the dialog
    public void hideDialog() {
        if (dialogOpen) {
            guiNode.detachChild(dialogText);
            guiNode.detachChild(dialogFrame);
            dialogOpen = false;
        }
    }

    // Action listener for handling mouse clicks to open or close dialog
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_DIALOG) && !isPressed) {
                handleInteraction();
            }
        }
    };

    private void handleInteraction() {
        // Cast a ray from the camera to detect objects in rootNode, not guiNode
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(viewPort.getCamera().getLocation(), viewPort.getCamera().getDirection());
        rootNode.collideWith(ray, results);  // Use rootNode instead of guiNode

        System.out.println("handle interaction called");
        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();

            if ("InteractableBox".equals(target.getName()) && !dialogOpen) {
                // Show dialog with specified message
                showDialog("It's very dark. I need this battery to recharge the flashlight...");
            } else {
                hideDialog();
            }
        } else {
            System.out.println("Hiding");
            hideDialog();
        }
    }

    // Clean up mappings when no longer needed
    public void cleanup() {
        inputManager.deleteMapping(MAPPING_DIALOG);
        inputManager.removeListener(actionListener);
        hideDialog();
    }
}