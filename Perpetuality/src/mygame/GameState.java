// Michael Kim, Alaisha Barber, Chenjia Zhang
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
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

/**
 *
 * @author mike0
 */
public class GameState extends AbstractAppState {
    private float health = 100;
    private Node rootNode;
    
    private Vector3f playerPosition;
    private Quaternion playerOrientation;
    private Set<String> collectedPages = new HashSet<>();
    private Map<String, Object> sceneData = new HashMap<>();

    
    public GameState() {
        
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
    }

    @Override
    public void update(float tpf) {
        //TODO: add update code
        
        // Constantly decrease health 
        if (this.health > 0) {
            this.health -= .1;
        }
        
    }
    
    // Increase health
    public void increaseHealth(int value) {
        if (this.health < 100) {
            this.health += value;
        }
    }
    
    // Decrease health
    public void decreaseHealth(int value) {
        if (this.health > 0) {
            this.health -= value;
        }
    }
    
    // Increase or decrease health
    public void applyHealth(int value) {
        if (this.health + value <= 100) {
            this.health += value;
        }
    }
    
    // Current Health Getter
    public float getHealth() {
        return this.health;
    }
    
    
    @Override
    public void cleanup() {
        // Optional cleanup logic
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

