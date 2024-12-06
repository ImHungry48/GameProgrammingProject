package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.audio.AudioNode;

public class GameState extends AbstractAppState {
    private float health = 100;
    private Node rootNode;
    
    private Vector3f playerPosition;
    private Quaternion playerOrientation;
    private Set<String> collectedPages = new HashSet<>();
    private Map<String, Object> sceneData = new HashMap<>();
    private SimpleApplication app;
    private SimplifiedInventorySystem inventory;

    private boolean gameOver = false;
    private int requiredNumPages = 2;
    private boolean isSafe = false;

    // Health Bar Components
    private Geometry healthBarBackground;
    private Geometry healthBarForeground;
    private float maxHealthBarWidth = 200; // Maximum width in pixels
    private float healthBarHeight = 20;    // Height in pixels
    private BitmapText healthText;
    
    private boolean backgroundMusicEnabled = false;
    private AudioNode backgroundMusic;
    
    private GameManager gameManager;

    public GameState(GameManager gameManager) {
        this.inventory = new SimplifiedInventorySystem();
        this.gameManager = gameManager;
        backgroundMusic = new AudioNode(this.gameManager.getAssetManager(), "Sound/horror-background-atmosphere.wav", false);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.inventory.initialize(stateManager, app);

        
        stateManager.attach(this.inventory);
        

        // Initialize Health Bar
        initHealthBar();
    }
    
    @Override
    public void update(float tpf) {
        if (!gameOver) {
            if ((this.health > 0 && !inventory.getFlashLight().checkIfOn())) {
                this.health -= .01f;
                updateHealthBar();
            }

            if (this.health <= 0) {
                this.health = 0;
                gameOver = true;
                displayGameOverScreen(false); // Player lost
            }

            if (checkPagesGathered()) {
                displayGameOverScreen(true); // Player gathered all pages and won
            }
        }
    }
    
    public void increaseHealth(int value) {
        if (this.health < 100) {
            this.health += value;
            if (this.health > 100) this.health = 100;
            updateHealthBar();
        }
    }

    public void decreaseHealth(int value) {
        if (this.health > 0) {
            this.health -= value;
            if (this.health < 0) this.health = 0;
            updateHealthBar();
            if (this.health == 0) {
                gameOver = true;
                // Handle game over logic here
            }
        }
    }
    
    public boolean checkPagesGathered() {
        int pages = getInventory().getPagesCount();
        if (pages == requiredNumPages) {
            gameOver = true;
            return true;
        }
        return false;
    }
    
    public void enableBackgroundMusic() {
        backgroundMusicEnabled = true;

        // Make sure the background music is non-positional and loops indefinitely
        backgroundMusic.setPositional(false);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.3f); // Adjust volume as needed

