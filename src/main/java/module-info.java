module org.example.ai_battleground {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires deeplearning4j.nn;
    requires nd4j.api;
    requires fastutil;
    requires java.desktop;
    requires oswego.concurrent;
    requires nearestneighbor.core;

    // Export the controller package to javafx.graphics
    exports controller to javafx.graphics;

    // Other exports or requires statements if needed
}
