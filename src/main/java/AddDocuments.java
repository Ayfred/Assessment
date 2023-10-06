/*
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class AddDocuments
{
    // Directory where the search index will be saved
    //private static String INDEX_DIRECTORY = "../index";
    private static String INDEX_DIRECTORY = "src/main/resources/index";
    private static String QUERY_DIRECTORY = "src/main/resources/data/cran.qry";

    public static void main(String[] args) throws IOException
    {
        // Make sure we were given something to index
*/
/*        if (args.length <= 0)
        {
            System.out.println("Expected corpus as input");
            System.exit(1);
        }*//*


        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();

        // ArrayList of documents in the corpus
        ArrayList<Document> documents = new ArrayList<Document>();

        // Open the directory that contains the search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Set up an index writer to add process and save documents to the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(directory, config);

*/
/*        for (String arg : args)
        {
            // Load the contents of the file
            System.out.printf("Indexing \"%s\"\n", arg);
            String content = new String(Files.readAllBytes(Paths.get(arg)));

            // Create a new document and add the file's contents
            Document doc = new Document();
            doc.add(new StringField("filename", arg, Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));

            // Add the file to our linked list
            documents.add(doc);
        }*//*

        File file = new File("src/main/resources/data/cran.qry");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = "";
        line = bufferedReader.readLine();

        for (int i = 0; i < 225; i++) {
            if (line.contains(".I")) {

                // Create a new document and add the file's contents
                Document doc = new Document();
                doc.add(new StringField("id", line.substring(3), Field.Store.YES));

                line = bufferedReader.readLine();
                line = bufferedReader.readLine(); // Skipping the .W line

                StringBuilder content = new StringBuilder();

                while (!line.startsWith(".I")) {
                    content.append(line);
                    line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                }
                doc.add(new TextField("content", content.toString(), Field.Store.YES));
                documents.add(doc);
            }
        }

        // Write all the documents in the linked list to the search index
        iwriter.addDocuments(documents);

        // Commit everything and close
        iwriter.close();
        directory.close();
    }
}*/
