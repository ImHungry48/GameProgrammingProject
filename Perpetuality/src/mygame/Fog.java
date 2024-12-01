/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;


import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.RenderManager;


import com.jme3.scene.Spatial;


/**
 *
 * @author jasonsmacbookpro
 */
public class Fog extends SimpleApplication {
    
    // NEWLY ADDED FIELD FOR FOG
    private FilterPostProcessor fpp;
    private FogFilter fogFilter;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Fog app = new Fog();
        app.start();

    }
    
    @Override
    public void simpleInitApp() {
        // NEW BELOW
        
        // This part is just for demo purposes
        /* ------------------- ----------------------------------------------------------------------------*/
        flyCam.setMoveSpeed(100);
        setUpLight();
        // We load the scene from the zip file and adjust its size.
        assetManager.registerLocator(
                        "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/jmonkeyengine/town.zip",
                        HttpZipLocator.class);
        Spatial sceneModel = assetManager.loadModel("main.scene");
        sceneModel.setLocalScale(2f);
        
        // Attach scene to root node
        rootNode.attachChild(sceneModel);
        /* ------------------- ----------------------------------------------------------------------------*/
        

        // THIS PART is the main part for fog
        /* ------------------- ----------------------------------------------------------------------------*/
        // Initialize FilterPostProcessor, and add it to viewPort
        fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);
        
        //Initialize the FogFilter and add it to the FilterPostProcesor.
        fogFilter = new FogFilter();
        fogFilter.setFogDistance(155); // Can change based on when we wantt the fog to appear or disapper, the distance is relative to camera
        fogFilter.setFogDensity(2.0f); // Can change
        fpp.addFilter(fogFilter);
        
        // Can set different fog color
        fogFilter.setFogColor(ColorRGBA.DarkGray);
        
        
        /* ------------------- ----------------------------------------------------------------------------*/
        
        
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

    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}



