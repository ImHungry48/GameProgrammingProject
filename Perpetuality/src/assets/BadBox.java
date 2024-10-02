/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assets;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
/**
 *
 * @author mike0
 */
public class BadBox {

    private static final Box MESH = new Box(Vector3f.ZERO, 1, 1, 1);

    public static Geometry createBadBox(String name, Vector3f loc, AssetManager assetManager) {
        Geometry geom = new Geometry(name, MESH);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Pink);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        return geom;
    }
}