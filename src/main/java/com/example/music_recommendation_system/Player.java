package com.example.music_recommendation_system;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import RecommedationSystem.Playlist;
import RecommedationSystem.RecommenderSystem;
import RecommedationSystem.SongData;
import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import tech.tablesaw.api.Table;


public class Player {
    ArrayList<SongData> songs;
    HashMap<SongData, Image> mapFromSongToImage;
    private boolean isPlaying;
    Media currentMedia;
    SongData currentSong;
    int currentIndex = 0;
    int count = 0;
    MediaPlayer mediaPlayer;

    public Player() throws InterruptedException {
        String songPath = "src/main/java/RecommedationSystem/song_data.csv";
        String userTablePath = "src/main/java/RecommedationSystem/user_table.csv";
        RecommenderSystem recommenderSystem = new RecommenderSystem(songPath, userTablePath);
        Table userRecommnedation = recommenderSystem.getRecommendation("");
        Playlist playlist = new Playlist(userRecommnedation);
        songs = playlist.getSongs();
        mapFromSongToImage = new HashMap<>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            }
        });

        t.start();




        System.out.println(mapFromSongToImage);
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
        currentSong = songs.get(currentIndex);
        File bip = new File(songs.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        currentMedia = hit;
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


}

class Counter {
    int count;

    Counter() {
        count = 0;
    }

    void increment() {
        count++;
    }

    int getCount() {
        return count;
    }
}

// class Example extends AsyncTask {
//    private UIController controller;
//    public Example(UIController controller) {
//        this.controller = controller;
//    }
//    @Override
//    void onPreExecute() {
//
//        //This method runs on UI Thread before background task has started
//        this.controller.updateProgressLabel("Starting Download")
//    }
//    @Override
//    void doInBackground() {
//        //This method runs on background thread
//
//        boolean downloading = true;
//
//        while (downloading){
//
//            /*
//             * Your download code
//             */
//
//            double progress = 65.5 //Your progress calculation
//            publishProgress(progress);
//        }
//    }
//    @Override
//    void onPostExecute() {
//        //This method runs on UI Thread after background task has done
//        this.controller.updateProgressLabel("Download is Done");
//    }
//    @Override
//    void progressCallback(Object... params) {
//        //This method update your UI Thread during the execution of background thread
//        double progress = (double)params[0]
//        this.controller.updateProgress(progress);
//    }
//}
