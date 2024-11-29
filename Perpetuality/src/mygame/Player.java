package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;

public class Player {
    private Node playerNode;
    private Node yawNode;
    private Node pitchNode;
    private CameraNode camNode;
    public BoundingBox playerBounds;
    private float playerHeight;
    private float eyeOffset = 0.01f;

    public Player(Spatial playerSpatial, Camera cam) {
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

        // Create yaw and pitch nodes
        yawNode = new Node("YawNode");
        playerNode.attachChild(yawNode);
        yawNode.setLocalTranslation(0, this.playerHeight / 2, 0);

        pitchNode = new Node("PitchNode");
        yawNode.attachChild(pitchNode);

        // Create the camera node and attach it
        camNode = new CameraNode("CameraNode", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        pitchNode.attachChild(camNode);
        camNode.setLocalTranslation(0, 0, 0.1f); // Adjust as needed
        
        playerSpatial.setCullHint(Spatial.CullHint.Always);
    }

    public Node getPlayerNode() {
        return this.playerNode;
    }

    public Node getYawNode() {
        return this.yawNode;
    }

    public Node getPitchNode() {
        return this.pitchNode;
    }

    public float getPlayerHeight() {
        return this.playerHeight;
    }

    public void setPosition(Vector3f newPosition) {
        playerNode.setLocalTranslation(newPosition);
        playerBounds.setCenter(newPosition);
    }

    public void setCameraRotation(Quaternion rotation) {
        // Decompose the rotation into yaw and pitch
        float[] angles = rotation.toAngles(null);
        float yaw = angles[1];   // Yaw rotation around Y-axis
        float pitch = angles[0]; // Pitch rotation around X-axis

        yawNode.setLocalRotation(new Quaternion().fromAngles(0, yaw, 0));
        pitchNode.setLocalRotation(new Quaternion().fromAngles(pitch, 0, 0));
        
        System.out.println("YawNode translation: " + yawNode.getLocalTranslation() + "\nYawNode rotation: " + yawNode.getLocalRotation());
        System.out.println("PitchNode translation: " + pitchNode.getLocalTranslation() + "\nPitchNode rotation: " + pitchNode.getLocalRotation());
        
    }
   
}
