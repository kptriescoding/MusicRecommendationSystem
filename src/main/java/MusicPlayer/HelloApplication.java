package MusicPlayer;

import RecommedationSystem.Playlist;
import RecommedationSystem.SongData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Flow;


public class HelloApplication extends Application {
    static class Scheduler extends TimerTask {
        public void run() {


        }
    }

    ImageView play_pause, next, prev;
    Button search;
    static Player p;
    static Stage universalStage;
    static Scene homeScene, playListScene, searchScene, loginScene;
    User currentUser;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {

//        p.fetchSongs("src/main/resources/songs");


        FXMLLoader homeLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        FXMLLoader playListLoader = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
        FXMLLoader searchLoader = new FXMLLoader(HelloApplication.class.getResource("search.fxml"));
        FXMLLoader loginLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));

        universalStage = stage;
        homeScene = new Scene(homeLoader.load(), 1280, 720);
        playListScene = new Scene(playListLoader.load(), 1280, 720);
        searchScene = new Scene(searchLoader.load(), 1280, 720);
        loginScene = new Scene(loginLoader.load(), 600, 400);


        p = new Player(stage, homeScene, currentUser);
        p.addAlbumArts(homeScene);


        universalStage.setTitle("DK Music PLayer just...play it");

        universalStage.setScene(loginScene);

        TextField username, password;
        Button submit;
        username = (TextField) loginScene.lookup("#username");
        password = (TextField) loginScene.lookup("#password");
        submit = (Button) loginScene.lookup("#submit");
        submit.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                if (p.hasUser(username.getText())) {
                    if (p.matchPassowrd(username.getText(), password.getText())) {
                        universalStage.close();
                        universalStage.setScene(homeScene);
                        universalStage.show();
                        currentUser = new User(username.getText(), username.getText(), password.getText());
                        try {
                            p = new Player(stage, homeScene, currentUser);
                            p.addAlbumArts(homeScene);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2.5), ev -> {
                            setUpStage(homeScene, universalStage, "home");

                        }));
                        timeline.setCycleCount(Animation.INDEFINITE);
                        timeline.play();

                    } else {
                        System.out.println("Wrong Password... TryAgain...");
                        try {
                            submit.setText("Wrong Password... TryAgain...");
                            Thread.sleep(1000);
                            submit.setText("Login");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        submit.setOnMouseClicked(mouseEvent -> {
            if (p.hasUser(username.getText())) {
                if (p.matchPassowrd(username.getText(), password.getText())) {
                    universalStage.close();
                    universalStage.setScene(homeScene);
                    universalStage.show();
                    currentUser = new User(username.getText(), username.getText(), password.getText());
                    try {
                        p = new Player(stage, homeScene, currentUser);
                        p.addAlbumArts(homeScene);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2.5), ev -> {
                        setUpStage(homeScene, universalStage, "home");

                    }));
                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();

                } else {
                    System.out.println("Wrong Password... TryAgain...");
                    try {
                        submit.setText("Wrong Password... TryAgain...");
                        Thread.sleep(1000);
                        submit.setText("Login");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        universalStage.show();


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

            p.updtePlayList("home");
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

        if (present_scene.equals("home")) {
            updatePlayPause(homeScene, p);
            updateCurrentSongName(homeScene, p);


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
                    if ((seekbar.getMax() - seekbar.getValue()) < 4.1) {
                        p.next();
                        updatePlayPause(homeScene, p);
                        updateCurrentSongName(homeScene, p);
                    }
                }
            });



            HashMap<FlowPane, String> mapFromViewToSongId = new HashMap<>();
            GridPane songsList = (GridPane) homeScene.lookup("#songs_grid_pane");

            songsList.setPrefWidth(1078);
            songsList.setPrefHeight(425);
//            TRBL
            songsList.setPadding(new Insets(10, 60, 10, 30));
            songsList.setHgap(20);
            songsList.setVgap(10);
            songsList.setAlignment(Pos.CENTER);
            int cols = 2, colCnt = 0, rowCnt = 0;
            System.out.println(p.songs.size());
            for (int i = 0; i < p.songs.size(); i++) {

//                songsList.add(new ImageView(p.mapFromSongToImage.get(p.songs.get(i))), colCnt, rowCnt);
                if (colCnt == 0) {
                    RowConstraints c = new RowConstraints();
                    c.setPrefHeight(300);
                    songsList.getRowConstraints().add(c);
                }
                FlowPane pane = getSongPane(p.songs.get(i));
                songsList.add(pane, colCnt, rowCnt);
                mapFromViewToSongId.put(pane,p.songs.get(i).getSongId());
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
                        p.playSongWithId(p.getActualId(mapFromViewToSongId.get(songPane)));
                        updateCurrentSongName(homeScene, p);
                        updatePlayPause(homeScene, p);


                    }
                });
            });
            updatePlayPause(homeScene, p);
            updateCurrentSongName(homeScene, p);
            play_pause.setOnMouseClicked(mouseEvent -> {
                if (p.isPlaying()) {
                    p.pause();
                    updatePlayPause(homeScene, p);
                    updateCurrentSongName(homeScene, p);
                } else {
                    p.play();
                    updatePlayPause(homeScene, p);
                    updateCurrentSongName(homeScene, p);
                }
            });

            next.setOnMouseClicked(mouseEvent -> {
                p.next();
                updatePlayPause(homeScene, p);
                updateCurrentSongName(homeScene, p);

            });
            prev.setOnMouseClicked(mouseEvent -> {
                p.prev();
                updatePlayPause(homeScene, p);
                updateCurrentSongName(homeScene, p);

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
                updateCurrentSongName(searchScene, p);

            });
            home.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    setUpStage(homeScene, universalStage, "home");
                }
            });

        } else if (present_scene.equals("search")) {
            p.updtePlayList("search");
            ImageView play_pause, next, prev;
            play_pause = (ImageView) searchScene.lookup("#play_view");
            next = (ImageView) searchScene.lookup("#next_view");
            prev = (ImageView) searchScene.lookup("#prev_view");


            updatePlayPause(searchScene, p);
            updateCurrentSongName(searchScene, p);
            play_pause.setOnMouseClicked(mouseEvent -> {
                if (p.isPlaying()) {
                    p.pause();

                } else {
                    p.play();

                }
                updatePlayPause(searchScene, p);
                updateCurrentSongName(searchScene, p);
            });

            next.setOnMouseClicked(mouseEvent -> {
                p.next();
                updatePlayPause(searchScene, p);
                updateCurrentSongName(searchScene, p);


            });
            prev.setOnMouseClicked(mouseEvent -> {
                p.prev();
                updatePlayPause(searchScene, p);
                updateCurrentSongName(searchScene, p);

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
                updateCurrentSongName(searchScene, p);


            });

            Slider seekbar;
            play_pause = (ImageView) searchScene.lookup("#play_view");
            next = (ImageView) searchScene.lookup("#next_view");
            prev = (ImageView) searchScene.lookup("#prev_view");
            seekbar = (Slider) searchScene.lookup("#seekbar");


            play_pause.setOnMouseClicked(mouseEvent -> {
                if (p.isPlaying()) {
                    p.pause();

                } else {
                    p.play();

                }
                updatePlayPause(searchScene, p);
                updateCurrentSongName(searchScene, p);
            });

            next.setOnMouseClicked(mouseEvent -> {
                p.next();
                updatePlayPause(searchScene, p);
                updateCurrentSongName(searchScene, p);


            });
            prev.setOnMouseClicked(mouseEvent -> {
                p.prev();
                updatePlayPause(searchScene, p);
                updateCurrentSongName(searchScene, p);

            });


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


            HashMap<FlowPane, String> mapFromViewToSongId = new HashMap<>();
            GridPane songsList = (GridPane) searchScene.lookup("#grid_pane_search_scene");

            songsList.setPrefWidth(1078);
            songsList.setPrefHeight(425);
//            TRBL
            songsList.setPadding(new Insets(10, 60, 10, 30));
            songsList.setHgap(20);
            songsList.setVgap(10);
            songsList.setAlignment(Pos.CENTER);
            int cols = 2, colCnt = 0, rowCnt = 0;
            System.out.println(p.songs.size());
            for (int i = 0; i < p.songs.size(); i++) {

//                songsList.add(new ImageView(p.mapFromSongToImage.get(p.songs.get(i))), colCnt, rowCnt);
                if (colCnt == 0) {
                    RowConstraints c = new RowConstraints();
                    c.setPrefHeight(300);
                    songsList.getRowConstraints().add(c);
                }
                FlowPane pane = getSongPane(p.songs.get(i));
                songsList.add(pane, colCnt, rowCnt);
                mapFromViewToSongId.put(pane,p.songs.get(i).getSongId());
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
                       p.playSongWithId(p.getActualId(mapFromViewToSongId.get(songPane)));

                        updatePlayPause(searchScene, p);
                        updateCurrentSongName(searchScene, p);
                    }
                });
            });


            p.mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!seekbar.isValueChanging()) {
                    seekbar.setValue(newTime.toSeconds());
                    if ((seekbar.getMax()==seekbar.getValue())) {
                        p.next();
                        try{
                        Thread.sleep(100);}catch (Exception ignored){}
//                        seekbar.
                        updatePlayPause(searchScene, p);
                        updateCurrentSongName(searchScene, p);
                    }
                }
            });

            TextField searchBar = (TextField) searchScene.lookup("#search_bar");
            Button searchButton = (Button) searchScene.lookup("#search_button");
            GridPane listView = (GridPane) searchScene.lookup("#list_view_for_search");

            listView.setVgap(5);
            listView.setAlignment(Pos.CENTER);

            HashMap<FlowPane, String> mapFromListViewToSongId = new HashMap<>();

            searchButton.setOnMouseClicked(mouseEvent -> {
                ArrayList<SongData> lis = (new Playlist(p.recommenderSystem.searchBarRecommender(searchBar.getText()))).getSongs();
                listView.getChildren().clear();
                listView.getRowConstraints().clear();
//                seekbar.valueProperty().removeListener();
                for (int i = 0; i < lis.size(); i++) {

                    FlowPane current = getSongPaneForListView(lis.get(i), i + 1);
                    RowConstraints c = new RowConstraints();
                    c.setPrefHeight(100);
                    listView.getRowConstraints().add(c);
                    mapFromListViewToSongId.put(current, lis.get(i).getSongId());
                    listView.add(current, 0, i);

                }
                listView.getChildren().forEach(songPane -> {
                    songPane.setOnMouseClicked(new EventHandler<>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {

                            p.playSongWithId(p.getActualId(mapFromListViewToSongId.get(songPane)));
                            seekbar.valueProperty().addListener((obs, oldValue, newValue) -> {
                                if (!seekbar.isValueChanging()) {
                                    double currentTime = p.mediaPlayer.getCurrentTime().toSeconds();
                                    if (Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                                        p.mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                                    }
                                }
                            });
                            updatePlayPause(searchScene, p);
                            updatePlayPause(searchScene, p);
                            updateCurrentSongName(searchScene, p);
                        }
                    });
                });

            });


        }
    }

    static FlowPane getSongPaneForListView(SongData song, int no) {

        Label serialno, songname, songduration;
        ImageView songImage;
        FlowPane flowPane = new FlowPane();

//TRBL

//
//        Label l = new Label(song.getSongName());
//        l.setTextFill(Color.rgb(254, 255, 254));
//
//        pane.setOrientation(Orientation.HORIZONTAL);
//        l.setMaxHeight(Double.MAX_VALUE);
//        Label cnt = new Label(song.getSongId());
//        cnt.setMaxHeight(Double.MAX_VALUE);
//        ImageView album = new ImageView(p.mapFromSongToImage.get(song));
//        album.setPreserveRatio(true);
//        album.setX(pane.getLayoutX());
//        l.setPrefWidth(150);
//        album.prefHeight(80);
//        pane.setRowValignment(VPos.BOTTOM);
//        //            TRBL
//        album.setFitHeight(80);
//        l.setPadding(new Insets(5, 5, 5, 5));
//        pane.setPadding(new Insets(15, 10, 15, 5));
//        l.setAlignment(Pos.BASELINE_RIGHT);

        songImage = new ImageView(p.mapFromSongToImage.get(song));
        if(p.mapFromSongToImage.get(song)==null) {
            songImage.setImage(new Image((new File("src/main/resources/images/search/headphone.jpeg")).toURI().toString()));
        }
        songImage.setFitHeight(45);
        songImage.setFitWidth(45);
        songImage.setPreserveRatio(true);
        serialno = new Label(String.valueOf(no) + ".");
//        serialno.setPrefHeight(40);
//        serialno.setFont(Font.loadFont("ubuntu", 13));
        serialno.setTextFill(Color.rgb(255, 255, 255));
        serialno.setPadding(new Insets(5, 10, 5, 10));
        serialno.setPrefWidth(50);

        songname = new Label(song.getSongName());
//        songname.setPrefHeight(40);
//        songname.setFont(Font.loadFont("ubuntu", 13));
        songname.setTextFill(Color.rgb(255, 255, 255));
        songname.setPadding(new Insets(5, 10, 5, 10));
        songname.setAlignment(Pos.TOP_CENTER);
        songname.setPrefWidth(450);

        File bip = new File(song.getPath());
        Media hit = new Media(bip.toURI().toString());
        MediaPlayer m = new MediaPlayer(hit);

        songduration = new Label(String.valueOf(m.getTotalDuration().toSeconds()));
//        songduration.setPrefHeight(40);
//        songduration.setFont(Font.loadFont("ubuntu", 13));
        songduration.setTextFill(Color.rgb(255, 255, 255));
        songduration.setPadding(new Insets(5, 10, 5, 10));
        songduration.setPrefWidth(50);

        songImage.setX(flowPane.getLayoutX());

        flowPane.setPadding(new Insets(1, 4, 1, 1));
        flowPane.setPrefWidth(600);
//        flowPane.setPrefHeight(80);
        flowPane.setBackground(Background.EMPTY);
        flowPane.setRowValignment(VPos.CENTER);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.getChildren().addAll(songImage);
        flowPane.getChildren().add(songname);
        flowPane.getChildren().addAll(songduration);
//        flowPane.setBorder(Border.stroke(Color.rgb(255, 255, 255)));
        songImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("Image Clicked");
            }
        });
        flowPane.setOrientation(Orientation.VERTICAL);
        return flowPane;
    }

    static void updatePlayPause(Scene scene, Player p) {

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

    static void updateCurrentSongName(Scene scene, Player p) {
        Label currentSong = (Label) scene.lookup("#current_song_name");
        currentSong.setText(p.currentSong.getSongName());

    }


}


