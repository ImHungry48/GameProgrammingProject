package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class AnimateModel {

    private Node robot; // Robot model
    private AnimComposer animComposer; // Animation controller
    private RigidBodyControl hitboxControl; // Collision shape for the hitbox

    private static final String ANI_IDLE = "stand";
    private static final String ANI_WALK = "Walk";

    public AnimateModel(AssetManager assetManager, Node rootNode, com.jme3.bullet.PhysicsSpace physicsSpace) {
        loadModel(assetManager, rootNode);
        setUpHitbox(physicsSpace); // Set up hitbox and add to physics space
    }

    private void loadModel(AssetManager assetManager, Node rootNode) {
        robot = (Node) assetManager.loadModel("Textures/Oto/Oto.mesh.xml");

        // Position and scale the robot
        robot.setLocalTranslation(new Vector3f(-2.5f, 1.2f, 0));
        robot.setLocalScale(0.25f);

        // Attach the model to the rootNode
        rootNode.attachChild(robot);

        // Get the animation composer
        animComposer = robot.getControl(AnimComposer.class);
        if (animComposer != null) {
            animComposer.setCurrentAction(ANI_IDLE); // Start with idle animation
        }
    }

    private void setUpHitbox(com.jme3.bullet.PhysicsSpace physicsSpace) {
        // Create a collision shape for the robot
        hitboxControl = new RigidBodyControl(CollisionShapeFactory.createDynamicMeshShape(robot), 1f); // Dynamic hitbox

        // Attach the collision control to the robot
        robot.addControl(hitboxControl);

        // Add the hitbox to the physics space
        physicsSpace.add(hitboxControl);
    }

    public void startWalking() {
        if (animComposer != null && !ANI_WALK.equals(animComposer.getCurrentAction().toString())) {
            animComposer.setCurrentAction(ANI_WALK);
        }
    }

    public void stopWalking() {
        if (animComposer != null) {
            animComposer.setCurrentAction(ANI_IDLE);
        }
    }

    public void onCollision() {
        System.out.println("Robot hit!");
        // Implement logic when the robot is hit (e.g., change animation or reduce health)
    }
}