/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package mygame;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.water.SimpleWaterProcessor;

public class WaterPuddle {

    private Geometry puddleGeometry;

    public WaterPuddle(SimpleWaterProcessor waterProcessor, float radius, float height, int radialSamples, Node rootNode) {
        // Ensure minimum values for radial and axis samples
        int axisSamples = 2; // Minimum allowed axis samples
        if (radialSamples < 3) {
            radialSamples = 3; // Ensure radial samples are valid
        }

        // Create a cylinder mesh for the puddle
        Cylinder cylinderMesh = new Cylinder(axisSamples, radialSamples, radius, height, true);
        puddleGeometry = new Geometry("PuddleCylinder", cylinderMesh);

        // Rotate the puddle to lie flat on the ground (90 degrees around the X-axis)
        puddleGeometry.rotate(FastMath.PI / 2, 0, 0);

        // Set the material using the water processor's material
        puddleGeometry.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(this.getPuddleGeometry());

    }

    public Geometry getPuddleGeometry() {
        return puddleGeometry;
    }
}

