package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.Queue;

public class Cube {
    private String type;
    private Node node;
    private Geometry geometry;

    public Cube(String type, AssetManager assetManager, Vector3f position, GameManager gameManager) {
        this.type = type;
        this.node = new Node();

        // Create the cube geometry
        Box box = new Box(0.2f, 0.2f, 0.2f);
        this.geometry = new Geometry("Cube", box);

        // Set a different color depending on the type
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        switch (type) {
            case "Consumable":
                mat.setColor("Color", ColorRGBA.Gray);
                break;
            case "Door":
                mat.setColor("Color", ColorRGBA.Blue);
                break;
            case "Artifact":
                mat.setColor("Color", ColorRGBA.Yellow);
                break;
            default:
                mat.setColor("Color", ColorRGBA.White);
                break;
        }
        this.geometry.setMaterial(mat);

        // Attach a CubeControl to the geometry
        CubeControl control = new CubeControl(this, gameManager);
        geometry.addControl(control);
        
        node.attachChild(this.geometry);
        node.setLocalTranslation(position);
    }

    public String getType() {
        return type;
    }

    public Node getNode() {
        return node;
    }
    
    public Geometry getGeometry() {
        return geometry;
    }
}
