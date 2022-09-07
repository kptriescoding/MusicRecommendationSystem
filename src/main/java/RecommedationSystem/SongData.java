package RecommedationSystem;


import tech.tablesaw.api.Row;

public class SongData {

    private String songName,artist,language,album,composer,path,songId;


    public SongData(Row row){
        songId=row.getString("SongId");
        path=row.getString("Path");
        composer=row.getString("Composer");
        album=row.getString("Album");
        language=row.getString("Language");
        artist=row.getString("Artist");
        songName= row.getString("SongName");
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}
