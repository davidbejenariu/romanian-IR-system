package org.example;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.*;

public class Main {
    private static final String indexPath = "src/main/java/org/example/indexer";

    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("index", false, "Index documents");
        options.addOption("search", false, "Search in index");
        options.addOption("directory", true, "Path to the documents");
        options.addOption("query", true, "Query to search");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("index") && cmd.hasOption("directory")) {
                String pathToDocs = cmd.getOptionValue("directory");
                System.out.println("Indexing documents in: " + pathToDocs);
                indexDocs(pathToDocs);
            } else if (cmd.hasOption("search") && cmd.hasOption("query")) {
                String query = cmd.getOptionValue("query");
                System.out.println("Searching for query: " + query);
                search(query);
            } else {
                printUsage(options);
            }

        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            printUsage(options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void indexDocs(String pathToDocs) {
        try {
            File folder = new File(indexPath);
            if (!isFolderEmpty(folder)) {
                cleanup(folder, 0);
            }

            Indexer indexer = new Indexer(indexPath);

            File[] docs = new File(pathToDocs).listFiles();
            assert docs != null;
            for (File file : docs) {
                if (file.isFile()) {
                    System.out.println("Indexing file: " + file.getName());
                    indexer.indexFile(file);
                }
            }

            indexer.close();
            System.out.println("Indexing completed successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void search(String query) throws Exception {
        Searcher searcher = new Searcher();
        searcher.search(query, indexPath);
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("docsearch", options);
    }

    private static void cleanup(File folder, int level) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f: files) {
                if (f.isDirectory()) {
                    cleanup(f, level + 1);
                } else {
                    f.delete();
                }
            }
        }

        if (level > 0) {
            folder.delete();
        }
    }

    public static boolean isFolderEmpty(File folder) {
        File[] files = folder.listFiles();
        return files == null || files.length == 0;
    }
}
