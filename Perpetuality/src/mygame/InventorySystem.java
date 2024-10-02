// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mike0
 * Inventory System used to hold consumables needed to increase or decrease sanity
 */
public class InventorySystem {
    private final int MAX_CAPACITY = 3;
    private List<Item> items;

    // Constructor
    public InventorySystem() {
        items = new ArrayList<>();
    }

    // Add an item to the inventory
    public boolean addItem(Item item) {
        if (isFull()) {
            System.out.println("Cannot add \"" + item.getName() + "\". Inventory is full.");
            return false;
        }
        items.add(item);
        System.out.println("\"" + item.getName() + "\" has been added to the inventory.");
        return true;
    }

    // Check if an item exists at the given slot number (index)
    public boolean checkItemExists(int slot) {
        if (slot < 0 || slot >= items.size()) {
            System.out.println("Out of bounds.");
            return false;
        }
        if (items.get(slot - 1) == null)
        {
            System.out.println("No items");
        }
        return items.get(slot - 1) != null;
    }
    
    // Return the item's score
    public int useItem(int slot) {
        return items.get(slot - 1).getScore();
    }


    // View all items in the inventory
    public void viewInventory() {
        if (isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        System.out.println("Inventory Contents:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
    }

    // Check if inventory is full
    public boolean isFull() {
        return items.size() >= MAX_CAPACITY;
    }

    // Check if inventory is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Get current number of items
    public int getItemCount() {
        return items.size();
    }
}