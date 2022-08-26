module com.example.music_recommendation_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.music_recommendation_system to javafx.fxml;
    exports com.example.music_recommendation_system;
}