package RecommedationSystem;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

import java.util.*;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

public class RecommenderSystem extends MLTools{
    private Table df_songs;
    final private Table  df_songs_data, df_user;
   final private int no_of_songs, no_of_similar_songs, no_of_users,no_of_search_songs;
    final private String songPath,userTablePath;
    public RecommenderSystem(String songPath,String userTablePath) {
        this.songPath=songPath;
        this.userTablePath=userTablePath;
        this.df_songs = Table.read().csv(songPath);
        this.df_songs_data = Table.create();
        df_songs_data.addColumns(df_songs.column("SongId"));
        df_songs_data.addColumns(df_songs.column("TotalFrequency"));
        StringColumn input = df_songs.stringColumn("Artist");
        Integer[] encoded_output = tranform((ArrayList<String>) input.asList());
        IntColumn encoded_column=IntColumn.create("Artist",encoded_output);
        df_songs_data.addColumns(encoded_column);
        input = df_songs.stringColumn("Language");
        encoded_output =tranform((ArrayList<String>) input.asList());
        encoded_column=IntColumn.create("Language",encoded_output);
        df_songs_data.addColumns(encoded_column);
        input = df_songs.stringColumn("Composer");
        encoded_output = tranform((ArrayList<String>) input.asList());
        encoded_column=IntColumn.create("Composer",encoded_output);
        df_songs_data.addColumns(encoded_column);
        input = df_songs.stringColumn("Album");
        encoded_output = tranform((ArrayList<String>) input.asList());
        encoded_column=IntColumn.create("Album",encoded_output);
        df_songs_data.addColumns(encoded_column);
        this.df_user = Table.read().csv(userTablePath);
        this.no_of_songs = 5;
        this.no_of_users = 20;
        this.no_of_similar_songs = 3;
        this.no_of_search_songs=15;
    }

