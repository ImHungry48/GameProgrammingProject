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
                System.out.println("Taken consumable");
                gameManager.getGameState().getInventory().addItem("consumable");
                break;
                
            case "Battery":
                System.out.println("Taken battery");
                // Call battery function here
                gameManager.getGameState().getInventory().addItem("battery");
                break;
                
            case "Pages":
                System.out.println("Taken page");
                // Call page function here
                gameManager.getGameState().getInventory().addItem("page");
                break;
                
            case "Door":
                // Check if game is able to finish
                System.out.println("Clicked door");
                if (!gameManager.getGameState().checkWin()) {
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