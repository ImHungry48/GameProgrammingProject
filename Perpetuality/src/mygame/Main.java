// Michael Kim, Alaisha Barber, Chenjia Zhang

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
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.collision.*;
import com.jme3.math.Ray;
import java.util.ArrayList;

import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.DirectionalLight;
import com.jme3.scene.Spatial;


/**
 *
 * @author jasonsmacbookpro
 */
public class Main extends SimpleApplication {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        GameManager app = new GameManager();
        app.start();
    }
    
    
    @Override
    public void simpleInitApp() {
       
    }
    
    
   
    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
