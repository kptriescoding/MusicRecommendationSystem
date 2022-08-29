package com.example.music_recommendation_system;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Player p = new Player();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
        p.fetchSongs("src/main/resources/songs");
        System.out.println(p.songs.size());
        boolean playing = false;

        ImageView play_pause = (ImageView) scene.lookup("#play_view");
        ImageView next = (ImageView) scene.lookup("#next_view");
        ImageView prev = (ImageView) scene.lookup("#prev_view");
        play_pause.setOnMouseClicked(mouseEvent -> {
            if (p.isPlaying()) {

                File f = new File("src/main/resources/images/player/play.png");
                Image image = new Image(f.toURI().toString());
                play_pause.setImage(image);
                p.pause();
            } else {
                p.play();
                File f = new File("src/main/resources/images/player/pause.png");
                Image image = new Image(f.toURI().toString());
                play_pause.setImage(image);
            }

        });
        next.setOnMouseClicked(mouseEvent -> {
            File f = new File("src/main/resources/images/player/pause.png");
            Image image = new Image(f.toURI().toString());
            play_pause.setImage(image);
            p.next();
            p.setAlbumArt();
        });
        prev.setOnMouseClicked(mouseEvent -> {
            File f = new File("src/main/resources/images/player/pause.png");
            Image image = new Image(f.toURI().toString());
            play_pause.setImage(image);
            p.prev();
        });

//        scene = new Scene(fxmlLoader1.load(), 1280, 720);
//        stage.setScene(scene);
//        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}