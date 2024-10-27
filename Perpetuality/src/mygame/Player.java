package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Player {
    private Node playerNode;
    public float playerBoxHalfExtent = 1f;
    public BoundingBox playerBounds;

    public Player(Spatial playerSpatial) {
        this.playerNode = new Node("PlayerNode");

        // Attach the player's spatial model to the player node
        this.playerNode.attachChild(playerSpatial);
        
        // Set initial position and bounding box
        playerSpatial.setLocalTranslation(0, playerBoxHalfExtent, 0);
        this.playerBounds = new BoundingBox(playerSpatial.getLocalTranslation(), playerBoxHalfExtent, playerBoxHalfExtent, playerBoxHalfExtent);
    }

    public Node getPlayerNode() {
        return playerNode;
    }

    public void setPosition(Vector3f newPosition) {
        playerNode.setLocalTranslation(newPosition);
        playerBounds.setCenter(newPosition);
    }
}