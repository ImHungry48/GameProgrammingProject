package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class CubeManager {

    private AssetManager assetManager;
    private Node rootNode;
    private GameManager gameManager;

    public CubeManager(AssetManager assetManager, Node rootNode, GameManager gameManager) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.gameManager = gameManager;
    }

    public void createCubes(int consumableCount, int pagesCount, int doorCount) {
        // Add consumable cubes
        for (int i = 0; i < consumableCount; i++) {
            Cube cube = createCube("Consumable", new Vector3f(i * 3 - 3, 1, 0));
            attachCube(cube);
        }

        // Add pages cubes
        for (int i = 0; i < pagesCount; i++) {
            Cube cube = createCube("Pages", new Vector3f(i * 3 - 3, 2, 3));
            attachCube(cube);
        }

        // Add door cubes
        for (int i = 0; i < doorCount; i++) {
            Cube cube = createCube("Door", new Vector3f(i * 3 - 3, 1, -3));
            attachCube(cube);
        }
    }

    private Cube createCube(String type, Vector3f position) {
        return new Cube(type, assetManager, position, gameManager);
    }

    private void attachCube(Cube cube) {
        cube.getNode().addControl(new CubeControl(cube, gameManager));
        rootNode.attachChild(cube.getNode());
    }
}
