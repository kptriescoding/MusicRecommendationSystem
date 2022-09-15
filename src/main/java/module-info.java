module com.example.music_recommendation_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires tablesaw.core;

    opens com.example.music_recommendation_system to javafx.fxml;
    exports com.example.music_recommendation_system;

}