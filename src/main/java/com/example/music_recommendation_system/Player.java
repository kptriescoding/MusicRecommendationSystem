package com.example.music_recommendation_system;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Player {
    ArrayList<String> songs;
    private boolean isPlaying;
    String currentSong;
    int currentIndex = 0;
MediaPlayer mediaPlayer;
    public Player() {

        songs = new ArrayList<>();
    }

    public void fetchSongs(String p) {

        File f = new File(p);
        File[] l = f.listFiles();
        for (File x : l) {
            if (x == null) return;
            if (x.isHidden() || !x.canRead()) continue;
            if (x.isDirectory()) fetchSongs(x.getPath());
            else if (x.getName().endsWith(".mp3"))
                songs.add(x.getPath());
        }

    }

    public void pause() {
        isPlaying = false;
        this.mediaPlayer.pause();
    }

    public void play() {
        currentSong = songs.get(currentIndex);
        File bip = new File(songs.get(currentIndex));
        Media hit = new Media(bip.toURI().toString());
         this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        isPlaying = true;
    }

    public void next(){
        this.mediaPlayer.pause();
        currentIndex = (currentIndex+1)% songs.size();
        File bip = new File(songs.get(currentIndex));
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        isPlaying = true;
    }

    public void prev(){
        this.mediaPlayer.pause();
        if(currentIndex==0) currentIndex = songs.size()-1;
        else currentIndex = currentIndex-1;
        File bip = new File(songs.get(currentIndex));
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        isPlaying = true;
    }

    boolean isPlaying() {
        return isPlaying;
    }
}
