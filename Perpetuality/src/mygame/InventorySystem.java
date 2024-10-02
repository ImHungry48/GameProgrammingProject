// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mike0
 * Inventory System used to hold consumables needed to increase or decrease sanity
 */
public class InventorySystem {
    private final int MAX_CAPACITY = 3;
    private Item[] items;

    // Constructor
    public InventorySystem() {
        items = new Item[MAX_CAPACITY];
        Arrays.fill(items, null); // Initialize each slot to null
    }

    // Add an item to the inventory
    public boolean addItem(Item item) {
        for (int i = 0; i < MAX_CAPACITY; i++) {
            if (items[i] == null) {
                items[i] = item;
                System.out.println("\"" + item.getName() + "\" has been added to the inventory.");
                return true;
            }
        }
        System.out.println("Cannot add \"" + item.getName() + "\". Inventory is full.");
        return false;
    }

    // Check if an item exists at the given slot number (index)
    public boolean checkItemExists(int slot) {
        if (items[slot - 1] == null)
        {
            System.out.println("No items");
            return false;
        }
        return true;
    }
    
    // Return the item's score
    public int useItem(int slot) {
        System.out.println("item consumed");
        return items[slot - 1].getScore();
    }
}