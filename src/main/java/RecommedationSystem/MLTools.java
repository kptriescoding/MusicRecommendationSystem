package RecommedationSystem;

import tech.tablesaw.api.Row;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class MLTools implements CosineSimilarity,LabelEncoder{
    public Integer[] tranform(ArrayList<String> input){
        Integer[] endodedArray = new Integer[input.size()];
        int ind=0;
        for (String str:input){
            index.putIfAbsent(str,index.size()+1);
            endodedArray[ind++]=index.get(str);
        }
        return endodedArray;
    }
    public Double cosineSimilarity(ArrayList<Integer> vec1,ArrayList<Integer> vec2){
        if (vec1 == null || vec2 == null || vec1.size() <1 || vec2.size() < 1 || vec1.size() != vec2.size())
            return Double.NaN;
        double sum = 0.0, sum_a = 0, sum_b = 0;
        for (int i = 0; i < vec1.size(); i++) {
            sum += vec1.get(i) * vec2.get(i);
            sum_a += vec1.get(i) * vec1.get(i);
            sum_b += vec2.get(i) * vec2.get(i);
        }
        double val = Math.sqrt(sum_a) * Math.sqrt(sum_b);
        return abs(1-sum / val);
    }
    public ArrayList<Integer> convertRowToArray(Row song, int numberOfCol){
        ArrayList<Integer> rSong=new ArrayList<>();
        for(int i=1;i<numberOfCol;i++)
            rSong.add(song.getInt(i));
        return rSong;
    }
}
