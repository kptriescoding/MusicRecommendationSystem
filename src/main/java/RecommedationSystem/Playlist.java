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
   public void addSongs(Table table){
       for(int i=0;i<table.rowCount();i++)
           songs.add(new SongData(table.row(i)));
   }
   public ArrayList<SongData> getSongs(){
       return songs;
   }
}
