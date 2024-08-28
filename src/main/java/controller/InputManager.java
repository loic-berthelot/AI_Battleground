package controller;

import javafx.scene.input.KeyCode;
import java.util.Hashtable;
import static javafx.scene.input.KeyCode.*;

public class InputManager {
    final private Hashtable<KeyCode, Boolean> inputValues;
    final private Hashtable<String, KeyCode> actionInputs;
    private static InputManager instance;
    private InputManager(){
        inputValues = new Hashtable<>();
        actionInputs = new Hashtable<>();
        addActionInput("MoveUp1", Z);
        addActionInput("MoveDown1", S);
        addActionInput("MoveRight1", D);
        addActionInput("MoveLeft1", Q);
        addActionInput("MoveUp2", UP);
        addActionInput("MoveDown2", DOWN);
        addActionInput("MoveRight2", RIGHT);
        addActionInput("MoveLeft2", LEFT);
        addActionInput("Turbo", SPACE);
        addActionInput("Pause", P);
        addActionInput("Restart", R);
    }
    public static InputManager getInstance() {
        if (instance == null) instance = new InputManager();
        return instance;
    }
    public void addActionInput(String action, KeyCode input) {
        actionInputs.put(action, input);
    }
    public void setInput(KeyCode input, boolean enabled) {
        inputValues.put(input, enabled);
    }
    public boolean getInput(String action) {
        KeyCode keyCode = actionInputs.get(action);
        if (keyCode == null || !inputValues.containsKey(keyCode)) return false;
        return inputValues.get(keyCode);
    }
}
