/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;

/**
 *
 * @author mike0
 */
public class GameInputManager {

    private final static Trigger TRIGGER_CHANGEHEALTH = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    private final static Trigger TRIGGER_ITEM1 = new KeyTrigger(KeyInput.KEY_1);
    private final static Trigger TRIGGER_ITEM2 = new KeyTrigger(KeyInput.KEY_2);
    private final static Trigger TRIGGER_ITEM3 = new KeyTrigger(KeyInput.KEY_3);
    private final static String MAPPING_CHANGEHEALTH = "Change Health";
    private final static String MAPPING_ROTATE = "Rotate";
    private final static String MAPPING_ITEM1 = "Use Item 1";
    private final static String MAPPING_ITEM2 = "Use Item 2";
    private final static String MAPPING_ITEM3 = "Use Item 3";

    private InputManager inputManager;
    private ActionListener actionListener;
    private AnalogListener analogListener;

    // Constructor
    public GameInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    // Set Action Listener
    public void setActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    // Set Analog Listener
    public void setAnalogListener(AnalogListener listener) {
        this.analogListener = listener;
    }

    // Initialize input mappings
    public void initInputMappings() {
        inputManager.addMapping(MAPPING_CHANGEHEALTH, TRIGGER_CHANGEHEALTH);
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addMapping(MAPPING_ITEM1, TRIGGER_ITEM1);
        inputManager.addMapping(MAPPING_ITEM2, TRIGGER_ITEM2);
        inputManager.addMapping(MAPPING_ITEM3, TRIGGER_ITEM3);

        // Register listeners
        inputManager.addListener(actionListener, MAPPING_CHANGEHEALTH, MAPPING_ITEM1, MAPPING_ITEM2, MAPPING_ITEM3);
        inputManager.addListener(analogListener, MAPPING_ROTATE);
    }
}