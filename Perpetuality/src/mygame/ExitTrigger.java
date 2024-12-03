/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;

/**
 *
 * @author alais
 */
public class ExitTrigger {
    private Vector3f exitPoint;
    private BoundingBox exitBox;
    private Runnable onExitSuccess;
    private Runnable onExitFailure;

    public ExitTrigger(Vector3f exitPoint, Runnable onExitSuccess, Runnable onExitFailure) {
        this.exitPoint = exitPoint;
        this.exitBox = new BoundingBox(exitPoint, 1f, 2f, 1f); // Adjust extents as needed
        this.onExitSuccess = onExitSuccess;
        this.onExitFailure = onExitFailure;
    }

    public boolean isPlayerNear(Player player) {
        Vector3f playerPosition = player.getPlayerNode().getWorldTranslation();
        return exitBox.contains(playerPosition);
    }

    public void execute(Player player) {
        if (isPlayerNear(player)) {
            if (onExitSuccess != null) onExitSuccess.run();
        } else {
            if (onExitFailure != null) onExitFailure.run();
        }
    }
}

