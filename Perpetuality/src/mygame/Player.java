package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Player {
    private Node playerNode;
    public float playerBoxHalfExtent = 1f;
    public BoundingBox playerBounds;
    private float playerHeight;
    private float eyeOffset = 0.01f;

    public Player(Spatial playerSpatial) {
        this.playerNode = new Node("PlayerNode");

        // Attach the player's spatial model to the player node
        this.playerNode.attachChild(playerSpatial);

        // Calculate player height and bounds
        playerSpatial.updateModelBound();
        BoundingBox playerBox = (BoundingBox) playerSpatial.getWorldBound();
            this.playerHeight = playerBox.getYExtent() * 2;
            this.playerBounds = new BoundingBox(
                    playerBox.getCenter(),
                    playerBox.getXExtent(),
                    playerBox.getYExtent(),
                    playerBox.getZExtent()
            );
    }


    public Node getPlayerNode() {
        return this.playerNode;
    }
    
    public float getPlayerHeight() {
        return this.playerHeight;
    }
    
    public float getPlayerEyeOffset() {
        return this.eyeOffset;
    }

    public void setPosition(Vector3f newPosition) {
        playerNode.setLocalTranslation(newPosition);
        playerBounds.setCenter(newPosition);
    }
}