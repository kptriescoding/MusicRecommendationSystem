package com.example.music_recommendation_system;

import RecommedationSystem.SongData;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class HelloApplication extends Application {
    ImageView play_pause, next, prev;
    Button search;
    static Player p;
    static Stage universalStage;
    static Scene homeScene, playListScene;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {

//        p.fetchSongs("src/main/resources/songs");
        FXMLLoader homeLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        FXMLLoader playListLoader = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
        FXMLLoader searchLoader = new FXMLLoader(HelloApplication.class.getResource("search.fxml"));
        universalStage = stage;
        homeScene = new Scene(homeLoader.load(), 1280, 720);
        playListScene = new Scene(playListLoader.load(), 1280, 720);
        p = new Player(stage,homeScene);
        p.addAlbumArts();


        universalStage.setTitle("Hello!");
        universalStage.setScene(homeScene);
        universalStage.show();
        setUpStage(homeScene, universalStage, "home");
        homeScene.addEventFilter(KeyEvent.KEY_PRESSED,keyEvent -> {
            if(keyEvent.getCode()== KeyCode.SPACE){
                if (p.isPlaying()) {
                    p.pause();
                    updatePlayPause(homeScene, p);
                } else {
                    p.play();
                    updatePlayPause(homeScene, p);
                }
            }
        });
        playListScene.addEventFilter(KeyEvent.KEY_PRESSED,keyEvent -> {
            if(keyEvent.getCode()== KeyCode.SPACE){
                if (p.isPlaying()) {
                    p.pause();
                    updatePlayPause(playListScene, p);
                } else {
                    p.play();
                    updatePlayPause(playListScene, p);
                }
            }
        });


    }

    static FlowPane getSongPane(SongData song) {
        FlowPane pane = new FlowPane();
        Color col = Color.rgb(42,42,42);

        CornerRadii corn = new CornerRadii(0);

        Background background = new Background(new BackgroundFill(col,corn, Insets.EMPTY));

        Label l = new Label(song.getSongName());
        l.setTextFill(Color.rgb(254,255,254));

        pane.setOrientation(Orientation.HORIZONTAL);
        l.setMaxHeight(Double.MAX_VALUE);
        Label cnt = new Label(song.getSongId());
        cnt.setMaxHeight(Double.MAX_VALUE);
        ImageView album = new ImageView(p.mapFromSongToImage.get(song));
        album.setPreserveRatio(true);
        album.setX(pane.getLayoutX());
        l.setPrefWidth(150);
        album.prefHeight(80);
        pane.setRowValignment(VPos.BOTTOM);
        //            TRBL
        album.setFitHeight(80);
        l.setPadding(new Insets(5, 5, 5, 5));
        pane.setPadding(new Insets(15, 10, 15, 5));
        l.setAlignment(Pos.BASELINE_RIGHT);


        pane.setAlignment(Pos.CENTER);
        pane.getChildren().add(album);

        pane.getChildren().addAll(l);
//        pane.getChildren().add(cnt);
        pane.setBackground(background);

        return pane;


    }

    static void setUpStage(Scene scene, Stage stage, String present_scene) {


//        boolean playing = false;
        universalStage = stage;
        updatePlayPause(scene, p);

        if (present_scene.equals("home")) {
            HashMap<FlowPane, Integer> mapFromViewToSongId = new HashMap<>();
            ImageView play_pause, next, prev;
            play_pause = (ImageView) homeScene.lookup("#play_view");
            next = (ImageView) homeScene.lookup("#next_view");
            prev = (ImageView) homeScene.lookup("#prev_view");

            GridPane songsList = (GridPane) homeScene.lookup("#songs_grid_pane");

            songsList.setPrefWidth(1078);
            songsList.setPrefHeight(425);
//            TRBL
            songsList.setPadding(new Insets(10, 60, 10, 30));
            songsList.setHgap(20);
            songsList.setVgap(10);
            songsList.setAlignment(Pos.CENTER);
            int cols = 2, colCnt = 0, rowCnt = 0;
            for (int i = 0; i < p.songs.size(); i++) {

//                songsList.add(new ImageView(p.mapFromSongToImage.get(p.songs.get(i))), colCnt, rowCnt);
                if (colCnt == 0) {
                    RowConstraints c = new RowConstraints();
                    c.setPrefHeight(300);
                    songsList.getRowConstraints().add(c);
                }
                FlowPane pane = getSongPane(p.songs.get(i));
                songsList.add(pane, colCnt, rowCnt);
                mapFromViewToSongId.put(pane, i);
                colCnt++;
                if (colCnt > cols) {
                    rowCnt++;
                    colCnt = 0;

                }
            }
            songsList.getChildren().forEach(songPane -> {
                songPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        p.playSongWithId(mapFromViewToSongId.get(songPane));

                        updatePlayPause(homeScene, p);
                    }
                });
            });
            updatePlayPause(homeScene, p);
            play_pause.setOnMouseClicked(mouseEvent -> {
                if (p.isPlaying()) {
                    p.pause();
                    updatePlayPause(homeScene, p);
                } else {
                    p.play();
                    updatePlayPause(homeScene, p);
                }
            });

            next.setOnMouseClicked(mouseEvent -> {
                p.next();
                updatePlayPause(homeScene, p);


            });
            prev.setOnMouseClicked(mouseEvent -> {
                p.prev();
                updatePlayPause(homeScene, p);


            });


            Button search = (Button) homeScene.lookup("#search");
            Button home = (Button) homeScene.lookup("#home");
            Button playlist = (Button) homeScene.lookup("#playlist");
            search.setOnMouseClicked(mouseEvent -> {

                universalStage.close();
                universalStage.setScene(playListScene);
                universalStage.show();
                setUpStage(playListScene, universalStage, "search");
                updatePlayPause(playListScene, p);


            });
            home.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    setUpStage(homeScene,universalStage,"home");
                }
            });

        } else if (present_scene.equals("search")) {
            ImageView play_pause, next, prev;
            play_pause = (ImageView) playListScene.lookup("#play_view");
            next = (ImageView) playListScene.lookup("#next_view");
            prev = (ImageView) playListScene.lookup("#prev_view");


            updatePlayPause(playListScene, p);
            play_pause.setOnMouseClicked(mouseEvent -> {
                if (p.isPlaying()) {
                    p.pause();
                    updatePlayPause(playListScene, p);
                } else {
                    p.play();
                    updatePlayPause(playListScene, p);
                }
            });

            next.setOnMouseClicked(mouseEvent -> {
                p.next();
                updatePlayPause(playListScene, p);

            });
            prev.setOnMouseClicked(mouseEvent -> {
                p.prev();
                updatePlayPause(playListScene, p);
            });


            Button search = (Button) playListScene.lookup("#search");
            Button home = (Button) playListScene.lookup("#home");
            Button playlist = (Button) playListScene.lookup("#playlist");
            home.setOnMouseClicked(mouseEvent -> {

                universalStage.close();
                universalStage.setScene(homeScene);
                universalStage.show();
                setUpStage(homeScene, universalStage, "home");
                updatePlayPause(homeScene, p);


            });
        }
    }

    static void updatePlayPause(Scene scene, Player p) {
        System.out.println(scene.toString());
        ImageView play_pause = (ImageView) scene.lookup("#play_view");

        File playImageFile = new File("src/main/resources/images/player/play.png");
        File pauseImageFile = new File("src/main/resources/images/player/pause.png");

        Image playImage = new Image(playImageFile.toURI().toString());
        Image pauseImage = new Image(pauseImageFile.toURI().toString());

        if (p.isPlaying()) {
            play_pause.setImage(pauseImage);
        } else {
            play_pause.setImage(playImage);
        }
        p.updateAlbumArt(scene);
    }


    public static void main(String[] args) {
        launch();
    }
}


