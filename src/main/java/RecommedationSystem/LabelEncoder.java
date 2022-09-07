package RecommedationSystem;

import java.util.ArrayList;
import java.util.HashMap;

public interface LabelEncoder {
    HashMap<String,Integer> index = new HashMap<>();
    public Integer[] tranform(ArrayList<String> input);
}