    public Table findSimilarSongs(String song){
    Row songRow=df_songs_data.where(df_songs_data.stringColumn("SongId").isEqualTo(song)).row(0);
    ArrayList<Integer> rSongArr=convertRowToArray(songRow,songRow.columnCount());
    ArrayList<Integer> songArr;
    Double[] cosineSimilarities=new Double[df_songs.rowCount()];
            for (int i=0;i<df_songs.rowCount();i++){
                Row song2=df_songs_data.row(i);
                songArr=convertRowToArray(song2,song2.columnCount());
            cosineSimilarities[i]=cosineSimilarity(rSongArr, songArr);
            }
        DoubleColumn cosineColumn=DoubleColumn.create("CosineSimilarity",cosineSimilarities);
        df_songs.addColumns(cosineColumn);
        Table returnSong = df_songs.sortOn("CosineSimilarity").first(no_of_similar_songs);
        df_songs.removeColumns(cosineColumn);
        return returnSong;
}
    public Table findSimilarUsers(String user) {
        Row userRow=df_user.where(df_user.stringColumn("UserId").isEqualTo(user)).row(0);
        ArrayList<Integer> rUserArr=convertRowToArray(userRow,userRow.columnCount());
        ArrayList<Integer> userArr;
        Double[] cosineSimilarities=new Double[df_user.rowCount()];
        for (int i=0;i<df_user.rowCount();i++){
            Row user2=df_user.row(i);
            userArr=convertRowToArray(user2,user2.columnCount());
            cosineSimilarities[i]=cosineSimilarity(rUserArr, userArr);
        }
        DoubleColumn cosineColumn=DoubleColumn.create("CosineSimilarity",cosineSimilarities);
        df_user.addColumns(cosineColumn);
        Table returnUsers=df_user.sortOn("CosineSimilarity").first(no_of_users);
        df_user.removeColumns(cosineColumn);
        return returnUsers;
    }
    public void addNewRow(String user){
        Row userRow=df_user.row(0);
        userRow.setString("UserId",user);
        for(int i=1;i<userRow.columnCount();i++)
            userRow.setInt(i,0);
        df_user.append(userRow);
        df_user.write().csv(userTablePath);
    }
    public Table getRecommendation(String user) {
        Table userTable=df_user.where(df_user.stringColumn("UserId").isEqualTo(user));
        if(userTable.rowCount()==0){
            addNewRow(user);
        }
       Table dfSimilarUser = this.findSimilarUsers(user);
        Table freqSum = dfSimilarUser.summarize(dfSimilarUser.columnNames(),sum).apply();
        TreeMap<Double,String> topSongs = new TreeMap<>(Collections.reverseOrder());
        String songName;
        for(int i=2;i<freqSum.columnCount()-1;i++) {
            songName=freqSum.doubleColumn(i).name();
            songName=songName.split("\\[")[1].split("\\]")[0];
            if(Objects.equals(songName, "CosineSimilarity"))continue;
            topSongs.put(freqSum.doubleColumn(i).getDouble(0),songName );
        }
        ArrayList<String> songArr=new ArrayList<>();
        Iterator itr=topSongs.keySet().iterator();
        while(itr.hasNext()&&songArr.size()<no_of_songs){
            songArr.add(topSongs.get((Double) itr.next()));
        }
        Table reqSongs=null;
        for(int i=0;i<songArr.size();i++) {
        String curSong = songArr.get(i);
        Table curSimSong = this.findSimilarSongs(curSong);
            if(reqSongs==null)
                reqSongs = curSimSong;
            else
        for(int j=0;j<curSimSong.rowCount();j++)
            reqSongs.append(curSimSong.row(j));

        }
        return reqSongs;
    }
    public void updateFrequency(String songId,String user){
        int index=-1;
        for(int i=0;i<df_songs.rowCount();i++)
            if (Objects.equals(df_songs.stringColumn("SongId").get(i), songId)){
                index = i;
                break;
            }
        IntColumn tfCol = (IntColumn) df_songs.column("TotalFrequency");
        int tf= tfCol.get(index);
        tf+=1;
        tfCol.set(index,tf);
        df_songs.replaceColumn("TotalFrequency",tfCol);
        df_songs.write().csv(songPath);
        for(int i=0;i<df_user.rowCount();i++)
            if (Objects.equals(df_user.stringColumn("UserId").get(i), songId)){
                index = i;
                break;
            }
        IntColumn fCol = (IntColumn) df_user.column(songId);
        int f= fCol.get(index);
        f+=1;
        fCol.set(index,f);
        df_user.replaceColumn(songId,fCol);
        df_user.write().csv(userTablePath);

    }
    public Table searchBarRecommender(String search){
        df_songs=df_songs.sortOn("-TotalFrequency");
        StringColumn searchColumn=df_songs.stringColumn("SongName");
        String temp;
        ArrayList<String> originalSongsName=new ArrayList<>();
        for(int i=0;i<searchColumn.size();i++){
            temp=searchColumn.get(i);
            originalSongsName.add(temp);
            temp=temp.toLowerCase();
            searchColumn.set(i,temp);
        }
        Selection searchFilter=searchColumn.startsWith(search);
        for(int i=0;i<searchColumn.size();i++)
            searchColumn.set(i,originalSongsName.get(i));
        Table recommendedSongs=df_songs.where(searchFilter);
        searchFilter=searchColumn.containsString(search);
        recommendedSongs.append(df_songs.where(searchFilter));
        recommendedSongs.append(df_songs);
        return recommendedSongs.dropDuplicateRows().first(no_of_search_songs);
    }
    public static void main(String[] args){
        String songPath="src/main/java/RecommedationSystem/song_data.csv";
String userTablePath = "src/main/java/RecommedationSystem/user_table.csv";

// Search Recommender
RecommenderSystem recommenderSystem=new RecommenderSystem(songPath,userTablePath);
        Table searchResults = recommenderSystem.searchBarRecommender("paat");
        System.out.println("Search Results");
        Playlist recommendedSongs=new Playlist(searchResults);
        System.out.println(recommendedSongs +"\n\n\n");

        //User Recommendation
        System.out.println("User Recommended Songs");
       Table userRecommnedation=recommenderSystem.getRecommendation("user5");
       Playlist playlist=new Playlist(userRecommnedation);
        System.out.println(playlist);


        //Update Frequency
        recommenderSystem.updateFrequency("song4","user0");
    }
}
