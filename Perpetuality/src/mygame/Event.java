/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

/**
 *
 * @author alais
 */
public class Event {
    private String name;
    private String description;
    private boolean isTriggered;
    private Runnable consequence;
    
    public Event(String name, String description, Runnable consequence) {
        this.name = name;
        this.description = description;
        this.isTriggered = false;
        this.consequence = consequence;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    private boolean isTriggered() {
        return this.isTriggered;
    }
    
    public void resetEvent() {
        this.isTriggered = false;
    }
}
