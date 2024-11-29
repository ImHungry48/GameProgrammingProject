package mygame;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class GameInputManager {


    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    private final static Trigger TRIGGER_ITEM1 = new KeyTrigger(KeyInput.KEY_1);
    private final static Trigger TRIGGER_ITEM2 = new KeyTrigger(KeyInput.KEY_2);
    private final static Trigger TRIGGER_ITEM3 = new KeyTrigger(KeyInput.KEY_3);
    private final static String MAPPING_CHANGEHEALTH = "Change Health";
    private final static String MAPPING_ROTATE = "Rotate";
    private final static String MAPPING_ITEM1 = "Use Item 1";
    private final static String MAPPING_ITEM2 = "Use Item 2";
    private final static String MAPPING_ITEM3 = "Use Item 3";

    // Definitions for mouse triggers for first-person look
    private final static Trigger TRIGGER_LOOK_LEFT = new MouseAxisTrigger(MouseInput.AXIS_X, true);
    private final static Trigger TRIGGER_LOOK_RIGHT = new MouseAxisTrigger(MouseInput.AXIS_X, false);
    private final static Trigger TRIGGER_LOOK_UP = new MouseAxisTrigger(MouseInput.AXIS_Y, false);
    private final static Trigger TRIGGER_LOOK_DOWN = new MouseAxisTrigger(MouseInput.AXIS_Y, true);

    // Definitions for mapping names for first-person look
    private final static String MAPPING_LOOK_LEFT = "Look Left";
    private final static String MAPPING_LOOK_RIGHT = "Look Right";
    private final static String MAPPING_LOOK_UP = "Look Up";
    private final static String MAPPING_LOOK_DOWN = "Look Down";
    
    // Defininitions triggers for movement
    private final static Trigger TRIGGER_FORWARD = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_BACKWARD = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_LEFT = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_RIGHT = new KeyTrigger(KeyInput.KEY_D);

    // Definitions for movement mapping names
    protected final static String MAPPING_FORWARD = "Move Forward";
    protected final static String MAPPING_BACKWARD = "Move Backward";
    protected final static String MAPPING_LEFT = "Move Left";
    protected final static String MAPPING_RIGHT = "Move Right";
    
    // Definition of animation mapping names
    private static final String ANI_IDLE = "stand";
    private static final String ANI_WALK = "Walk";
    
    // Definition of trigger mapping names
    private final static Trigger TRIGGER_WALK = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static String MAPPING_WALK = "Walk";

    private final InputManager inputManager;
    private final Node yawNode; // Yaw node (left/right)
    private final Node pitchNode; // Pitch node (up/down)
    
    private final AnimateModel animateModel;

    private boolean enabled = false;
    
   // Callback interfaces
    public interface ActionHandler {

    }

    public interface AnalogHandler {
        void onRotate(float intensity, float tpf);
    }

    // Handlers set by GameManager
    private ActionHandler actionHandler;
    private AnalogHandler analogHandler;

    // Constructor
    public GameInputManager(InputManager inputManager, Node yawNode, Node pitchNode, AnimateModel animateModel) {
        this.inputManager = inputManager;
        this.yawNode = yawNode;
        this.pitchNode = pitchNode;
        this.animateModel = animateModel;
    }

    // Methods to set the handlers
    public void setActionHandler(ActionHandler handler) {
        this.actionHandler = handler;
    }

    public void setAnalogHandler(AnalogHandler handler) {
        this.analogHandler = handler;
    }

    // Initialize input mappings
    public void initInputMappings() {
        // Map actions
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addMapping(MAPPING_ITEM1, TRIGGER_ITEM1);
        inputManager.addMapping(MAPPING_ITEM2, TRIGGER_ITEM2);
        inputManager.addMapping(MAPPING_ITEM3, TRIGGER_ITEM3);

        // Map look controls
        inputManager.addMapping(MAPPING_LOOK_LEFT, TRIGGER_LOOK_LEFT);
        inputManager.addMapping(MAPPING_LOOK_RIGHT, TRIGGER_LOOK_RIGHT);
        inputManager.addMapping(MAPPING_LOOK_UP, TRIGGER_LOOK_UP);
        inputManager.addMapping(MAPPING_LOOK_DOWN, TRIGGER_LOOK_DOWN);
        
        inputManager.addMapping(MAPPING_FORWARD, TRIGGER_FORWARD);
        inputManager.addMapping(MAPPING_BACKWARD, TRIGGER_BACKWARD);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_RIGHT);

        // Register listeners
        inputManager.addListener(actionListener, MAPPING_CHANGEHEALTH, MAPPING_ITEM1, MAPPING_ITEM2, MAPPING_ITEM3);
        inputManager.addListener(analogListener, MAPPING_ROTATE);
        inputManager.addListener(cameraControlListener, MAPPING_LOOK_LEFT, MAPPING_LOOK_RIGHT, MAPPING_LOOK_UP, MAPPING_LOOK_DOWN);
        inputManager.addListener(movementListener, MAPPING_FORWARD, MAPPING_BACKWARD, MAPPING_LEFT, MAPPING_RIGHT);
    
        inputManager.addMapping(MAPPING_WALK, TRIGGER_WALK);
        inputManager.addListener(actionListener, MAPPING_WALK);
    }

    public void enable() {
        if (!enabled) {
            enabled = true;

            initInputMappings();
        }
    }

    public void disable() {
        if (enabled) {
            enabled = false;

            // Remove mappings and listeners
            inputManager.deleteMapping(MAPPING_CHANGEHEALTH);
            inputManager.deleteMapping(MAPPING_ROTATE);
            inputManager.deleteMapping(MAPPING_ITEM1);
            inputManager.deleteMapping(MAPPING_ITEM2);
            inputManager.deleteMapping(MAPPING_ITEM3);
            inputManager.deleteMapping(MAPPING_LOOK_LEFT);
            inputManager.deleteMapping(MAPPING_LOOK_RIGHT);
            inputManager.deleteMapping(MAPPING_LOOK_UP);
            inputManager.deleteMapping(MAPPING_LOOK_DOWN);
            inputManager.deleteMapping(MAPPING_FORWARD);
            inputManager.deleteMapping(MAPPING_BACKWARD);
            inputManager.deleteMapping(MAPPING_LEFT);
            inputManager.deleteMapping(MAPPING_RIGHT);
            inputManager.deleteMapping(MAPPING_WALK);

            inputManager.removeListener(actionListener);
            inputManager.removeListener(analogListener);
            inputManager.removeListener(cameraControlListener);
            inputManager.removeListener(movementListener);
        }
    }

    // Action listener to handle actions
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!enabled) return;
            
            if (isPressed && actionHandler != null) {
//                switch (name) {
//                    case MAPPING_CHANGEHEALTH:
//                        actionHandler.onChangeHealth();
//                        break;
//                    case MAPPING_ITEM1:
//                        actionHandler.onUseItem(1);
//                        break;
//                    case MAPPING_ITEM2:
//                        actionHandler.onUseItem(2);
//                        break;
//                    case MAPPING_ITEM3:
//                        actionHandler.onUseItem(3);
//                        break;
//                    default:
//                        break;
//                }
            }
            if (MAPPING_WALK.equals(name)) {
                if (isPressed) {
                    animateModel.startWalking();
                } else {
                    animateModel.stopWalking();
                }
            }
        }
    };

    // Analog listener to handle analog input
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            if (!enabled) return;

            if (analogHandler != null) {
                if (name.equals(MAPPING_ROTATE)) {
                    analogHandler.onRotate(intensity, tpf);
                }
            }
        }
    };

    // Camera control listener remains the same
    private final AnalogListener cameraControlListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (!enabled) return;

            if (yawNode != null && pitchNode != null) {
                final float ROTATION_SPEED = 1.0f; // Adjust for desired sensitivity
                switch (name) {
                    case MAPPING_LOOK_LEFT:
                        yawNode.rotate(0, ROTATION_SPEED * value, 0);
                        break;
                    case MAPPING_LOOK_RIGHT:
                        yawNode.rotate(0, -ROTATION_SPEED * value, 0);
                        break;
                    case MAPPING_LOOK_UP:
                        rotatePitchNode(-ROTATION_SPEED * value);
                        break;
                    case MAPPING_LOOK_DOWN:
                        rotatePitchNode(ROTATION_SPEED * value);
                        break;
                }
            }
        }

        private void rotatePitchNode(float angle) {
            // Limit the pitch rotation to prevent flipping
            float currentPitch = pitchNode.getLocalRotation().toAngles(null)[0];
            float newPitch = currentPitch + angle;

            float maxPitch = FastMath.HALF_PI - 0.01f; // Slightly less than 90 degrees
            float minPitch = -FastMath.HALF_PI + 0.01f;

            if (newPitch > maxPitch) {
                angle = maxPitch - currentPitch;
            } else if (newPitch < minPitch) {
                angle = minPitch - currentPitch;
            }

            pitchNode.rotate(angle, 0, 0);
        }
    };
    
    // Movement listener to handle movement input
    private final AnalogListener movementListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (!enabled) return;

            if (movementHandler != null) {
                movementHandler.onMove(name, value, tpf);
            }
        }
    };
    
    // Interface for movement handling
    public interface MovementHandler {
        void onMove(String name, float value, float tpf);
    }

    // Movement handler set by GameManager
    private MovementHandler movementHandler;

    public void setMovementHandler(MovementHandler handler) {
        this.movementHandler = handler;
    }
    
}
