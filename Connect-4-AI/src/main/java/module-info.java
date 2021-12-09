module com.example.connect4ai {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.connect4ai to javafx.fxml;
    exports com.example.connect4ai;
}