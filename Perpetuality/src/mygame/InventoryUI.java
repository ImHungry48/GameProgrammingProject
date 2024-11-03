package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;

public class InventoryUI extends AbstractAppState {
    private final InventorySystem inventory;
    private final BitmapText[] inventoryTextSlots;
    private Node guiNode;
    private BitmapFont font;

    public InventoryUI(InventorySystem inventory) {
        this.inventory = inventory;
        this.inventoryTextSlots = new BitmapText[3];
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Cast app to SimpleApplication to access guiNode
        SimpleApplication simpleApp = (SimpleApplication) app;
        guiNode = simpleApp.getGuiNode();
        font = simpleApp.getAssetManager().loadFont("Interface/Fonts/Default.fnt");

        // Initialize each slot's text UI
        for (int i = 0; i < inventoryTextSlots.length; i++) {
            inventoryTextSlots[i] = new BitmapText(font);
            inventoryTextSlots[i].setSize(font.getCharSet().getRenderedSize());
            inventoryTextSlots[i].setText("N");  // Default text for empty slot
            inventoryTextSlots[i].setLocalTranslation(10, (i + 1) * 20, 0);  // Position each slot below the previous
            guiNode.attachChild(inventoryTextSlots[i]);
        }

        // Initial update for the UI to reflect current inventory state
        updateInventoryUI();
    }

    public void addItem(Item item) {
        if (inventory.addItem(item)) {
            updateInventoryUI();
        }
    }

    public void useItem(int slot) {
        inventory.useItem(slot);
        updateInventoryUI();
    }

    private void updateInventoryUI() {
        // Loop through inventory slots and update display text
        for (int i = 0; i < inventoryTextSlots.length; i++) {
            if (inventoryTextSlots[i] != null) {  // Null check to prevent NullPointerException
                Item item = inventory.getItem(i + 1);  // Slots are 1-indexed in InventorySystem
                inventoryTextSlots[i].setText(item != null ? item.getName().substring(0, 1) : "N");
            }
        }
    }


    @Override
    public void cleanup() {
        super.cleanup();
        // Remove text elements when the UI is detached
        for (BitmapText text : inventoryTextSlots) {
            guiNode.detachChild(text);
        }
    }
}
