package mygame;

import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.Node;

public class CubeControl extends AbstractControl {

    private Cube cube;
    private GameManager gameManager;
    
    public CubeControl(Cube cube, GameManager gameManager) {
        this.cube = cube;
        this.gameManager = gameManager;
    }

    @Override
    protected void controlUpdate(float tpf) {
        // No ongoing updates required for now
    }

    @Override
    protected void controlRender(com.jme3.renderer.RenderManager rm, com.jme3.renderer.ViewPort vp) {
        // No rendering-specific updates required
    }

    public void onClick() {
        switch (cube.getType()) {
            case "Consumable":
                gameManager.getGameState().getInventory().addItem("consumable");
                break;
                
            case "Battery":
                // Call battery function here
                gameManager.getGameState().getInventory().addItem("battery");
                break;
                
            case "Pages":
                // Call page function here
                gameManager.getGameState().getInventory().addItem("page");
                break;
                
            case "Door":
                // Check if game is able to finish
                if (!gameManager.getGameState().checkPagesGathered()) {
                    // Game is not finish so just end method early
                    return;
                }
                gameManager.getGameState().displayGameOverScreen(true);
                break;
                
        }
        
        Node parent = spatial.getParent();
        if (parent != null) {
            parent.detachChild(spatial);
        }
    }
}
