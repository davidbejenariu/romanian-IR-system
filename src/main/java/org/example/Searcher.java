package org.example;

import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.file.Paths;

public class Searcher {
    public void search(String queryString, String indexPath) throws Exception {
        FSDirectory dir = FSDirectory.open(Paths.get(indexPath));
        if (Main.isFolderEmpty(new File(indexPath)))
        {
            throw new Exception("No index found. Make sure to first run the script using '-index' option.");
        }

        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
        RomanianAnalyzer analyzer = new RomanianAnalyzer();
        QueryParser parser = new QueryParser("content", analyzer);

        String processedQuery = TextProcessor.removeDiacritics(TextProcessor.applyStemming(queryString));
        Query query = parser.parse(processedQuery);

        TopDocs results = searcher.search(query, 5);
        for (var hit : results.scoreDocs) {
            Document doc = searcher.doc(hit.doc);
            System.out.println(doc.get("filename"));
        }
    }
}
