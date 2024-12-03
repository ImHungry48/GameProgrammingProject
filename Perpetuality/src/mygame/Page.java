/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author alais
 */
public class Page {
    
    private boolean hasCollected;
    private Vector3f location;
    private Spatial pageSpatial;
    private SimplifiedInventorySystem inventorySystem;
    private Node rootNode;
    private BulletAppState bulletAppState;    
    private SceneManager sceneManager;
    private AudioNode collectSound;
    
    
    public Page(Vector3f location,
            AssetManager assetManager,
            Node rootNode,
            SimplifiedInventorySystem simplifiedInventorySystem,
            BulletAppState bulletAppState) {
        this.hasCollected = false;
        this.location = location;
        this.rootNode = rootNode;
        this.inventorySystem = inventorySystem;
        this.bulletAppState = bulletAppState;
        
        initialize(assetManager);
    }
    
    private void initialize(AssetManager assetManager) {
        //setupAudio();
        //Spatial page = assetManager.loadModel();
    }
    
    public boolean checkCollected() {
        return this.hasCollected;
    }
    
    public void collectPage() {
        if (this.hasCollected) return;
        
        this.hasCollected = true;
    }
    
//    private void setupAudio() {
//        collectSound = new AudioNode(app.getAssetManager(), "Sound/", false);
//        collectSound.setPosition(false);
//        collectSound.setLooping(false);
//        collectSound.setVolume(3);
//        app.getRootNode().attachChild(collectSound);
//    }
}
