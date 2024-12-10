package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private Node playerNode;
    private Node yawNode;
    private Node pitchNode;
    private CameraNode camNode;
    public BoundingBox playerBounds;
    private float playerHeight;
    private float eyeOffset = 0.01f;
    private BetterCharacterControl characterControl;
    private BulletAppState bulletAppState;
    private boolean updateCamera = false;
    
    private boolean usePhysics = true;

    public Player(Spatial playerSpatial, Camera cam, BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
        this.playerNode = new Node("PlayerNode");

        // Attach the player's spatial model to the player node
        this.playerNode.attachChild(playerSpatial);

        // Configure the camera's near clipping plane
        //cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        
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
        
        float radius = 0.5f; // Adjust as needed
        float height = playerHeight; // Use playerHeight from your calculation
        float mass = 80f; // Average human mass in kg

        characterControl = new BetterCharacterControl(radius, height, mass);
        characterControl.setGravity(new Vector3f(0, -9.81f, 0)); // Set gravity

        // Add the control to the player node
        playerNode.addControl(characterControl);

        // Add the player control to the physics space
        bulletAppState.getPhysicsSpace().add(characterControl);
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
    
    public void enableCamera() {
        this.updateCamera = true;
    }
    
    public BetterCharacterControl getCharacterControl() {
        return this.characterControl;
    }


    public void setPosition(Vector3f newPosition) {
        if (usePhysics && characterControl != null) {
            characterControl.warp(newPosition); // For physics-enabled movement
        } else {
            playerNode.setLocalTranslation(newPosition); // Direct translation for static positioning
        }

        System.out.println("Player node set to position: " + playerNode.getLocalTranslation());
        System.out.println("Camera world position: " + camNode.getWorldTranslation());
        playerBounds.setCenter(newPosition); // Update bounding box
    }


    public void setCameraRotation(Quaternion rotation) {
        // Decompose the rotation into yaw and pitch
        float[] angles = rotation.toAngles(null);
        float yaw = angles[1];   // Yaw rotation around Y-axis
        float pitch = angles[0]; // Pitch rotation around X-axis

        yawNode.setLocalRotation(new Quaternion().fromAngles(0, yaw, 0));
        pitchNode.setLocalRotation(new Quaternion().fromAngles(pitch, 0, 0));        
    }
    
    public void updateCameraPosition() {
        Vector3f desiredCameraPosition = camNode.getWorldTranslation();
        Vector3f playerPosition = playerNode.getWorldTranslation();

        // Ray direction
        Vector3f direction = desiredCameraPosition.subtract(playerPosition).normalizeLocal();

        // Ray test
        List<PhysicsRayTestResult> results = new ArrayList<>();
        bulletAppState.getPhysicsSpace().rayTest(playerPosition, desiredCameraPosition, results);

        if (!results.isEmpty()) {
            // Find the closest collision
            PhysicsRayTestResult closestResult = results.stream()
                .min((r1, r2) -> Float.compare(r1.getHitFraction(), r2.getHitFraction()))
                .orElse(null);

            if (closestResult != null) {
                // Calculate collision distance
                float collisionDistance = closestResult.getHitFraction() * desiredCameraPosition.distance(playerPosition);
                float minimumCameraDistance = 1.0f; // Minimum distance between camera and player

                // Adjust camera position only if collision is too close
                if (collisionDistance < minimumCameraDistance) {
                    Vector3f adjustedPosition = playerPosition.add(direction.mult(collisionDistance - 0.1f));
                    camNode.setLocalTranslation(playerNode.worldToLocal(adjustedPosition, null));
                }
            }
        } else {
            System.out.println("Results is empty.");
        }
    }

}
