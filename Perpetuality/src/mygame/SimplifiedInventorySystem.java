package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;

public class SimplifiedInventorySystem extends AbstractAppState {

    private SimpleApplication app;
    private Node guiNode;
    private AssetManager assetManager;
    private BitmapFont guiFont;
    private GameState gameState;
    private FlashlightSystem flashLight;

    // Inventory data
    private int consumablesCount = 0;
    private int pagesCount = 0;
    private int batteriesCount = 2;

    // UI elements
    private BitmapText consumablesText;
    private BitmapText pagesText;
    private BitmapText batteriesText;
    
    public SimplifiedInventorySystem(GameState gameState) {
        this.gameState = gameState;
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        
        guiNode = this.app.getGuiNode();
        assetManager = this.app.getAssetManager();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        // Initialize UI
        
        initUI();
        
        // Initialize Flashlight
        flashLight = new FlashlightSystem(gameState);
        flashLight.initialize(stateManager, app);
        
        stateManager.attach(flashLight);
        
        // Register input mapping for the E key
        app.getInputManager().addMapping("UseConsumable", new KeyTrigger(KeyInput.KEY_E));
        app.getInputManager().addListener(actionListener, "UseConsumable");
        
        // Register input mapping for the R key
        app.getInputManager().addMapping("UseBattery", new KeyTrigger(KeyInput.KEY_R));
        app.getInputManager().addListener(actionListener, "UseBattery");
        

    }
    
    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        if (!isPressed) {
            return; // Ignore key release events
        }

        if (name.equals("UseConsumable")) {
            useItem("consumable", 1);
        } else if (name.equals("UseBattery")) {
            useItem("battery", 1);
        }
    };


    private void initUI() {
        System.out.println("Initializing UI...");

        int screenHeight = app.getViewPort().getCamera().getHeight();

        // Load the font
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        // Check if consumablesText already exists before creating
        if (consumablesText == null) {
            consumablesText = createText("Consumables: " + consumablesCount, 10, screenHeight - 10);
            guiNode.attachChild(consumablesText); // Attach only once
        } else {
            System.out.println("ConsumablesText already initialized.");
        }

        if (pagesText == null) {
            pagesText = createText("Pages: " + pagesCount, 10, screenHeight - 30);
            guiNode.attachChild(pagesText);
        }

        if (batteriesText == null) {
            batteriesText = createText("Batteries: " + batteriesCount, 10, screenHeight - 50);
            guiNode.attachChild(batteriesText);
        }

        System.out.println("UI Initialized.");
    }


    
    

    private BitmapText createText(String text, float x, float y) {
        BitmapText bitmapText = new BitmapText(guiFont, false);
        bitmapText.setSize(guiFont.getCharSet().getRenderedSize());
        bitmapText.setLocalTranslation(x, y, 0);
        bitmapText.setText(text);
        return bitmapText;
    }

    public void addItem(String itemType) {
        if (itemType == null || itemType.isEmpty()) {
            System.out.println("Invalid item type.");
            return;
        }

        switch (itemType.toLowerCase()) {
            case "consumable" -> consumablesCount += 1;
            case "page" -> pagesCount += 1;
            case "battery" -> batteriesCount += 1;
            default -> System.out.println("Unknown item type: " + itemType);
        }

        updateUI();
    }

    public void useItem(String itemType, int amount) {
        if (itemType == null || itemType.isEmpty()) {
            System.out.println("Invalid item type.");
            return;
        }

        switch (itemType.toLowerCase()) {
            case "consumable" -> {
                if (consumablesCount >= amount) {
                    consumablesCount -= amount;
                    // Invoke increasing gamestate sanity
                    gameState.addHealth();
                } else {
                    System.out.println("Not enough consumables to use.");
                }
            }
            case "battery" -> {
                if (batteriesCount >= amount) {
                    batteriesCount -= amount;
                    // Invoke increasing battery charge
                    flashLight.addCharge();
                } else {
                    System.out.println("Not enough batteries to use.");
                }
            }
            default -> System.out.println("Unknown item type: " + itemType);
        }

        updateUI();
    }

    private void updateUI() {
        // Update the text of the consumable counter instead of creating new BitmapText objects
        consumablesText.setText("Consumables: " + consumablesCount);
        pagesText.setText("Pages: " + pagesCount);
        batteriesText.setText("Batteries: " + batteriesCount);
    }

    public int getPagesCount() {
        return pagesCount;
    }
    
    public FlashlightSystem getFlashLight() {
        return flashLight;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        guiNode.detachAllChildren();
    }
}
