import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QueryIndexBM25 {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: java -jar file.jar AnalyzerClassName");
            System.exit(1);
        }

        String indexDir = "src/main/resources/index";
        String queryFile = "src/main/resources/data/cran.qry";
        String saveFileTxtname = "src/main/resources/save/result.txt";
        String similarityClassName = "BM25Similarity";

        int MAX_RESULTS = 50;

        String analyzerClassName = args[0];
        if(args.length == 2){
            similarityClassName = args[1];
        }

        // Initialize the IndexSearcher and Analyzer
        FSDirectory indexx = FSDirectory.open(Paths.get(indexDir));
        IndexSearcher isearcher = new IndexSearcher(DirectoryReader.open(indexx));

        Analyzer analyzer = null;
        try {
            // Load the analyzer class dynamically based on the provided name
            analyzer = (Analyzer) Class.forName(analyzerClassName).newInstance();
            isearcher.setSimilarity(createSimilarityInstance(similarityClassName));
            // Use the loaded analyzer and similarity for indexing or searching
        } catch (Exception e) {
            System.err.println("Error loading or instantiating the classes: " + e.getMessage());
            System.exit(1);
        }

        // Create a QueryParser for your specific field
        QueryParser parser = new QueryParser("content", analyzer);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(queryFile))) {
            String line;
            String index = null;
            StringBuilder content = new StringBuilder();
            int count = 0;

            //Save results in a txt file
            FileWriter writer = new FileWriter(saveFileTxtname);
            List<ResultLine> resultLines = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {

                if (line.startsWith(".I")) {
                    count++;

                    if (index != null) {
                        String queryString = content.toString().trim(); // trim leading and trailing whitespace from the query
                        if (queryString.contains("?")) { // remove the question mark if it exists
                            queryString = queryString.replace("?", "");
                        }

                        // Parse the query and search for documents
                        Query query = parser.parse(queryString);
                        ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs; // Get the set of results from the searcher

                        for (int i = 0; i < hits.length; i++) {
                            Document hitDoc = isearcher.doc(hits[i].doc);
                            String docId = hitDoc.get("index-ID");
                            System.out.println("docId: " + docId);

                            ResultLine resultLine = new ResultLine(i, docId, index, hits[i].score);
                            resultLines.add(resultLine);


                        }

                        content = new StringBuilder(); // reset the content
                    }

                    index = String.valueOf(count);
                    //index = String.valueOf(Integer.parseInt(line.substring(3)));// get the index
                } else if (line.startsWith(".W")) {// Skip the ".W" line
                    // Skip the ".W" line
                } else {
                    // Append the content lines
                    content.append(line).append(" ");
                }
            }

            // Process the last query if there is one
            if (index != null) {
                String queryString = content.toString().trim();
                if (queryString.contains("?")) {
                    queryString = queryString.replace("?", "");
                }

                // Parse the query and search for documents
                Query query = parser.parse(queryString);
                ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;

                for (int i = 0; i < hits.length; i++) {
                    Document hitDoc = isearcher.doc(hits[i].doc);
                    String docId = hitDoc.get("index-ID"); // Replace "doc_id" with the actual field name

                    // Save the results in a list
                    ResultLine resultLine = new ResultLine(i, docId, index, hits[i].score);
                    resultLines.add(resultLine);

                }


            }


            // Sort the resultLines by score
            Collections.sort(resultLines, new Comparator<ResultLine>() {
                @Override
                public int compare(ResultLine o1, ResultLine o2) {
                    return Double.compare(o2.getScore(), o1.getScore());
                }
            });

            // Save results in a txt file with ranks
            for (int rank = 1; rank <= resultLines.size(); rank++) {
                ResultLine resultLine = resultLines.get(rank - 1);
                writer.write(resultLine.getIndex()
                        + " " + resultLine.getQ()
                        + " " + resultLine.getDocId()
                        + " " + rank
                        + " " + resultLine.getScore()
                        +" " + resultLine.getAnalyzer()+ "\n");
            }

            writer.close();


        }
    }


    // A class to store the result line
    static class ResultLine {
        private final int rank;
        private final String index;
        private final double score;
        private final String docId;

        public ResultLine(int rank, String docId, String index, double score) {
            this.rank = rank;
            this.index = index;
            this.score = score;
            this.docId = docId;
        }

        public int getRank() {
            return rank;
        }

        public String getDocId() {
            return docId;
        }

        public String getIndex() {
            return index;
        }

        public double getScore() {
            return score;
        }

        public String getAnalyzer() {
            return "STANDARD";
        }

        public String getQ() {
            return "Q0";
        }
    }

    private static Similarity createSimilarityInstance(String similarityClassName) throws Exception {
        Class<?> similarityClass = Class.forName(similarityClassName);
        return (Similarity) similarityClass.newInstance();
    }
}