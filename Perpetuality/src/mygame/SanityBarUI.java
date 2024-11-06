/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;

/**
 *
 * @author alais
 */

public class SanityBarUI extends AbstractAppState {
    private final SimpleApplication app;
    private final Node guiNode;
    
    private final float maxSanity = 100f;
    private float currentSanity;
    

    // UI elements
    private BitmapText sanityText;
    private Geometry sanityBar;
    private float barWidth = 200; // Width of the sanity bar
    private float barHeight = 20; // Height of the sanity bar
    
    public SanityBarUI(Application app) {
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
        this.currentSanity = maxSanity; // Set initial sanity to maximum
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Create and initialize the sanity bar
        initSanityBar();
        initTextDisplay();
    }

    private void initSanityBar() {
        // Create the bar background
        Quad quad = new Quad(barWidth, barHeight);
        sanityBar = new Geometry("SanityBar", quad);

        // Create a material for the bar (color it green)
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        sanityBar.setMaterial(mat);

        // Get the screen width and height
        int screenWidth = app.getContext().getSettings().getWidth();
        int screenHeight = app.getContext().getSettings().getHeight();

        // Position the bar in the bottom-right corner with a margin
        float margin = 20f; // Margin from the edges of the screen
        sanityBar.setLocalTranslation(screenWidth - barWidth - margin, margin, 0);
        sanityBar.setQueueBucket(RenderQueue.Bucket.Gui);
        
        // Attach the bar to the GUI node
        guiNode.attachChild(sanityBar);
    }
    
    private void initTextDisplay() {
        BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
    
        // Sanity Text Display
        sanityText = new BitmapText(font, false);
        sanityText.setSize(font.getCharSet().getRenderedSize());
        sanityText.setColor(ColorRGBA.White);

        // Position the text above the sanity bar
        int screenWidth = app.getContext().getSettings().getWidth();
        float margin = 20f;
        sanityText.setLocalTranslation(screenWidth - barWidth - margin, barHeight + margin + 25, 0);

        guiNode.attachChild(sanityText);
    }

    @Override
    public void update(float tpf) {
        // Update the width of the bar based on current sanity
        updateSanityBar();
        // updateTextDisplay();
    }

    public void setSanity(float sanity) {
        this.currentSanity = Math.max(0, Math.min(sanity, maxSanity)); // Clamp between 0 and maxSanity
    }

    private void updateSanityBar() {
        // Scale the bar width based on current sanity
        float sanityPercent = currentSanity / maxSanity;
        sanityBar.setLocalScale(sanityPercent, 1, 1);
    }
    
    private void updateTextDisplay() {
        sanityText.setText("Sanity: " + (int) currentSanity + "/" + (int) maxSanity);
    }
}

