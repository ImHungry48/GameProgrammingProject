// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

/**
 *
 * @author mike0
 * Consumable that can increase or decrease sanity
 */
// Item.java
public class Item {
    private String name;
    private String description;
    private int score;

    // Constructor
    public Item(String name, String description, int score) {
        this.name = name;
        this.description = description;
    }

    // Get the item's name
    public String getName() {
        return name;
    }

    // Get the item's description
    public String getDescription() {
        return description;
    }
    
    // Get the item's score
    public int getScore() {
        return score;
    }

}

