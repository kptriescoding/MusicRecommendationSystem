package com.example.music_recommendation_system;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        setUpStage(p, scene, stage, "home");


    }

    static void setUpStage(Player p, Scene scene, Stage stage, String present_scene) {

        System.out.println(p.songs.size());
//        boolean playing = false;
        ImageView play_pause, next, prev;
        play_pause = (ImageView) scene.lookup("#play_view");
        next = (ImageView) scene.lookup("#next_view");
        prev = (ImageView) scene.lookup("#prev_view");
        File playImage = new File("src/main/resources/images/player/play.png");
        File pauseImage = new File("src/main/resources/images/player/pause.png");

        Image image1 = new Image(playImage.toURI().toString());


        GridPane songsList = (GridPane) scene.lookup("#songs_grid_pane");
        songsList.setPadding(new Insets(10,10,10,10));
        songsList.setHgap(10);
        songsList.setVgap(10);

        int cols=3, colCnt = 0, rowCnt = 0;
        for(int i = 0;i<p.songs.size();i++){
            songsList.add(new Label(p.songs.get(i)),colCnt,rowCnt);
            colCnt++;
            if (colCnt>cols) {
                rowCnt++;
                colCnt=0;
            }
        }
        play_pause.setImage(image1);
        play_pause.setOnMouseClicked(mouseEvent -> {
            if (p.isPlaying()) {
                Image image = new Image(playImage.toURI().toString());
                play_pause.setImage(image);
                p.pause();
            } else {
                p.play();

                Image image = new Image(pauseImage.toURI().toString());
                play_pause.setImage(image);
            }
            FlowPane f = new FlowPane();

        });

        next.setOnMouseClicked(mouseEvent -> {
            Image image = new Image(pauseImage.toURI().toString());
            play_pause.setImage(image);
            p.next();
            p.setAlbumArt();
        });
        prev.setOnMouseClicked(mouseEvent -> {

            Image image = new Image(pauseImage.toURI().toString());
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
            setUpStage(p, scene1, stage, "search");
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
            setUpStage(p, scene1, stage, "home");
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

    static void SetPlayListScene(Player P, Stage stage, Scene scene) {


    }

    public static void main(String[] args) {
        launch();
    }
}