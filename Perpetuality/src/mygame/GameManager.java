/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author mike0
 */
public class GameManager extends SimpleApplication {    
    private final static Trigger TRIGGER_CHANGEHEALTH = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_CHANGEHEALTH = "Change Health";
    private final static String MAPPING_ROTATE = "Rotate";
    
    private GameState gameState;
    
    // Mesh potentially
    private static Box mesh = new Box(Vector3f.ZERO, 1, 1, 1);
    
    // There are some cache related issues, need to ask
     private ArrayList<Geometry> good_geoms;
     private ArrayList<Geometry> bad_geoms;
    // Solution for now: just have a few interactable objects

  
    
    // Field for Game Logic
    @Override
    public void simpleInitApp() {
        inputManager.addMapping(MAPPING_CHANGEHEALTH, TRIGGER_CHANGEHEALTH);
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addListener(actionListener, new String[] {MAPPING_CHANGEHEALTH});
        inputManager.addListener(analogListener, new String[] {MAPPING_ROTATE});
        
        // Cache Issue
        good_geoms = new ArrayList<>();
        bad_geoms = new ArrayList<>();
        
        // Just as an idea, maybe grades should be the threshold for which we have different status
        
        // Fill the scene with some randomly positioned and randomly colored cubes
        makeGoodCubes(10);
        makeBadCubes(10);
        
        Vector3f loc1 = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50) 
        );
        
        Vector3f loc2 = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50) 
        );
        
        // Current Approach is limited
        rootNode.attachChild(myBox("Good Box",
                loc1, ColorRGBA.Green));
        
        rootNode.attachChild(myBox("Bad Box", 
                loc2, ColorRGBA.Red));

        gameState = new GameState();
        stateManager.attach(gameState);
        
        // To make camera runs faster 
        flyCam.setMoveSpeed(50f);
        
        
        /* SCENE LOADING */
        SceneLoader sceneLoader = new SceneLoader(assetManager, rootNode);
        sceneLoader.loadScene("Scenes/Bathroom.j3o");
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1.0f, -0.5f).normalizeLocal());  // Direction of the light
        sun.setColor(ColorRGBA.White);  // Color of the light
        rootNode.addLight(sun);  // Attach the light to the rootNode

        attachCenterMark();
    } 
    
    private void attachCenterMark() {
        Geometry c = myBox("center mark", 
            Vector3f.ZERO, ColorRGBA.White);
        c.scale(4); 
        c.setLocalTranslation( settings.getWidth()/2,
        settings.getHeight()/2, 0 ); 
        guiNode.attachChild(c); // attach to 2D user interface
    }
    
    private void makeGoodCubes(int number) {
        for (int i = 0; i < number; i++) {
            Vector3f loc = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50)
            );
            
            // Add the good geom
            Geometry goodgeom = myBox("GoodBox" + i, loc, ColorRGBA.Yellow);
            goodgeom.addControl(new ChangeHealthBarControl(cam, rootNode));
            rootNode.attachChild(goodgeom);
            
            // Add the goodgeom to good_geoms
            // Cache ISSUE
             good_geoms.add(goodgeom);
            
            
        }
    }
    
    private void makeBadCubes(int number) {
        for (int i = 0; i < number; i++) {
            Vector3f loc = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50) 
            );
            
            // Add the bad geom
            Geometry badgeom = myBox("BadBox" + i, loc, ColorRGBA.Pink);
            badgeom.addControl(new ChangeHealthBarControl(cam, rootNode));
            rootNode.attachChild(badgeom);
            
            // Add the badgeom to bad_geoms
            // Cache ISSUE
            bad_geoms.add(badgeom);
        }
    }
        
    
    public Geometry myBox(String name, Vector3f loc, ColorRGBA color) {
        Geometry local_geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        local_geom.setMaterial(mat);
        local_geom.setLocalTranslation(loc);
        return local_geom;
    }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_CHANGEHEALTH) && !isPressed) {
                // implement action here
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);
                
                
                if (results.size() > 0) {
                    Geometry target = results.getClosestCollision().getGeometry();
                    // implement action here
                    if (target.getName().equals("Good Box")) {
                        gameState.increaseHealth(10);
                        target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                    } else if (target.getName().equals("Bad Box")) {
                        gameState.decreaseHealth(10);
                        target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                    }
                    
                    if (good_geoms.contains(target)) {
                        gameState.increaseHealth(10);
                        target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                        good_geoms.remove(target); // delete the obj instance as a good geom
                        // 
                    } else if(bad_geoms.contains(target)) {
                        gameState.decreaseHealth(10);
                        target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                        bad_geoms.remove(target);
                        // delete the obj instance as a bad geom
                    }
                } else {
                    System.out.println("Selection: Nothing");
                }
               
            }
            // System.out.println("You triggered: "+name);
           
        }
    };
    
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            if (name.equals(MAPPING_ROTATE)) {
                // implement action here
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);
                /* For printing out result, but don't need this
                for (int i = 0; i < results.size(); i++){
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String target = results.getCollision(i).getGeometry().getName();
                    System.out.println("Selection: #" + i + ": " + target +
                    " at " + pt + ", " + dist + " WU away.");
                } */ 
                if (results.size() > 0) {
                    Geometry target = results.getClosestCollision().getGeometry();
                    // implement action here
                    if (target.getName().equals("Good Box")) {
                        target.rotate(tpf,tpf,tpf);
                    } else if (target.getName().equals("Bad Box")) {
                        target.rotate(tpf,tpf,tpf);
                    }
                    
                    //----------------------------------
                    
                    //Cache issue
                    if (good_geoms.contains(target)) {
                        // the good geom rotates as if being selected, but no effects on health yet (just like an inspection)
                        // future Implementation: might have a text box pop out with instructions about the interaction with the object
                        target.rotate(tpf,tpf,tpf);
                    } else if(bad_geoms.contains(target)) {
                        target.rotate(tpf,tpf,tpf);
                    }
                } else {
                    System.out.println("Selection: Nothing");
                }
            }
            // System.out.println("You triggered: "+name);
        }
   };
    

    @Override
    public void simpleUpdate(float tpf) {
        
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}