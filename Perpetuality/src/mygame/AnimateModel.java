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


import com.jme3.input.*;
import com.jme3.input.controls.*;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class AnimateModel extends SimpleApplication {
    //Class Fields
    private Node player;
    private static final String ANI_IDLE = "stand";
    private static final String ANI_WALK = "Walk";
    
    private final static Trigger TRIGGER_WALK = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static String MAPPING_WALK = "Walk";
    
    AnimComposer animComposer;
    
   

    public static void main(String[] args) {
        AnimateModel app = new AnimateModel();
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
        animComposer.setCurrentAction("stand");
        
        
        // Add sun to rootnode
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)));
        sun.setColor(ColorRGBA.White.mult(2));
        rootNode.addLight(sun);
        
        
        
        // Play an animated model
        inputManager.addMapping(MAPPING_WALK, TRIGGER_WALK);
        inputManager.addListener(actionListener, new String[] {MAPPING_WALK});
        inputManager.addListener(analogListener, MAPPING_WALK);
        
        // replace walk action with original walk action + doneTween
        Action walk = animComposer.action(ANI_WALK);
        Tween doneTween = Tweens.callMethod(AnimateModel.this, "onAnimComplete", ANI_WALK);
        animComposer.actionSequence(ANI_WALK, walk, doneTween);
    }
    
    public void onAnimComplete(String animationName) {
        System.out.println(animComposer.getSpatial().getName() + " completed one " + animationName + " loop.");
    }
    
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_WALK) && isPressed) {
                if (!animComposer.getCurrentAction().toString().contains(ANI_WALK)) {
                  animComposer.setCurrentAction(ANI_WALK);
                }
            }
            if (name.equals(MAPPING_WALK) && !isPressed) {
            animComposer.setCurrentAction(ANI_IDLE);
          }
        }
    };
    
    private final AnalogListener analogListener = new AnalogListener() {
         @Override
         public void onAnalog(String name, float intensity, float tpf) {
           if (name.equals(MAPPING_WALK) ) {
             player.move(0, 0, tpf);
            } 
         }      
    };

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
