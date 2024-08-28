package controller;

public class InputBuffer {
    private String input;
    private boolean inputBuffer;
    public InputBuffer(String input){
        this.input = input;
        inputBuffer = false;
    }
    public boolean read(){
        if (InputManager.getInstance().getInput(input)) {
            if (!inputBuffer) {
                inputBuffer = true;
                return true;
            }
        } else {
            inputBuffer = false;
        }
        return false;
    }

}
