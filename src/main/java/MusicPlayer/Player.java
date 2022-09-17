package MusicPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import RecommedationSystem.Playlist;
import RecommedationSystem.RecommenderSystem;
import RecommedationSystem.SongData;
import javafx.application.Preloader;
import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;


public class Player {
    ArrayList<SongData> songs;
    HashMap<SongData, Image> mapFromSongToImage;
    Stage stage;
    Scene scene;
    private boolean isPlaying;
    Media currentMedia;
    SongData currentSong;
    int currentIndex = 0;
    int count = 0;
    MediaPlayer mediaPlayer;


    public Player(Stage stage,Scene scene) throws InterruptedException {
        this.stage = stage;
        this.scene = scene;


        String songPath = "src/main/java/CSVFiles/song_data.csv";
        String userTablePath = "src/main/java/CSVFiles/user_table.csv";



        RecommenderSystem recommenderSystem = new RecommenderSystem(songPath, userTablePath);
        Table userRecommnedation = recommenderSystem.searchBarRecommender("");
        Playlist playlist = new Playlist(userRecommnedation);
        songs = playlist.getSongs();
        mapFromSongToImage = new HashMap<>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            }
        });

        t.start();

        File bip = new File(songs.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
    }


    public void addAlbumArts() {

        for (SongData song : songs) {
            File file = new File(song.getPath());
            Media media = new Media(file.toURI().toString());

            media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
                if (change.wasAdded()) {
                    if (change.getKey().equals("image")) {
                        Image image = (Image) change.getValueAdded();
                        mapFromSongToImage.put(song, image);
                        count++;
                        System.out.println(count);
                        stage.setScene(scene);
                    }
                    ;

                }
            });

        }

    }

    public void fetchSongs(String p) {

//        File f = new File(p);
//        File[] l = f.listFiles();
//        for (File x : l) {
//            if (x == null) return;
//            if (x.isHidden() || !x.canRead()) continue;
//            if (x.isDirectory()) fetchSongs(x.getPath());
//            else if (x.getName().endsWith(".mp3"))
//                songs.add(x.getPath());
//        }

    }

    public void pause() {
        isPlaying = false;
        this.mediaPlayer.pause();
    }

    public void play() {
        if(currentSong==null) {
            currentSong = songs.get(currentIndex);
            File bip = new File(songs.get(currentIndex).getPath());
            Media hit = new Media(bip.toURI().toString());

            this.mediaPlayer = new MediaPlayer(hit);
            currentMedia = hit;
        }
        this.mediaPlayer.play();
        System.out.println(this.mediaPlayer.getCurrentTime());

        isPlaying = true;
    }

    public void next() {
        if (mediaPlayer != null) this.mediaPlayer.pause();
        currentIndex = (currentIndex + 1) % songs.size();
        File bip = new File(songs.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        currentMedia = hit;
        isPlaying = true;
    }

    public void prev() {

        this.mediaPlayer.pause();
        if (currentIndex == 0) currentIndex = songs.size() - 1;
        else currentIndex = currentIndex - 1;
        File bip = new File(songs.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        currentMedia = hit;
        isPlaying = true;
    }

    boolean isPlaying() {
        return isPlaying;
    }


    public void playSongWithId(int id){
        if(id==currentIndex) return;
        if(this.mediaPlayer!=null)
            this.mediaPlayer.pause();
        currentIndex = id;
        File bip = new File(songs.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        currentMedia = hit;
        isPlaying = true;

    }


    public void updateAlbumArt(Scene scene){
        SongData currentSong = songs.get(currentIndex);
        ImageView album_art = (ImageView) scene.lookup("#album_art_current_song");
        album_art.setImage(mapFromSongToImage.get(currentSong));
    }
}




