/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;


/**
 *
 * @author jasonsmacbookpro
 */
public class Sky extends SimpleApplication {
    
    private Geometry geom;
    
    // NEW FOR SKY
    private Node sceneNode;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Sky app = new Sky();
        app.start();

    }
    
    @Override
    public void simpleInitApp() {
        
        /* New for Sky Box*/
        // 1st Option is a dark blue lagoon sky (I think this kinda work well)
        Texture west  = assetManager.loadTexture(
           "Textures/Sky/Lagoon/lagoon_west.jpeg");
        Texture east  = assetManager.loadTexture(
           "Textures/Sky/Lagoon/lagoon_east.jpeg");
        Texture north = assetManager.loadTexture(
           "Textures/Sky/Lagoon/lagoon_north.jpeg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpeg");
        Texture up = assetManager.loadTexture(
        "Textures/Sky/Lagoon/lagoon_up.jpeg");
        Texture down  = assetManager.loadTexture(
        "Textures/Sky/Lagoon/lagoon_down.jpeg");
        
        // use createSky() method of SkyFactory to create spatial
        Spatial sky = SkyFactory.createSky(assetManager,
        west, east, north, south, up, down);
        

        // 2nd option is the jmonkey default sky
//        Spatial sky = SkyFactory.createSky( assetManager,
//       "Textures/Sky/Bright/BrightSky.dds", false);

        // 3rd option is a hdr file I got from the web, but on mac I could not open the scene Explorer
        // and set the map format to equirectangular. 
        // Here's how it should work if you can check it out
        /*
        How to Map Equirect Images in JMonkey using the Skybox Wizard.
        Skybox Wizard > Single Texture > Map Type: EquirectMap
        */
        //Spatial sky = SkyFactory.createSky( assetManager,
        // "Textures/Sky/satara_night_no_lamps_4k.hdr", false); // Currently we can see the sky but the orientation is off

        rootNode.attachChild(sky);

        
        // setUpLight();
        
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        
        // Scaling 
        //geom.setLocalScale(0.5f);
        geom.setLocalScale(0.5f,3f,0.75f);
        
        // Rotate
        float r = FastMath.DEG_TO_RAD * 45f;
        geom.rotate(0.0f, r, 0.0f);

        rootNode.attachChild(geom);
    
       
    }
    
    private void setUpLight() {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    rootNode.addLight(al);

    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
    rootNode.addLight(dl);
  }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
           
        }
    };
    
   private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
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



