package RecommedationSystem;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

import java.util.*;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

public class RecommenderSystem extends MLTools{
    Table df_songs, df_songs_data, df_user;
    int no_of_songs, no_of_similar_songs, no_of_users;
    String songPath,userTablePath;
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
        return df_user.sortOn("CosineSimilarity").first(no_of_users);
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
        System.out.println(songArr);
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
    public static void main(String[] args){
        String songPath="src/main/java/RecommedationSystem/song_data.csv";
String userTablePath = "src/main/java/RecommedationSystem/user_table.csv";
       Table osme=new RecommenderSystem(songPath,userTablePath).getRecommendation("user300");
        System.out.println(osme);
//       Playlist playlist=new Playlist(osme);
//        new RecommenderSystem(songPath,userTablePath).updateFrequency("song4","user0");
    }
}
