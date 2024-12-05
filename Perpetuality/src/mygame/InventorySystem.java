package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import java.util.Arrays;

/**
 * Manages both the inventory and its UI display.
 */
public class InventorySystem extends AbstractAppState {
    private static final int MAX_CAPACITY = 3;
    private final Item[] items;  // Inventory items array
    private final BitmapText[] inventoryTextSlots; // UI text slots
    private Node guiNode;
    private BitmapFont font;

    // Constructor
    public InventorySystem() {
        items = new Item[MAX_CAPACITY];
        Arrays.fill(items, null); // Initialize each slot to null
        inventoryTextSlots = new BitmapText[MAX_CAPACITY];
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Setup GUI node and font
        SimpleApplication simpleApp = (SimpleApplication) app;
        guiNode = simpleApp.getGuiNode();
        font = simpleApp.getAssetManager().loadFont("Interface/Fonts/Default.fnt");

        // Initialize each inventory slot's text UI
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

    // Add an item to the inventory and update UI
    public boolean addItem(Item item) {
        for (int i = 0; i < MAX_CAPACITY; i++) {
            if (items[i] == null) {
                items[i] = item;
                System.out.println("\"" + item.getName() + "\" has been added to the inventory.");
                updateInventoryUI(); // CHANGE: Comment <Should update this to dialog textbox later>
                return true;
            }
        }
        // CHANGE: Comment <Should update this to dialog textbox later>
        System.out.println("Cannot add \"" + item.getName() + "\". Inventory is full.");
        return false;
    }

    // Use an item in the inventory and update UI
    public int useItem(int slot) {
        Item item = getItem(slot);
        if (item == null) {
            System.out.println("No item in slot " + slot);
            return 0;
        }
        int score = 0;
        if ("Consumable".equals(item.getType())) {
            score = useConsumable(slot);
        } else if ("Key Item".equals(item.getType())) {
            score = useKeyItem(slot);
        }
        // Remove item from inventory and update UI
        items[slot - 1] = null;
        updateInventoryUI();
        return score;
    }

    // Check if an item exists at the given slot number (index)
    public boolean checkItemExists(int slot) {
        return items[slot - 1] != null;
    }

    public Item getItem(int slot) {
        return items[slot - 1];
    }

    // Return the item's score
    public int useConsumable(int slot) {
        System.out.println("Item consumed");
        return items[slot - 1].getScore();
    }

    public int useKeyItem(int slot) {
        // Trigger event for key item
        return 0;
    }

    // Update the inventory UI based on current items
    private void updateInventoryUI() {
        for (int i = 0; i < inventoryTextSlots.length; i++) {
            if (inventoryTextSlots[i] != null) { // Null check to prevent NullPointerException
                Item item = items[i];
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
