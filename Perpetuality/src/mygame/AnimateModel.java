package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.io.File;
import java.io.IOException;

public class AnimateModel {
    private Node player;
    private static final String ANI_IDLE = "stand";
    private static final String ANI_WALK = "Walk";
    
    private AnimComposer animComposer;

    public AnimateModel(AssetManager assetManager, Node rootNode) {
        loadModel(assetManager, rootNode);
        setUpLighting(rootNode);
    }
    
    private void loadModel(AssetManager assetManager, Node rootNode) {
        player = (Node) assetManager.loadModel("Textures/Oto/Oto.mesh.xml");
        
        // Set the position of the player to move it out of the walls
        player.setLocalTranslation(new Vector3f(-2.5f, 1.2f, 0));
        
        // Scale the model down
        player.setLocalScale(0.25f);
        
        try {
            File file = new File("assets/Models/Oto/Oto.j3o");
            BinaryExporter exporter = BinaryExporter.getInstance();
            exporter.save(player, file);
        } catch (IOException e) {
            System.out.println("Unable to save j3o file: " + e.getMessage());
        }
        rootNode.attachChild(player);
        
        animComposer = player.getControl(AnimComposer.class);
        animComposer.setCurrentAction(ANI_IDLE);
    }
    
    private void setUpLighting(Node rootNode) {
        //DirectionalLight sun = new DirectionalLight();
        //sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f));
        //sun.setColor(ColorRGBA.White.mult(2));
        //rootNode.addLight(sun);
    }
    
    public void startWalking() {
        if (animComposer != null && !ANI_WALK.equals(animComposer.getCurrentAction().toString())) {
            animComposer.setCurrentAction(ANI_WALK);
        }
    }
    
    public void stopWalking() {
        if (animComposer != null) {
            animComposer.setCurrentAction(ANI_IDLE);
        }
    }
    
    public void onAnimComplete(String animationName) {
        System.out.println(animComposer.getSpatial().getName() + " completed one " + animationName + " loop.");
    }
}