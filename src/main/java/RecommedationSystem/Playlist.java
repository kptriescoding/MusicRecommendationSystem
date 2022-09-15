package RecommedationSystem;

import tech.tablesaw.api.Table;

import java.util.ArrayList;

public class Playlist {
    ArrayList<SongData> songs;
   public Playlist(Table table){
       songs=new ArrayList<>();
       for(int i=0;i< table.rowCount();i++)
           songs.add(new SongData(table.row(i)));
   }


    public String toString() {
        StringBuilder S= new StringBuilder(String.format("  %-10s  |  %-50s  |  %-50s  |  %-20s  |  %-50s  |  %-20s  |  %-100s\n", "SongId", "SongName", "Artist", "Language", "Album", "Composer", "Path"));
        for(SongData song:songs){
            S.append(song.toString());
        }
        return new String(S);
    }

    public void addSongs(Table table){
       for(int i=0;i<table.rowCount();i++)
           songs.add(new SongData(table.row(i)));
   }
   public ArrayList<SongData> getSongs(){
       return songs;
   }
}
