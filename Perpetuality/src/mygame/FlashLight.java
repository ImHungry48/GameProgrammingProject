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
public class FlashLight extends SimpleApplication {

    // Class Fields
    private BitmapText dialogText;
    Picture dialogFrame;
    private final static Trigger TRIGGER_DIALOG = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_DIALOG = "Dialog";
    
    private boolean dialogOpen;
    
    private static Box mesh1 = new Box(Vector3f.ZERO, 1, 1, 1); // for cursor
    
    // fields for flahslight
    private SpotLight flash_light;
    private float flash_radius;
    // Sunlight
    DirectionalLight sun = new DirectionalLight();
    final int SHADOWMAP_SIZE=1024;
    
    // Character
    Spatial monsterGeom;
    
    public static void main(String[] args) {
        FlashLight app = new FlashLight();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        dialogOpen = false;
        
        // Remove default GUI from the app
        setDisplayStatView(false);
        setDisplayFps(false);
        
        // Trigger Handle
        inputManager.addMapping(MAPPING_DIALOG, TRIGGER_DIALOG);
        inputManager.addListener(actionListener, new String[] {MAPPING_DIALOG});
        
        // NPC Character Place Holder, PROBLEM: material not applying
        monsterGeom = assetManager.loadModel("Models/male_base_mesh/male_base_mesh.j3o");
        monsterGeom.setName("Monster");
        Material mat = new Material(assetManager, 
            "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Ambient", ColorRGBA.Blue);
        mat.setColor("Diffuse", ColorRGBA.Blue);
        monsterGeom.setMaterial(mat);
        rootNode.attachChild(monsterGeom);
        
        
        // Load a font, initialize BimapText object and attach it to guiNode
        dialogText = new BitmapText(guiFont);
        dialogText.setSize(60);
        dialogText.setColor(ColorRGBA.White);
        
        
        // Loading the format of dialog frame into the game
        dialogFrame = new Picture("dialog box frame");
        dialogFrame.setImage(assetManager, "Interface/dialogbox.jpg", false);
        dialogFrame.move(settings.getWidth()/2 - 800, settings.getHeight()/2 - 512, -2);
        dialogFrame.setWidth(1500);
        dialogFrame.setHeight(1000);
        
        // the cursor
        attachCenterMark();
        
        // Sunlight
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        rootNode.addLight(sun);
        
        flash_radius = 70f;
        // FLASH Light implementation
        flash_light = new SpotLight();
        // Spot Range, light cone
        flash_light.setSpotRange(flash_radius);
        flash_light.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD);
        flash_light.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD);
        // Color, location, direction
        flash_light.setColor(ColorRGBA.Yellow.mult(ColorRGBA.White));
        flash_light.setPosition(cam.getLocation());
        flash_light.setDirection(cam.getDirection());
        rootNode.addLight(flash_light);
        // Shadow
        SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        slsr.setLight(flash_light);
        viewPort.addProcessor(slsr);
        
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
                    if (target.equals(monsterGeom) && !dialogOpen) {
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
        
        // Flashlight Mechanism Update
        flash_light.setPosition(cam.getLocation());
        flash_light.setDirection(cam.getDirection());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
