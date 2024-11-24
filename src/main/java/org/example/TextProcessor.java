package org.example;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.text.Normalizer;

public class TextProcessor {
    public static String removeDiacritics(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return string;
    }

    public static String applyStemming(String text) throws IOException {
        RomanianAnalyzer analyzer = new RomanianAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        StringBuilder stemmedText = new StringBuilder();
        while (tokenStream.incrementToken()) {
            stemmedText.append(charTermAttribute.toString()).append(" ");
        }
        tokenStream.end();
        tokenStream.close();

        return stemmedText.toString().trim();
    }
}
