package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import mygame.DialogBox;
import mygame.SceneLoader;

/**
 *
 * @author abarbe23
 */
public class BathroomScene extends AbstractAppState {
    private SceneLoader sceneLoader;
    private DialogBoxUI dialogBoxUI;
    private boolean flashlightPickedUp = false;

    public BathroomScene(SceneLoader sceneLoader, DialogBoxUI dialogBoxUI) {
        this.sceneLoader = sceneLoader;
        this.dialogBoxUI = dialogBoxUI;
    }

    public void loadScene() {
        sceneLoader.loadScene("Scenes/Bathroom.j3o", this::setupScene);
    }

    private void setupScene() {
        // Display initial dialogue
        dialogBoxUI.showDialog("Did I get sick again?", 1.0f, false);
    }

    public void update(float tpf) {
        if (!flashlightPickedUp) {
            checkFlashlightInteraction();
        }
    }

    private void checkFlashlightInteraction() {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(sceneLoader.getRootNode().getLocalTranslation(), Vector3f.UNIT_Z);
        sceneLoader.getRootNode().collideWith(ray, results);

        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();
            if ("Flashlight".equals(target.getName())) {
                dialogBoxUI.showDialog("You picked up the flashlight.", 1.0f, false);
                flashlightPickedUp = true;
                sceneLoader.getRootNode().detachChild(target);
            }
        }
    }
}
