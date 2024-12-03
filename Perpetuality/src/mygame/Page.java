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
    private SceneManager sceneManager;
    private AudioNode collectSound;
    
    
    public Page(Spatial pageSpatial,
            SimplifiedInventorySystem simplifiedInventorySystem) {
        this.hasCollected = false;
        
        PageControl pageControl = new PageControl(this);
        pageSpatial.addControl(pageControl);
    }
    
    private void initialize(AssetManager assetManager) {
        // Load the spatial representing the page
        pageSpatial = assetManager.loadModel("Models/ExamPage/ExamPage.j3o");
        pageSpatial.setName("Page");
        pageSpatial.setLocalTranslation(location);
        rootNode.attachChild(pageSpatial);

        // Set user data to link back to this Page instance
        PageControl pageControl = new PageControl(this);
        pageSpatial.addControl(pageControl);
    }
    
    public boolean checkCollected() {
        return this.hasCollected;
    }
    
    public void collectPage() {
        if (this.hasCollected) return;
        
        this.hasCollected = true;
        // Remove the page from the scene
        pageSpatial.removeFromParent();

        // Update inventory
        inventorySystem.addItem("page");
    }
    
    public Spatial getPageSpatial() {
        return pageSpatial;
    }
    
//    private void setupAudio() {
//        collectSound = new AudioNode(app.getAssetManager(), "Sound/", false);
//        collectSound.setPosition(false);
//        collectSound.setLooping(false);
//        collectSound.setVolume(3);
//        app.getRootNode().attachChild(collectSound);
//    }
}
