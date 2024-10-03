// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author mike0
 */
public class GameState extends AbstractAppState {
    private float health = 100;
    private Node rootNode;

    
    public GameState() {
        
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        
        
    }

    

    @Override
    public void update(float tpf) {
        //TODO: add update code
        
        // Constantly decrease health 
        health -= 0.001;
        
    }
    
    // Increase health
    public void increaseHealth(int value) {
        health += value;
    }
    
    // Decrease health
    public void decreaseHealth(int value) {
        health -= value;
    }
    
    // Increase or decrease health
    public void applyHealth(int value) {
        health += value;
    }
    
    // Current Health Getter
    public float getHealth() {
        return health;
    }
    
    
    @Override
    public void cleanup() {
        // Optional cleanup logic
    }
}

