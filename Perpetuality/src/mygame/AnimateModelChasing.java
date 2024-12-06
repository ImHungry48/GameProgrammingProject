package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.animation.AnimChannel;
import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.scene.Spatial;
import java.io.File;
import java.io.IOException;
import com.jme3.renderer.Camera;


import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.AmbientLight;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class AnimateModelChasing extends SimpleApplication {
    //Class Fields
    private Node player;
    private static final String ANI_IDLE = "stand";
    private static final String ANI_WALK = "Walk";
    
    
    AnimComposer animComposer;
    
    private Vector3f fromEnemy;
    private final Vector3f updirection = new Vector3f(0,1,0);
    
    AmbientLight light;
   

    public static void main(String[] args) {
        AnimateModelChasing app = new AnimateModelChasing();
        app.start();
        
    }

    @Override
    public void simpleInitApp() { 
        // Convert .xml to j3o and then Load an animated Model
        player = (Node) assetManager.loadModel("Textures/Oto/Oto.mesh.xml");
        try {
            File file = new File("assets/Models/Oto/Oto.j3o");
            BinaryExporter exporter = BinaryExporter.getInstance();
            exporter.save(player, file);
        } catch (IOException e) {
            System.out.println("Unable to save j3o file: " + e.getMessage());
        }
        rootNode.attachChild(player);
        
        // Control the animations using AnimComposer
        // NOTICE: control is just animComposer
        // AnimComposer animComposer = player.getControl(AnimComposer.class);
        animComposer = player.getControl(AnimComposer.class);
        // animComposer.setCurrentAction("stand");
        animComposer.setCurrentAction(ANI_WALK);
        
        // Add sun to rootnode
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)));
        sun.setColor(ColorRGBA.White.mult(2));
        rootNode.addLight(sun);
        
        
        light = new AmbientLight();
        rootNode.addLight(light);
        
        
        // replace walk action with original walk action + doneTween
        Action walk = animComposer.action(ANI_WALK);
        Tween doneTween = Tweens.callMethod(AnimateModelChasing.this, "onAnimComplete", ANI_WALK);
        animComposer.actionSequence(ANI_WALK, walk, doneTween);
        
        
        flyCam.setMoveSpeed(50f);
    }
    
    public void onAnimComplete(String animationName) {
        System.out.println(animComposer.getSpatial().getName() + " completed one " + animationName + " loop.");
    }
    

    @Override
    public void simpleUpdate(float tpf) {
        
        //TODO: add update code
        fromEnemy = new Vector3f(cam.getLocation()).subtractLocal( player.getWorldTranslation() ).normalizeLocal();
        player.move(fromEnemy.mult(tpf * 5));

        // Set Enemy to look at and move in camera direction 
        player.lookAt(cam.getLocation(), updirection);
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
