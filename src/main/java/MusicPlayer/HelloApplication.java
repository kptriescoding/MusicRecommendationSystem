package MusicPlayer;

import RecommedationSystem.SongData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class HelloApplication extends Application {
    static class Scheduler extends TimerTask {
        public void run() {


        }
    }

    ImageView play_pause, next, prev;
    Button search;
    static Player p;
    static Stage universalStage;
    static Scene homeScene, playListScene, searchScene;
    User currentUser;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {

//        p.fetchSongs("src/main/resources/songs");


        FXMLLoader homeLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        FXMLLoader playListLoader = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
        FXMLLoader searchLoader = new FXMLLoader(HelloApplication.class.getResource("search.fxml"));
        universalStage = stage;
        homeScene = new Scene(homeLoader.load(), 1280, 720);
        playListScene = new Scene(playListLoader.load(), 1280, 720);
        searchScene = new Scene(searchLoader.load(), 1280, 720);
        p = new Player(stage, homeScene);
        p.addAlbumArts();


        universalStage.setTitle("Hello!");
        universalStage.setScene(homeScene);
        universalStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
            setUpStage(homeScene, universalStage, "home");
            homeScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    if (p.isPlaying()) {
                        p.pause();
                        updatePlayPause(homeScene, p);
                    } else {
                        p.play();
                        updatePlayPause(homeScene, p);
                    }
                }
            });
            searchScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    if (p.isPlaying()) {
                        p.pause();
                        updatePlayPause(searchScene, p);
                    } else {
                        p.play();
                        updatePlayPause(searchScene, p);
                    }
                }
            });
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }


    static FlowPane getSongPane(SongData song) {
        FlowPane pane = new FlowPane();
        Color col = Color.rgb(42, 42, 42);

        CornerRadii corn = new CornerRadii(0);

        Background background = new Background(new BackgroundFill(col, corn, Insets.EMPTY));

        Label l = new Label(song.getSongName());
        l.setTextFill(Color.rgb(254, 255, 254));

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

        if (present_scene.equals("home")) {
            Button bright = (Button) scene.lookup("#home");
            Button dull1 = (Button) scene.lookup("#like");
            Button dull2 = (Button) scene.lookup("#search");

            bright.setTextFill(Color.rgb(255, 255, 255));
            dull1.setTextFill(Color.rgb(200, 180, 200));
            dull2.setTextFill(Color.rgb(200, 180, 200));
        } else if (present_scene.equals("like")) {
            Button bright = (Button) scene.lookup("#like");
            Button dull1 = (Button) scene.lookup("#home");
            Button dull2 = (Button) scene.lookup("#search");
            bright.setTextFill(Color.rgb(255, 255, 255));

            dull1.setTextFill(Color.rgb(200, 180, 200));
            dull2.setTextFill(Color.rgb(200, 180, 200));
        } else {
            Button bright = (Button) scene.lookup("#search");
            Button dull1 = (Button) scene.lookup("#like");
            Button dull2 = (Button) scene.lookup("#home");
            bright.setTextFill(Color.rgb(255, 255, 255));

            dull1.setTextFill(Color.rgb(200, 180, 200));
            dull2.setTextFill(Color.rgb(200, 180, 200));
        }

//        boolean playing = false;
        universalStage = stage;
        updatePlayPause(scene, p);

        if (present_scene.equals("home")) {
            HashMap<FlowPane, Integer> mapFromViewToSongId = new HashMap<>();
            ImageView play_pause, next, prev;
            Slider seekbar;
            play_pause = (ImageView) homeScene.lookup("#play_view");
            next = (ImageView) homeScene.lookup("#next_view");
            prev = (ImageView) homeScene.lookup("#prev_view");
            seekbar = (Slider) homeScene.lookup("#seekbar");

            seekbar.setMax(p.mediaPlayer.getTotalDuration().toSeconds());
            seekbar.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isNowChanging) {
                    if (!isNowChanging) {
                        p.mediaPlayer.seek(Duration.seconds(seekbar.getValue()));
                    }
                }
            });
            seekbar.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!seekbar.isValueChanging()) {
                    double currentTime = p.mediaPlayer.getCurrentTime().toSeconds();
                    if (Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                        p.mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                    }
                }
            });

            p.mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!seekbar.isValueChanging()) {
                    seekbar.setValue(newTime.toSeconds());
                    if ((seekbar.getMax()-seekbar.getValue())<0.1) {
                        p.next();
                        updatePlayPause(homeScene, p);
                    }
                }
            });
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
            Button playlist = (Button) homeScene.lookup("#like");

            home.setTextFill(Color.rgb(255, 255, 255));
            search.setTextFill(Color.rgb(200, 180, 200));
            playlist.setTextFill(Color.rgb(200, 180, 200));

            search.setOnMouseEntered(mouseEvent -> search.setTextFill(Color.rgb(141, 141, 121)));
            search.setOnMouseExited(mouseEvent -> search.setTextFill(Color.rgb(200, 180, 200)));

            search.setOnMouseClicked(mouseEvent -> {

                universalStage.close();
                universalStage.setScene(searchScene);
                universalStage.show();
                setUpStage(searchScene, universalStage, "search");
                updatePlayPause(searchScene, p);


            });
            home.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    setUpStage(homeScene, universalStage, "home");
                }
            });

        } else if (present_scene.equals("search")) {
            ImageView play_pause, next, prev;
            play_pause = (ImageView) searchScene.lookup("#play_view");
            next = (ImageView) searchScene.lookup("#next_view");
            prev = (ImageView) searchScene.lookup("#prev_view");


            updatePlayPause(searchScene, p);
            play_pause.setOnMouseClicked(mouseEvent -> {
                if (p.isPlaying()) {
                    p.pause();
                    updatePlayPause(searchScene, p);
                } else {
                    p.play();
                    updatePlayPause(searchScene, p);
                }
            });

            next.setOnMouseClicked(mouseEvent -> {
                p.next();
                updatePlayPause(searchScene, p);

            });
            prev.setOnMouseClicked(mouseEvent -> {
                p.prev();
                updatePlayPause(searchScene, p);
            });


            Button search = (Button) searchScene.lookup("#search");
            Button home = (Button) searchScene.lookup("#home");
            Button playlist = (Button) searchScene.lookup("#playlist");

            search.setTextFill(Color.rgb(255, 255, 255));
            home.setTextFill(Color.rgb(200, 180, 200));
//            playlist.setTextFill(Color.rgb(200, 180, 200));

            home.setOnMouseEntered(mouseEvent -> search.setTextFill(Color.rgb(141, 141, 121)));
            home.setOnMouseExited(mouseEvent -> search.setTextFill(Color.rgb(200, 180, 200)));

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


}


