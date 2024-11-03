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

import com.jme3.math.Ray;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class DialogBox extends SimpleApplication {

    // Class Fields
    private BitmapText dialogText;
    Picture dialogFrame;
    private final static Trigger TRIGGER_DIALOG = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_DIALOG = "Dialog";
    
    private boolean dialogOpen;
    
    
    private static Box mesh1 = new Box(Vector3f.ZERO, 1, 1, 1); // for cursor
    
    public static void main(String[] args) {
        DialogBox app = new DialogBox();
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
        
        // Place Holder
        Box mesh = new Box(Vector3f.ZERO, 1, 1, 1); // create box mesh
        Geometry geom = new Geometry("Blue Box", mesh);
        Material mat = new Material(assetManager, 
            "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
        
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
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
