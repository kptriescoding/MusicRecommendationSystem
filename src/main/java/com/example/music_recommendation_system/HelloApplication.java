package com.example.music_recommendation_system;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
        boolean playing = false;
        Button play_id = (Button) scene.lookup("#play_id");
        play_id.setOnMouseClicked(mouseEvent -> {
            if (play_id.getText().equals("Play")) {
                play_id.setText("Pause");
            } else {
                play_id.setText("Play");
            }
        });
        ImageView play_pause = (ImageView) scene.lookup("#play_view");
        play_pause.setOnMouseClicked(mouseEvent -> {
            File f = new File("/home/dileep/Desktop/MusicRecommendationSystem/src/main/resources/images/player/pause.png");
            Image image = new Image(f.toURI().toString());
            play_pause.setImage(image);
        });
        scene = new Scene(fxmlLoader1.load(), 1280, 720);
    }

    public static void main(String[] args) {
        launch();
    }
}