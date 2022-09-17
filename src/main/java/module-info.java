module com.example.music_recommendation_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires tablesaw.core;

    opens MusicPlayer to javafx.fxml;
    exports MusicPlayer;

}