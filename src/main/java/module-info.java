module org.example.ai_battleground {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires deeplearning4j.nn;
    requires nd4j.api;
    requires fastutil;

    // Export the controller package to javafx.graphics
    exports controller to javafx.graphics;

    // Other exports or requires statements if needed
}
