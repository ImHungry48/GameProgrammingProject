/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
/* ---------New For Particle---------- */
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
/* ------------------- */
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


/**
 *
 * @author jasonsmacbookpro
 */
public class ParticleDustSmoke extends SimpleApplication {
    
    private final static Trigger TRIGGER_COLOR = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_COLOR = "Toggle Color";
    private final static String MAPPING_ROTATE = "Rotate";
    private final static Trigger TRIGGER_COLOR2 = 
            new KeyTrigger(KeyInput.KEY_C);
    
    
    private Geometry geom;
    
    
    // NEW FIELD FOR PARTICLE
    private ParticleEmitter dustEmitter;
    private float angle = 0;
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ParticleDustSmoke app = new ParticleDustSmoke();
        app.start();

    }
    
    @Override
    public void simpleInitApp() {
        
        
        // NEW FOR PARTICLE
        // Descriptive name for emitter, and keep 20 particles of type triangle ready
        dustEmitter = new ParticleEmitter("dust emitter", Type.Triangle, 100);
        
        // Set material
        Material dustMat = new Material(assetManager,"Common/MatDefs/Misc/Particle.j3md");
        dustEmitter.setMaterial(dustMat);
        
        // Load smoke.png into the Texture property of the material
        dustMat.setTexture("Texture",assetManager.loadTexture("Effects/smoke.png"));
        
        // Help with segmenting the image for smoke.png
        dustEmitter.setImagesX(2);
        dustEmitter.setImagesY(2);
        
        // Make dust cloud more swirly and random
        dustEmitter.setSelectRandomImage(true);
        dustEmitter.setRandomAngle(true);
        dustEmitter.getParticleInfluencer().setVelocityVariation(1f); // 1f means emits particle in all directions 360 degrees
        
        // Attach emitter to a node
        rootNode.attachChild(dustEmitter);
        
        // Can control various features of the dust
        dustEmitter.setStartSize(1);
        dustEmitter.setEndSize(3);
        dustEmitter.setStartColor(ColorRGBA.DarkGray);
        dustEmitter.setEndColor(ColorRGBA.Red);
        
        dustEmitter.setGravity(0,1,0);
        
        dustEmitter.setLowLife(3f);
        dustEmitter.setHighLife(10f);
        
        // Can connect the smoke to events if wanted to
//        dustEmitter.setParticlesPerSec(0); // Pause a freshly intialized emitter until we need it
//        dustEmitter.setParticlesPerSec(20); // Start playing again likely after a game action
//        dustEmitter.killAllParticles(); // stop the effect and remove all particles
        
        
        
        // Set location of the smoke, or can choose to make the smoke rotate in circle by commenting out the code in simple update
        // IDEA: I THINK WE CAN USE DUST EMITTER TO INDICATE LOCATION OF CONSUMABLES
        // dustEmitter.setLocalTranslation(0, 0, 3);
        /* ------------------- ----------------------------------------------------------------------------*/
        
        inputManager.addMapping(MAPPING_COLOR, TRIGGER_COLOR, TRIGGER_COLOR2);
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addListener(actionListener, new String[] {MAPPING_COLOR});
        inputManager.addListener(analogListener, new String[] {MAPPING_ROTATE});
        
        
        
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
        
        
        // Adding anothe r object
        Vector3f v = new Vector3f(2.0f, 1.0f, -3.0f);
        Box b2 = new Box(Vector3f.ZERO, 1,1,1);
        Geometry geom2 = new Geometry("Box", b2);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Yellow);
        geom2.setMaterial(mat2);
        
        // Translate
        geom2.setLocalTranslation(v);
        
        // Scaling
        //geom2.scale(2.0f);
        geom2.scale(2.0f, 0.33f,2.0f);
        
        // Rotate
        // geom2.rotate(r, 0.0f, 0.0f);
        
        // Rotate using Quaternion
        Quaternion roll045 = new Quaternion();
        roll045.fromAngleAxis( 45*FastMath.DEG_TO_RAD , Vector3f.UNIT_X);
        geom2.setLocalRotation(roll045);
        
        rootNode.attachChild(geom2);
        
        
        // Rotate around pivot
        Node pivot = new Node("pivot node");
        pivot.attachChild(geom);
        pivot.attachChild(geom2);
        pivot.rotate(00, 0, FastMath.DEG_TO_RAD*45);
        rootNode.attachChild(pivot);
    
       
    }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_COLOR) && !isPressed) {
                // implement action here
                geom.getMaterial().setColor("Color", ColorRGBA.randomColor());
            }
            // System.out.println("You triggered: "+name);
           
        }
    };
    
   private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            if (name.equals(MAPPING_ROTATE)) {
                // implement action here
                geom.rotate(0,intensity,0); // rotate around Y axis
            }
            // System.out.println("You triggered: "+name);
        }
   };

    
    @Override
    public void simpleUpdate(float tpf) {        
        // make the emitter fly in horizontal circles
        angle += tpf;
        angle %= FastMath.TWO_PI;
        // radius is currently 2
        float x = FastMath.cos(angle) * 5;
        float y = FastMath.sin(angle) * 5;
        dustEmitter.setLocalTranslation(x, 0, y);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}



