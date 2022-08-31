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
    ImageView play_pause, next, prev;
    Button search;

    @Override
    public void start(Stage stage) throws IOException {
        Player p = new Player();
        p.fetchSongs("src/main/resources/songs");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        setUpStage(p, scene,stage);




    }

    static void setUpStage(Player p, Scene scene,Stage stage) {

        System.out.println(p.songs.size());
//        boolean playing = false;
        ImageView play_pause, next, prev;
        play_pause = (ImageView) scene.lookup("#play_view");
        next = (ImageView) scene.lookup("#next_view");
        prev = (ImageView) scene.lookup("#prev_view");
        File f1 = new File("src/main/resources/images/player/play.png");
        Image image1 = new Image(f1.toURI().toString());

        play_pause.setImage(image1);
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
        File f2 = new File("src/main/resources/images/player/next.png");
        Image image2 = new Image(f2.toURI().toString());
        play_pause.setImage(image2);
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


        Button search = (Button) scene.lookup("#search");
        Button home = (Button) scene.lookup("#home");
        Button playlist = (Button) scene.lookup("#playlist");
        search.setOnMouseClicked(mouseEvent -> {
            FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
            Scene scene1 = null;
            try {

                scene1 = new Scene(fxmlLoader1.load(), 1280, 720);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.close();
            stage.setScene(scene1);
            stage.show();
            setUpStage(p, scene1,stage);
        });
        home.setOnMouseClicked(mouseEvent -> {
            FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
            Scene scene1 = null;
            try {

                scene1 = new Scene(fxmlLoader1.load(), 1280, 720);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.close();
            stage.setScene(scene1);
            stage.show();
            setUpStage(p, scene1,stage);
        });
//        playlist.setOnMouseClicked(mouseEvent -> {
//            FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
//            Scene scene1 = null;
//            try {
//
//                scene1 = new Scene(fxmlLoader1.load(), 1280, 720);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            stage.close();
//            stage.setScene(scene1);
//            stage.show();
//            setUpStage(p, scene1,stage);
//        });

    }

    public static void main(String[] args) {
        launch();
    }
}