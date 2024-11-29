package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.ui.Picture;

import com.jme3.collision.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.math.FastMath;

import com.jme3.math.Ray;
import com.jme3.scene.Spatial;
import com.jme3.shadow.SpotLightShadowRenderer;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class FlashLightHealthBar extends SimpleApplication {

    // Fields for Dialog Box Trigger
    private BitmapText dialogText;
    Picture dialogFrame;
    private final static Trigger TRIGGER_LEFT_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_DIALOG = "Dialog";
    private boolean dialogOpen;
    
    // Box Mesh
    private static Box mesh1 = new Box(Vector3f.ZERO, 1, 1, 1);
    
    /* ------------------------------------------------------------*/
    // IMPORTANT: FIELDS FOR FLASH LIGHT
    private SpotLight flash_light;
    private float flash_radius;
    private float flash_light_timer;
    public boolean flash_light_has_battery; // IMPORTANT: for inventory system management; Essentially flashlight can only function if it has battery.
    
    // NEW fields for flashlight trigger
    private final static Trigger TRIGGER_FLASHLIGHT = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    private final static String MAPPING_FLASHLIGHT = "Flash Light";
    
    // NEW fields for flashlight managing health
    private int health; // NEW: PLACE HOLDER for Health. Should Be deleted once incorporated
    private Geometry battery; // NEW: Place Holder for Battery 
    
    // New Trigger for collecting battery
    private final static String MAPPING_COLLECT_BATTERY = "Collect Battery";
    /* ------------------------------------------------------------*/
    
    // Sunlight
    DirectionalLight sun = new DirectionalLight();
    final int SHADOWMAP_SIZE=1024;
    
    // Character
    Spatial monsterGeom;
    
    public static void main(String[] args) {
        FlashLightHealthBar app = new FlashLightHealthBar();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        dialogOpen = false;
        
        // Remove default GUI from the app
        setDisplayStatView(false);
        setDisplayFps(false);
        
        /* THESE ARE DIALOG BOX AND MODEL RELATED, NOT FOR LIGHTING */
        /* ------------------------------------------------------------*/
        // DialogBox Trigger Handle
        inputManager.addMapping(MAPPING_DIALOG, TRIGGER_LEFT_CLICK);
        inputManager.addListener(actionListener, new String[] {MAPPING_DIALOG});
        
        // NPC Character Place Holder, PROBLEM: material not applying
        monsterGeom = assetManager.loadModel("Models/Oto/Oto.j3o");
        monsterGeom.setName("Monster");
        rootNode.attachChild(monsterGeom);
        
        // Place Holder for Dialog Box triggering
        rootNode.attachChild(myBox("Blue Box", 
                new Vector3f(0, -1.5f, -10), ColorRGBA.Blue));
        // Load a font, initialize BimapText object and attach it to guiNode
        dialogText = new BitmapText(guiFont);
        dialogText.setSize(60);
        dialogText.setColor(ColorRGBA.White);
        // Loading the format of dialog frame into the game
        setDialogFrame();
        
        // the cursor
        attachCenterMark();
     
        // Sunlight
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        rootNode.addLight(sun);
        /* ------------------------------------------------------------------------------------------------------------*/
        
        
        
        /* FLASH LIGHT RELATED INITIAL SET UP */
        // FlashLight Trigger Handle
        inputManager.addMapping(MAPPING_FLASHLIGHT, TRIGGER_FLASHLIGHT);
        inputManager.addListener(actionListener, new String[] {MAPPING_FLASHLIGHT});
        
        // Battery Collection Trigger Handle
        inputManager.addMapping(MAPPING_COLLECT_BATTERY, TRIGGER_LEFT_CLICK);
        inputManager.addListener(actionListener, new String[] {MAPPING_COLLECT_BATTERY});
        
        
        // Flash Light Set Up
        flash_radius = 70f;
        flash_light = new SpotLight();
        // Spot Range, light cone
        flash_light.setSpotRange(flash_radius);
        flash_light.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD);
        flash_light.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD);
        // Color, location, direction
        flash_light.setColor(ColorRGBA.Yellow.mult(ColorRGBA.White).mult(2));
        flash_light.setPosition(cam.getLocation());
        flash_light.setDirection(cam.getDirection());
        rootNode.addLight(flash_light);
        // Shadow
        SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        slsr.setLight(flash_light);
        viewPort.addProcessor(slsr);
        
        // Constant Fields
        flash_light.setEnabled(true); // CHANGE: we can choose to not have flash light on first
        flash_light_has_battery = true; // CHANGE: we can choose not to give battery first
        flash_light_timer = 100; // CHANGE: depends on whether battery is avaliable initially
        
        
        // NEW: Initialize the Place Holder for health
        health = 100; 
        // NEW: Intialize Place Holder for Battery Geometry
        battery = myBox("Battery", 
                new Vector3f(0, 5, -5), ColorRGBA.Green);
        rootNode.attachChild(battery);
    }
    
    /* THESE ARE DIALOG BOX and Box Creation Related Helper Function */
    /* ------------------------------------------------------------*/
    private void setDialogFrame() {
        dialogFrame = new Picture("dialog box frame");
        dialogFrame.setImage(assetManager, "Interface/dialogbox.jpg", false);
        dialogFrame.move(settings.getWidth()/2 - 800, settings.getHeight()/2 - 512, -2);
        dialogFrame.setWidth(1500);
        dialogFrame.setHeight(1000);
    }
    private void attachCenterMark() {
        Geometry c = myBox("center mark", 
            Vector3f.ZERO, ColorRGBA.White);
        c.scale(4); 
        c.setLocalTranslation( settings.getWidth()/2,
        settings.getHeight()/2, 0 ); 
        guiNode.attachChild(c); // attach to 2D user interface
    }
    public Geometry myBox(String name, Vector3f loc, ColorRGBA color) {
        Geometry local_geom = new Geometry(name, mesh1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        local_geom.setMaterial(mat);
        local_geom.setLocalTranslation(loc);
        return local_geom;
    }
    /* ------------------------------------------------------------*/
    
    /* Helper Function for Flash Light Implementation */
    
    // Turn flash light on or off (Only controls fl on or off)
    private void toggleFlashLight() {
        if (flash_light_has_battery) {
            if (!flash_light.isEnabled()) {
                flash_light.setEnabled(true); // turn flash light on
            } else {
                flash_light.setEnabled(false);
            }
        }
        // The Flash Light can not be toggled when there is no battery
    }
    
        
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_DIALOG) && !isPressed) {
                
                // If the user clicks on the box, the textbox will be up
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);
                
                if (results.size() > 0) {
                    Geometry target = results.getClosestCollision().getGeometry();
                    // implement action here
                    if (target.getName().equals("Blue Box") && !dialogOpen) {
                        // Set the dialogtext as needed
                        dialogText.setText("  It's very dark. \n"
                                + "   I need this battery to \n"
                                + "   recharge the \n flashlight...");
                        dialogText.setLocalTranslation(
                        settings.getWidth()/2 - dialogText.getLineWidth()/2,
                        settings.getHeight()/2 + 2* dialogText.getLineHeight(),
                        0);
                        // Dialog frame pop up along with text
                        guiNode.attachChild(dialogText);
                        guiNode.attachChild(dialogFrame);
                        dialogOpen = true;
                    } else if (dialogOpen) {
                        guiNode.detachChild(dialogText);
                        guiNode.detachChild(dialogFrame);
                        dialogOpen = false;
                    }
                } else {
                    if (dialogOpen) {
                        // if clicking on nothing, then detach node
                        guiNode.detachChild(dialogText);
                        guiNode.detachChild(dialogFrame);
                        dialogOpen = false;
                    }
                }
            }
            
            /* NEW Flash Light Related Trigger */
            if (name.equals(MAPPING_FLASHLIGHT) && !isPressed) {
                toggleFlashLight();
            }
            
            if (name.equals(MAPPING_COLLECT_BATTERY) && !isPressed) {
                // If the user clicks on the battery, flash_light will have battery, timer will reset
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);
                
                if (results.size() > 0) {
                    Geometry target = results.getClosestCollision().getGeometry();
                    // implement action here
                    if (target.getName().equals("Battery")) {
                        flash_light_has_battery = true;
                        flash_light_timer = 100;
                    }
                }
            }
            
            
        }
    };
    
    
    /*
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            if (name.equals(MAPPING_DIALOG)) {
                
                // If the user clicks and hold on too an object, the textbox will be up
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);
                
                // Set the dialogtext as needed
                dialogText.setText("It's very dark. I need this battery to recharge the flashlight...");
                // Dialog frame pop up along with text
                guiNode.attachChild(dialogText);
                guiNode.attachChild(dialogFrame);
            }
        }
    }; */
    
    

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
        /* Can modify to change the style of any diaogbox element
        if (distance < 10f) {
            logo.setImage(assetManager,
         "Interface/chimpanzee-smile.gif", true);
        } else {
            logo.setImage(assetManager,
         "Interface/chimpanzee-sad.gif", true);
        } */
        
        // Timer controls whether there is still battery
        if (flash_light_timer <= 0 && flash_light_has_battery) {
            flash_light_has_battery = false;
        }
        
        // Flashlight Mechanism
        if (flash_light_has_battery) {
            if (flash_light.isEnabled()) {
                flash_light.setPosition(cam.getLocation());
                flash_light.setDirection(cam.getDirection());
                
                // has battery, light on, then timer decreases
                flash_light_timer -=0.01;
                // System.out.println("This is the current flash_light_timer: "+ flash_light_timer);
  
            } else {
                // IMPORTANT: if flash light is off, then health will decrease (NEED to consider safe room!!)
                health -= 1;
                // System.out.println("This is the current health: "+ health);
            }
            
        } else {
            // Flash light has no battery
            if (flash_light.isEnabled()) {
                flash_light.setEnabled(false); // has to be set to off when no battery
                
                // IMPORTANT: if flash light is off, then health will decrease
                health -= 0.001;
                // System.out.println("This is the current health: "+ health);
            }
        }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