        // Start playing the background music
        backgroundMusic.play();
    }
    
    public void disableBackgroundMusic() {
        if (!backgroundMusicEnabled) return;
        
        backgroundMusic.setPositional(false);
        backgroundMusic.setLooping(false);
        backgroundMusic.setVolume(0f);
        
        backgroundMusic.pause();
    }
    
    public void displayGameOverScreen(boolean isWin) {
        // Create a transparent background
        Quad backgroundQuad = new Quad(app.getCamera().getWidth(), app.getCamera().getHeight());
        Geometry background = new Geometry("GameOverBackground", backgroundQuad);
        Material bgMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f)); // Semi-transparent black
        background.setMaterial(bgMat);
        background.setLocalTranslation(0, 0, 0);
        disableBackgroundMusic();
        app.getGuiNode().attachChild(background);

        // Display the Win/Lose Message
        BitmapFont guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        BitmapText gameOverText = new BitmapText(guiFont, false);
        gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        gameOverText.setColor(ColorRGBA.White);

        if (isWin) {
            gameOverText.setText("You Win!");
        } else {
            gameOverText.setText("Game Over! You Lose!");
        }

        // Center the text
        gameOverText.setLocalTranslation(
            (app.getCamera().getWidth() - gameOverText.getLineWidth()) / 2,
            app.getCamera().getHeight() / 2,
            1
        );
        app.getGuiNode().attachChild(gameOverText);
        inventory.cleanUI();
        app.getGuiNode().detachChild(healthBarBackground);
        app.getGuiNode().detachChild(healthBarForeground);
        app.getGuiNode().detachChild(healthText);
        inventory.getFlashLight().cleanUI();
    }


    @Override
    public void cleanup() {
        // Optional cleanup logic
        app.getGuiNode().detachChild(healthBarBackground);
        app.getGuiNode().detachChild(healthBarForeground);
        app.getGuiNode().detachChild(healthText);
    }

    public SimplifiedInventorySystem getInventory() {
        return inventory;
    }
    
    public float getHealth() {
        return this.health;
    }
    public void addHealth() {
        health += 10;
    }

    private void initHealthBar() {
        // Create Background Bar
        Quad backgroundQuad = new Quad(maxHealthBarWidth, healthBarHeight);
        healthBarBackground = new Geometry("HealthBarBackground", backgroundQuad);
        Material bgMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", ColorRGBA.DarkGray);
        healthBarBackground.setMaterial(bgMat);
        healthBarBackground.setLocalTranslation(app.getCamera().getWidth() - maxHealthBarWidth - 10, 30, 0);  // Bottom-right position with padding
        app.getGuiNode().attachChild(healthBarBackground);

        // Create Foreground Bar
        Quad foregroundQuad = new Quad(maxHealthBarWidth, healthBarHeight);
        healthBarForeground = new Geometry("HealthBarForeground", foregroundQuad);
        Material fgMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        fgMat.setColor("Color", ColorRGBA.Green);
        healthBarForeground.setMaterial(fgMat);
        healthBarForeground.setLocalTranslation(app.getCamera().getWidth() - maxHealthBarWidth - 10, 30, 1);  // Bottom-right position
        app.getGuiNode().attachChild(healthBarForeground);

        // Initialize Health Text
        BitmapFont guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        healthText = new BitmapText(guiFont, false);
        healthText.setSize(guiFont.getCharSet().getRenderedSize());
        healthText.setColor(ColorRGBA.White);
        healthText.setText("Health: " + (int) health);
        healthText.setLocalTranslation(app.getCamera().getWidth() - 150, 30, 2);  // Adjust text position near the health bar
        app.getGuiNode().attachChild(healthText);
    }

    public void updateHealthBar() {
        float healthRatio = this.health / 100f; // Assuming max health is 100
        float newWidth = maxHealthBarWidth * healthRatio;

        // Update Foreground Geometry
        Quad newForegroundQuad = new Quad(newWidth, healthBarHeight);
        healthBarForeground.setMesh(newForegroundQuad);

        // Update Foreground Color Based on Health
        Material fgMat = healthBarForeground.getMaterial();
        if (healthRatio > 0.6f) {
            fgMat.setColor("Color", ColorRGBA.Green);
        } else if (healthRatio > 0.3f) {
            fgMat.setColor("Color", ColorRGBA.Yellow);
        } else {
            fgMat.setColor("Color", ColorRGBA.Red);
        }

        // Update Health Text
        healthText.setText("Health: " + (int) this.health);
    }
    
    public Vector3f getPlayerPosition() {
        return playerPosition;
    }
    
    public void setPlayerPosition(Vector3f position) {
        this.playerPosition = position.clone();
    }
    
    public Quaternion getPlayerOrientation() {
        return playerOrientation;
    }
    
    public void setPlayerOrientation(Quaternion orientation) {
        this.playerOrientation = orientation.clone();
    }
    
    public void addCollectedPage(String page) {
        collectedPages.add(page);
    }
    
    public boolean isPageCollected(String page) {
        return collectedPages.contains(page);
    }
    
    public void saveSceneData(String key, Object value) {
        sceneData.put(key, value);
    }
    
    public Object getSceneData(String key) {
        return sceneData.get(key);
    }
}
