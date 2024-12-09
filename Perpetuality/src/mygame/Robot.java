package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Robot {
    private Node player;
    private AnimComposer animComposer;

    private static final String ANI_IDLE = "stand";
    private static final String ANI_WALK = "Walk";

    private final Vector3f upDirection = new Vector3f(0, 1, 0);

    public Robot(Spatial model, Vector3f spawnLocation) {
        // Load the model
        player = (Node) model;
        
        player.setLocalTranslation(spawnLocation);
        
        player.setLocalScale(0.1f); // Adjust this value for the desired size

        // Attach animation controller
        animComposer = player.getControl(AnimComposer.class);
        if (animComposer != null) {
            animComposer.setCurrentAction(ANI_WALK);

            // Replace walk action with original walk action + doneTween
            Action walk = animComposer.action(ANI_WALK);
            Tween doneTween = Tweens.callMethod(this, "onAnimComplete", ANI_WALK);
            animComposer.actionSequence(ANI_WALK, walk, doneTween);
        }
    }

    public Node getPlayer() {
        return player;
    }

    public void update(Vector3f cameraPosition, float tpf) {
        if (player.getParent() != null) {
            // Move towards the camera
            Vector3f direction = cameraPosition.subtract(player.getWorldTranslation()).normalizeLocal();
            player.move(direction.mult(tpf));

            // Look at the camera
            player.lookAt(cameraPosition, upDirection);
        }
    }
    
    // Method to move the robot to a target location
    public void moveTo(Vector3f targetLocation) {
        
        player.setLocalTranslation(targetLocation);
        
    }

    public void onAnimComplete(String animationName) {
        System.out.println("Robot completed one " + animationName + " loop.");
    }

    public void triggerDialog(Vector3f cameraPosition) {
        float distanceThreshold = 1.5f;
        float distanceToCamera = cameraPosition.distance(player.getWorldTranslation());
        if (distanceToCamera <= distanceThreshold) {
            System.out.println("Robot: I'm close to you! What should I do?");
        }
    }
    
    public float getDistance(Vector3f cameraPosition) {
        return cameraPosition.distance(player.getWorldTranslation());
    }
}
