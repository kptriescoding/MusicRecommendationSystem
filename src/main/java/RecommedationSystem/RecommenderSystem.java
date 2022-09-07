package RecommedationSystem;
import tech.tablesaw.api.*;
import tech.tablesaw.selection.Selection;

import java.util.*;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

public class RecommenderSystem extends MLTools{
    private Table dfSongs;
    final private Table dfSongsData, dfUser;
   final private int noOfSongs, noOfSimilarSongs, noOfUsers, noOfSearchSongs;
    final private String songPath,userTablePath;
    public RecommenderSystem(String songPath,String userTablePath) {
        this.songPath=songPath;
        this.userTablePath=userTablePath;
        this.dfSongs = Table.read().csv(songPath);
        this.dfSongsData = Table.create();
        dfSongsData.addColumns(dfSongs.column("SongId"));
        dfSongsData.addColumns(dfSongs.column("TotalFrequency"));
        StringColumn input = dfSongs.stringColumn("Artist");
        Integer[] encoded_output = tranform((ArrayList<String>) input.asList());
        IntColumn encoded_column=IntColumn.create("Artist",encoded_output);
        dfSongsData.addColumns(encoded_column);
        input = dfSongs.stringColumn("Language");
        encoded_output =tranform((ArrayList<String>) input.asList());
        encoded_column=IntColumn.create("Language",encoded_output);
        dfSongsData.addColumns(encoded_column);
        input = dfSongs.stringColumn("Composer");
        encoded_output = tranform((ArrayList<String>) input.asList());
        encoded_column=IntColumn.create("Composer",encoded_output);
        dfSongsData.addColumns(encoded_column);
        input = dfSongs.stringColumn("Album");
        encoded_output = tranform((ArrayList<String>) input.asList());
        encoded_column=IntColumn.create("Album",encoded_output);
        dfSongsData.addColumns(encoded_column);
        this.dfUser = Table.read().csv(userTablePath);
        this.noOfSongs = 5;
        this.noOfUsers = 20;
        this.noOfSimilarSongs = 3;
        this.noOfSearchSongs =15;
    }

    public Table findSimilarSongs(String song){
    Row songRow= dfSongsData.where(dfSongsData.stringColumn("SongId").isEqualTo(song)).row(0);
    ArrayList<Integer> rSongArr=convertRowToArray(songRow,songRow.columnCount());
    ArrayList<Integer> songArr;
    Double[] cosineSimilarities=new Double[dfSongs.rowCount()];
            for (int i = 0; i< dfSongs.rowCount(); i++){
                Row song2= dfSongsData.row(i);
                songArr=convertRowToArray(song2,song2.columnCount());
            cosineSimilarities[i]=cosineSimilarity(rSongArr, songArr);
            }
        DoubleColumn cosineColumn=DoubleColumn.create("CosineSimilarity",cosineSimilarities);
        dfSongs.addColumns(cosineColumn);
        Table returnSong = dfSongs.sortOn("CosineSimilarity").first(noOfSimilarSongs);
        dfSongs.removeColumns(cosineColumn);
        return returnSong;
}
    public Table findSimilarUsers(String user) {
        Row userRow= dfUser.where(dfUser.stringColumn("UserId").isEqualTo(user)).row(0);
        ArrayList<Integer> rUserArr=convertRowToArray(userRow,userRow.columnCount());
        ArrayList<Integer> userArr;
        Double[] cosineSimilarities=new Double[dfUser.rowCount()];
        for (int i = 0; i< dfUser.rowCount(); i++){
            Row user2= dfUser.row(i);
            userArr=convertRowToArray(user2,user2.columnCount());
            cosineSimilarities[i]=cosineSimilarity(rUserArr, userArr);
        }
        DoubleColumn cosineColumn=DoubleColumn.create("CosineSimilarity",cosineSimilarities);
        dfUser.addColumns(cosineColumn);
        Table returnUsers= dfUser.sortOn("CosineSimilarity").first(noOfUsers);
        dfUser.removeColumns(cosineColumn);
        return returnUsers;
    }
    public void addNewRow(String user){
        Row userRow= dfUser.row(0);
        userRow.setString("UserId",user);
        for(int i=1;i<userRow.columnCount();i++)
            userRow.setInt(i,0);
        dfUser.append(userRow);
        dfUser.write().csv(userTablePath);
    }
    public Table getRecommendation(String user) {
        Table userTable= dfUser.where(dfUser.stringColumn("UserId").isEqualTo(user));
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
        while(itr.hasNext()&&songArr.size()< noOfSongs){
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
        for(int i = 0; i< dfSongs.rowCount(); i++)
            if (Objects.equals(dfSongs.stringColumn("SongId").get(i), songId)){
                index = i;
                break;
            }
        IntColumn tfCol = (IntColumn) dfSongs.column("TotalFrequency");
        int tf= tfCol.get(index);
        tf+=1;
        tfCol.set(index,tf);
        dfSongs.replaceColumn("TotalFrequency",tfCol);
        dfSongs.write().csv(songPath);
        for(int i = 0; i< dfUser.rowCount(); i++)
            if (Objects.equals(dfUser.stringColumn("UserId").get(i), songId)){
                index = i;
                break;
            }
        IntColumn fCol = (IntColumn) dfUser.column(songId);
        int f= fCol.get(index);
        f+=1;
        fCol.set(index,f);
        dfUser.replaceColumn(songId,fCol);
        dfUser.write().csv(userTablePath);

    }
    public Table searchBarRecommender(String search){
        dfSongs = dfSongs.sortOn("-TotalFrequency");
        StringColumn searchColumn= dfSongs.stringColumn("SongName");
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
        Table recommendedSongs= dfSongs.where(searchFilter);
        searchFilter=searchColumn.containsString(search);
        recommendedSongs.append(dfSongs.where(searchFilter));
        recommendedSongs.append(dfSongs);
        return recommendedSongs.dropDuplicateRows().first(noOfSearchSongs);
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
