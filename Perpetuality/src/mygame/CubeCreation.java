/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author alais
 */
public class CubeCreation extends SimpleApplication {
    
    // Objects that are populated in our game
    private ArrayList<Geometry> good_interact_geoms;
    private ArrayList<Geometry> bad_interact_geoms;
    
    // Mesh potentially
    private static Box mesh = new Box(Vector3f.ZERO, 1, 1, 1);
    
    // Interactable cubes that can increase sanity
    private void makeGoodCubes(int number) {
        for (int i = 0; i < number; i++) {
            Vector3f loc = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50)
            );
            
            // Add the good geom
            Geometry goodgeom = myBox("GBox" + i, loc, ColorRGBA.Yellow);
            //goodgeom.addControl(new ChangeHealthBarControl(cam, rootNode));
            //rootNode.attachChild(goodgeom);
            
            // Add the goodgeom to good_geoms
            // Potential Cache ISSUE
            good_interact_geoms.add(goodgeom);
            
        }
    }
    
    /* public void onChangeHealth() {
        handleChangeHealth();
    }  */

    // Manage health when an object is interacted
    /*private void handleChangeHealth() {
        // implement action here
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        rootNode.collideWith(ray, results);


        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();
            // implement action here
            
            // Good Box and Bad Box are collectable objects that will be gone once clicked
            if (target.getName().equals("Good Box")) {
                gameState.increaseHealth(20);
                inventory.addItem(new Item("Apple", "A delicious apple",10, "Consumable"));
                rootNode.detachChild(target); // you have collected the apple, so it's gone
            } else if (target.getName().equals("Bad Box")) {
                gameState.decreaseHealth(10);
                rootNode.detachChild(target);
            }
            
            // Good_geoms and Bad_geoms are currently more like neutral objects that are not collectable but interactable
            if (good_interact_geoms.contains(target)) {
                gameState.increaseHealth(3);
                target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                good_interact_geoms.remove(target); // delete the obj instance as a good geom
                // 
            } else if(bad_interact_geoms.contains(target)) {
                gameState.decreaseHealth(1);
                target.getMaterial().setColor("Color", ColorRGBA.LightGray);
                bad_interact_geoms.remove(target); // delete the obj instance as a bad geom
            }
        }
    } */
    
    public void onRotate(float intensity, float tpf) {
        handleRotate(intensity, tpf);
    }
    
    public void handleRotate(float intensity, float tpf) {
        // implement action here
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        rootNode.collideWith(ray, results);
        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();
            // implement action here
            if (target.getName().equals("Good Box")) {
                target.rotate(tpf,tpf,tpf);
                System.out.println("You are selecting the good box");
                // for future work, should showcase description of the target to user
            } else if (target.getName().equals("Bad Box")) {
                target.rotate(tpf,tpf,tpf);
                System.out.println("Hmm, you are selecting the bad box");
                // for future work, should showcase description of the target to user
            }

            //----------------------------------

            //Potential Cache issue
            // Some other potentially neutral objects
            if (good_interact_geoms.contains(target)) {
                // the good geom rotates as if being selected, but no effects on health yet (just like an inspection)
                // future Implementation: might have a text box pop out with instructions about the interaction with the object
                target.rotate(tpf,tpf,tpf);
                System.out.println("This could be helpful?");
            } else if(bad_interact_geoms.contains(target)) {
                target.rotate(tpf,tpf,tpf);
                System.out.println("This could be harmful?");
            }
        } else {
            System.out.println("Selection: Nothing");
        }
        // System.out.println("You triggered: "+ name);
    }
    
        // ActionListener to handle actions
    /*private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                switch (name) {
                    case "Change Health":
                        handleChangeHealth();
                        break;
                    case "Use Item 1":
                        if (inventory.checkItemExists(1)) {
                            gameState.applyHealth(inventory.useItem(1));
                        }
                        break;
                    case "Use Item 2":
                        if (inventory.checkItemExists(2)) {
                            gameState.applyHealth(inventory.useItem(2));
                        }
                        break;
                    case "Use Item 3":
                        if (inventory.checkItemExists(3)) {
                            gameState.applyHealth(inventory.useItem(3));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };    */
    
    // Crosshair for our game :)
    private void attachCenterMark() {
        Geometry c = myBox("center mark", 
            Vector3f.ZERO, ColorRGBA.White);
        c.scale(4); 
        c.setLocalTranslation( settings.getWidth()/2,
        settings.getHeight()/2, 0 ); 
        guiNode.attachChild(c); // attach to 2D user interface
    }
    
    // Interactable cubes that can increase sanity
    private void makeBadCubes(int number) {
        for (int i = 0; i < number; i++) {
            Vector3f loc = new Vector3f(
                FastMath.nextRandomInt(-50, 50),
                0f,
                FastMath.nextRandomInt(-50,50) 
            );
            
            // Add the bad geom
            Geometry badgeom = myBox("BBox" + i, loc, ColorRGBA.Pink);
            //badgeom.addControl(new ChangeHealthBarControl(cam, rootNode));
            //rootNode.attachChild(badgeom);
            
            // Add the badgeom to bad_geoms
            // Potential Cache ISSUE
            bad_interact_geoms.add(badgeom);
        }
    }
    
    @Override
    public void simpleInitApp() {
        
       

        Geometry interactableBox = new Geometry("InteractableBox", new Box(0.1f, 0.1f, 0.1f));        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        interactableBox.setMaterial(mat);
        interactableBox.setLocalTranslation(0, 1, -10);
        
        rootNode.attachChild(interactableBox);
        
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
        
        
        // Collectable objects for now
        rootNode.attachChild(myBox("Good Box",
                loc1, ColorRGBA.Green));
        
        
        rootNode.attachChild(myBox("Bad Box", 
                loc2, ColorRGBA.Red));
    }
    
    // Box constructor
    public Geometry myBox(String name, Vector3f loc, ColorRGBA color) {
        Geometry local_geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        local_geom.setMaterial(mat);
        local_geom.setLocalTranslation(loc);
        return local_geom;
    }
}
