import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class AddRanking {



    public static void main(String[] args) throws Exception {

        // Add Qi to the second column of the query file
        File file = new File("src/main/resources/save/result.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;

        // Create new file to save the new query file
        //FileWriter writer = new FileWriter("../result_rank.txt");

        // Read the third column of the result file and add the ranking
        int rank = 1;
        List<String> scores = new ArrayList<>();
        while ((st = br.readLine()) != null) {

            // Add Qi to the second column of the query file
            String[] parts = st.split(" ");

            // Store the third column of the result file
            scores.add(parts[2]);
        }



    }
}
