import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
// import org.apache.lucene.store.RAMDirectory;

public class CreateIndex {

    // Directory where the search index will be saved
    //private static String INDEX_DIRECTORY = "../index";
    private static String INDEX_DIRECTORY = "src/main/resources/index";

    public static void main(String[] args) throws IOException {
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();

        File file = new File("src/main/resources/data/cran.all.1400");

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = "";
        StringBuilder title = new StringBuilder();
        StringBuilder author = new StringBuilder();
        StringBuilder biblio = new StringBuilder();
        StringBuilder content = new StringBuilder();
        String index = "";

        line = bufferedReader.readLine();


        // To store an index in memory
        // Directory directory = new RAMDirectory();
        // To store an index on disk
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // Index opening mode
        // IndexWriterConfig.OpenMode.CREATE = create a new index
        // IndexWriterConfig.OpenMode.APPEND = open an existing index
        // IndexWriterConfig.OpenMode.CREATE_OR_APPEND = create an index if it
        // does not exist, otherwise it opens it
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter iwriter = new IndexWriter(directory, config);

        for (int i = 0; i < 1400; i++) {


            if (line.contains(".I")) {
                index = line.substring(3);

                System.out.println("index " + index);
                line = bufferedReader.readLine();

            }

            if (line.contains(".T")) {

                while (!line.contains(".A")) {
                    line = bufferedReader.readLine();

                    if (!line.contains(".A")) {
                        title.append(line);
                    }
                }
            }
            if (line.contains(".A")) {
                while (!line.contains(".B")) {
                    line = bufferedReader.readLine();

                    if (!line.contains(".B")) {
                        author.append(line);
                    }
                }
            }
            if (line.contains(".B")) {
                while (!line.contains(".W")) {
                    line = bufferedReader.readLine();

                    if (!line.contains(".W")) {
                        biblio.append(line);
                    }
                }
            }
            if (line.contains(".W")) {
                while (!line.contains(".I")) {
                    line = bufferedReader.readLine();

                    //If the line is null, break out of the loop
                    if (line == null) {
                        break;
                    }

                    if (!line.contains(".I")) {
                        content.append(line);
                    }
                }
            }


            System.out.println("index " + index);
            System.out.println("title " + title);
            System.out.println("author " + author);
            System.out.println("biblio " + biblio);
            System.out.println("content " + content);
            System.out.println("------------------------------------------------");


            // Create a new document
            Document doc = new Document();
            doc.add(new TextField("index", index, Field.Store.YES));
            doc.add(new TextField("title", title.toString(), Field.Store.YES));
            doc.add(new TextField("author", author.toString(), Field.Store.YES));
            doc.add(new TextField("biblio", biblio.toString(), Field.Store.YES));
            doc.add(new TextField("content", content.toString(), Field.Store.YES));

            // Save the document to the index
            iwriter.addDocument(doc);

            //Reset all variables
            index = "";
            title = new StringBuilder();
            author = new StringBuilder();
            biblio = new StringBuilder();
            content = new StringBuilder();
        }

        // Commit changes and close everything
        iwriter.close();
        directory.close();
    }
}