package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;

public class DialogBoxUI extends AbstractAppState {

    public final SimpleApplication app;
    private final Node guiNode;

    private Geometry dialogBackground;
    private BitmapText dialogText;

    private final float boxWidth = 800; // Width of the dialog box
    private final float boxHeight = 150; // Height of the dialog box
    private boolean isVisible = false;
    
    // Shaking animation
    private boolean isShaking = false;
    private float shakeTimeElapsed = 0;
    private float shakeDuration = 1.0f;
    private float shakeIntensity = 5.0f;
    private float originalX, originalY;

    public DialogBoxUI(Application app) {
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Create and initialize the dialog box components
        initDialogBackground();
        initDialogText();
        hideDialog(); // Initially hide the dialog box
    }

    private void initDialogBackground() {
        // Create a semi-transparent background for the dialog box
        Quad quad = new Quad(boxWidth, boxHeight);
        dialogBackground = new Geometry("DialogBackground", quad);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.7f)); // Semi-transparent black
        dialogBackground.setMaterial(mat);

        // Position the background at the bottom center of the screen
        int screenWidth = app.getContext().getSettings().getWidth();
        int screenHeight = app.getContext().getSettings().getHeight();
        dialogBackground.setLocalTranslation((screenWidth - boxWidth) / 2, 20, 0);

        guiNode.attachChild(dialogBackground);
    }

    private void initDialogText() {
        BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");

        // Initialize the text
        dialogText = new BitmapText(font, false);
        dialogText.setSize(font.getCharSet().getRenderedSize() + 8); // Increase text size
        dialogText.setColor(ColorRGBA.White);

        // Position the text inside the dialog box
        int screenWidth = app.getContext().getSettings().getWidth();
        dialogText.setBox(new com.jme3.font.Rectangle(0, 0, boxWidth - 20, boxHeight - 20));
        dialogText.setAlignment(BitmapFont.Align.Center);
        dialogText.setVerticalAlignment(BitmapFont.VAlign.Center);
        dialogText.setLocalTranslation((screenWidth - boxWidth) / 2 + 10, 20 + boxHeight - 10, 1);

        originalX = dialogText.getLocalTranslation().x;
        originalY = dialogText.getLocalTranslation().y;
        
        guiNode.attachChild(dialogText);
    }

    public void showDialog(String text, float sizeMultiplier, boolean shake) {
        dialogText.setText(text);
        
        BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        dialogText.setSize(font.getCharSet().getRenderedSize() * sizeMultiplier);
        
  
        dialogBackground.setCullHint(Geometry.CullHint.Never);
        dialogText.setCullHint(Geometry.CullHint.Never);
        isVisible = true;
    }

    public void hideDialog() {
        dialogBackground.setCullHint(Geometry.CullHint.Always);
        dialogText.setCullHint(Geometry.CullHint.Always);
        
        dialogText.setSize(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt").getCharSet().getRenderedSize());
        
        isShaking = false;
        isVisible = false;
    }
    
    @Override
    public void update(float tpf) {
        if (isShaking) {
            shakeTimeElapsed += tpf;
            
            if (shakeTimeElapsed > shakeDuration) {
                isShaking = false; // Stop shaking after the duration
                dialogText.setLocalTranslation(originalX, originalY, 1); // Reset position
            } else {
                // Randomized shake effect
                float offsetX = (float)(Math.random() * shakeIntensity * 2 - shakeIntensity);
                float offsetY = (float)(Math.random() * shakeIntensity * 2 - shakeIntensity);
                
                dialogText.setLocalTranslation(originalX + offsetX, originalY + offsetY, 1);
            }
        }
    }

    public boolean isDialogVisible() {
        return isVisible;
    }
    
    public void clearDialog() {
        dialogText.setText(""); // Clear the text
        dialogBackground.setCullHint(Geometry.CullHint.Always); // Hide background if needed
        dialogText.setCullHint(Geometry.CullHint.Always);       // Hide text if needed
        isVisible = false;
    }
    
    public void startShake(float duration, float intensity) {
        this.isShaking = true;
        this.shakeTimeElapsed = 0;
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
    }
    
    public void showDialogWithShake(String text, float sizeMultiplier, float shakeDuration, float shakeIntensity) {
        showDialog(text, sizeMultiplier, true);
        startShake(shakeDuration, shakeIntensity);
    }

}
