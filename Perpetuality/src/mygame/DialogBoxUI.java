package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
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
    
    private String fullText = ""; // Full dialog text
    private int currentCharIndex = 0; // Current character index to display
    private float charDisplayInterval = 0.05f; // Time interval between characters
    private float charDisplayElapsed = 0; // Time elapsed since last character display
    private AudioNode typeSound; // Sound effect for character typing    
    
    // Shaking animation
    private boolean isShaking = false;
    private float shakeTimeElapsed = 0;
    private float shakeDuration = 1.0f;
    private float shakeIntensity = 5.0f;
    private float originalX, originalY;
    
    private boolean finishedDisplaying;

    public DialogBoxUI(Application app) {
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
        this.finishedDisplaying = false;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Create and initialize the dialog box components
        initDialogBackground();
        initDialogText();
        initTypeSound();
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
        dialogBackground.setLocalTranslation((screenWidth - boxWidth) / 2, 20, 0);

        guiNode.attachChild(dialogBackground);
    }

    private void initDialogText() {
        BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Alegreya.fnt");

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
    
    public void initTypeSound() {
        typeSound = new AudioNode(app.getAssetManager(), "Sound/text-sound.wav", false);
        typeSound.setPositional(false);
        typeSound.setLooping(false);
        typeSound.setVolume(1.0f);
    }

    public void showDialog(String text, float sizeMultiplier, boolean shake) {        
        fullText = text;
        currentCharIndex = 0;
        charDisplayElapsed = 0;
        finishedDisplaying = false;
        
        dialogText.setText("");
        
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
        
        if (isVisible && currentCharIndex < fullText.length()) {
            charDisplayElapsed += tpf;

            if (charDisplayElapsed >= charDisplayInterval && currentCharIndex < fullText.length()) {
                // Reveal the next character
                dialogText.setText(fullText.substring(0, currentCharIndex + 1));
                currentCharIndex++;

                // Play type sound
                typeSound.playInstance();

                // Reset timer
                charDisplayElapsed = 0;
            }
            
            if (currentCharIndex >= fullText.length()) {
                finishedDisplaying = true;
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
    
    public boolean isFinishedDisplaying() {
        return this.finishedDisplaying;
    }

}
