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

    private boolean isPlaying;
    Media currentMedia;
    SongData currentSong;
    int currentIndex = 0;
    int count = 0;
    MediaPlayer mediaPlayer;
    RecommenderSystem recommenderSystem;
    Table userRecommendation;
    User currentUser;
    HashMap<String, String> users;
    ArrayList<SongData> reccomendedSongs;
    ArrayList<SongData> allSongsReccomended;

    public Player(Stage stage, Scene scene, User user) throws InterruptedException {
        this.stage = stage;
        if (user == null) this.currentUser = new User("kptries", "Karthik Pai", "password");
        else currentUser = user;

        users = new HashMap<>();
        users.put("Dileep", "Dileep@09");
        users.put("kptries", "password");
        users.put("user1", "1010");
        users.put("user0", "162309");


        String songPath = "src/main/java/CSVFiles/song_data.csv";
        String userTablePath = "src/main/java/CSVFiles/user_table.csv";

        recommenderSystem = new RecommenderSystem(songPath, userTablePath);
        currentIndex = 0;
        userRecommendation = recommenderSystem.getAllSongs();

        Playlist playlist = new Playlist(userRecommendation);
        songs = playlist.getSongs();
        mapFromSongToImage = new HashMap<>();
        allSongsReccomended = new ArrayList<>(songs);

        reccomendedSongs = new Playlist(recommenderSystem.getRecommendation(currentUser.getUserId())).getSongs();
        File bip = new File(allSongsReccomended.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.currentSong = allSongsReccomended.get(currentIndex);
    }


    public void addAlbumArts(Scene scene) {

        for (SongData song : songs) {
            File file = new File(song.getPath());
            if (!file.isFile()) continue;
            Media media = new Media(file.toURI().toString());

            media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
                if (change.wasAdded()) {
                    if (change.getKey().equals("image")) {
                        Image image = (Image) change.getValueAdded();
                        mapFromSongToImage.put(song, image);
                        count++;
//                        System.out.println(count);
//                        stage.setScene(scene);
                    }
                    ;

                }
            });

        }

    }

    public ArrayList<SongData> getUserRecommendedSongs(User user) {
        Table t = this.recommenderSystem.getRecommendation(user.getName());
        return new Playlist(t).getSongs();
    }

    public void pause() {
        isPlaying = false;
        if(this.mediaPlayer!=null)
        this.mediaPlayer.pause();
        else this.mediaPlayer = new MediaPlayer(new Media((new File(songs.get(currentIndex).getPath())).toURI().toString()));
    }

    public void play() {
        if (currentSong == null) {
            currentSong = allSongsReccomended.get(currentIndex);
            File bip = new File(allSongsReccomended.get(currentIndex).getPath());
            Media hit = new Media(bip.toURI().toString());

            this.mediaPlayer = new MediaPlayer(hit);
            updateUser(allSongsReccomended.get(currentIndex).getSongId());
            currentMedia = hit;
        }
        this.mediaPlayer.play();
//        System.out.println(this.mediaPlayer.getCurrentTime());
        System.out.println(allSongsReccomended.get(currentIndex).getSongId());
        updateUser(allSongsReccomended.get(currentIndex).getSongId());
        isPlaying = true;
    }

    public void next() {
        if (mediaPlayer != null) this.mediaPlayer.pause();
        currentIndex = (currentIndex + 1) % allSongsReccomended.size();
        currentSong = allSongsReccomended.get(currentIndex);
        updateUser(allSongsReccomended.get(currentIndex).getSongId());
        File bip = new File(allSongsReccomended.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        currentMedia = hit;
        isPlaying = true;
    }

    public void prev() {

        this.mediaPlayer.pause();
        if (currentIndex == 0) currentIndex = allSongsReccomended.size() - 1;
        else currentIndex = currentIndex - 1;
        currentSong = songs.get(currentIndex);
        updateUser(allSongsReccomended.get(currentIndex).getSongId());
        File bip = new File(allSongsReccomended.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        currentMedia = hit;
        isPlaying = true;
    }

    boolean isPlaying() {
        return isPlaying;
    }


    public void playSongWithId(int id) {
        if (id == currentIndex) return;
        if (this.mediaPlayer != null)
            this.mediaPlayer.pause();
        currentIndex = id;
        currentSong = allSongsReccomended.get(currentIndex);
        File bip = new File(allSongsReccomended.get(currentIndex).getPath());
        Media hit = new Media(bip.toURI().toString());
        this.mediaPlayer = new MediaPlayer(hit);
        this.mediaPlayer.play();
        currentMedia = hit;
        isPlaying = true;
        updateUser(allSongsReccomended.get(currentIndex).getSongId());

    }



    public int getActualId(String id) {
        for (int i = 0; i < allSongsReccomended.size(); i++) {
            if ((allSongsReccomended.get(i).getSongId()).equals(id)) return i;
        }
        System.out.println("Not Found");
        return -1;
    }

    public void updateAlbumArt(Scene scene) {
        SongData currentSong = allSongsReccomended.get(currentIndex);
        ImageView album_art = (ImageView) scene.lookup("#album_art_current_song");
        album_art.setImage(mapFromSongToImage.get(currentSong));
    }

    private void updateUser(String songId) {
        recommenderSystem.updateFrequency(songId, currentUser.getUserId());
    }

    public boolean hasUser(String name) {
        return users.containsKey(name);
    }

    public boolean matchPassowrd(String name, String password) {
        return users.containsKey(name) && users.get(name).equals(password);
    }
    public SongData getCurrentSong(){
        return currentSong;
    }

    public void updtePlayList(String scene) {
        if (scene.equals("home")) {
            reccomendedSongs= new Playlist(recommenderSystem.getRecommendation(currentUser.getUserId())).getSongs();
            songs = reccomendedSongs;
            if(!isPlaying)
            pause();
        } else {
            songs = allSongsReccomended;
            if(!isPlaying)pause();
        }

    }


}




