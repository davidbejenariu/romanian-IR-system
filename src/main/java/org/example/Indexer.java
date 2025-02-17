package org.example;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Indexer {
    private final IndexWriter writer;

    public Indexer(String indexPath) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        CharArraySet updatedStopWords = this.updateStopWords();
        RomanianAnalyzer analyzer = new RomanianAnalyzer(updatedStopWords);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        this.writer = new IndexWriter(dir, config);
    }

    public static String extractText(File file) throws Exception {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".txt")) {
            return extractTextFromTxt(file);
        } else if (fileName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (fileName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileName);
        }
    }

    private static String extractTextFromTxt(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    private static String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private static String extractTextFromDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    public void indexFile(File file) throws IOException {
        try {
            String content = extractText(file);
            System.out.println("Extracted Content:");
            System.out.println(content);

            String stemmedContent = TextProcessor.applyStemming(content);

            Document doc = new Document();
            doc.add(new TextField("content", TextProcessor.removeDiacritics(stemmedContent), TextField.Store.YES));
            doc.add(new TextField("filename", file.getName(), TextField.Store.YES));

            writer.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        writer.close();
    }

    private CharArraySet updateStopWords() throws IOException {
        String pathToStopWords = "src/main/java/org/example/stopwords.txt";
        List<String> stopWords = new ArrayList<>();

        for (String line : Files.readAllLines(Path.of(pathToStopWords))) {
            line = line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                stopWords.add(line);
            }
        }

        return new CharArraySet(stopWords, true);
    }
}
