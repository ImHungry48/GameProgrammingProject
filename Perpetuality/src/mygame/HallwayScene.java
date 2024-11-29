/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;

/**
 *
 * @author alais
 */
public class HallwayScene extends AbstractAppState {

    private final GameManager app;
    private final AppStateManager stateManager;
    private final Node rootNode;
    private final Player player;
    private final DialogBoxUI dialogBoxUI;
    private final SceneLoader sceneLoader;
    
    public HallwayScene(GameManager gameManager) {
        this.app = gameManager;
        this.stateManager = gameManager.getStateManager();
        this.rootNode = gameManager.getRootNode();
        this.player = gameManager.getPlayer();
        this.dialogBoxUI = gameManager.getDialogBoxUI();
        this.sceneLoader = gameManager.getSceneLoader();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        
    }
    
    public void loadScene() {
        
    }
    
    public void setupScene() {
        
    }
    
    @Override
    public void update(float tpf) {
    }
}
