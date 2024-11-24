# Romanian Information Retrieval System

The following project is designed to implement an Information Retrieval (IR) system for documents written in Romanian. It uses **Apache Lucene** for efficient full-text indexing and searching, while also addressing language-specific challenges such as diacritics, stemming, and stop words.

The IR system is split into two core functionalities: **indexing** and **searching**. As far as the implementation goes, the solution is split into 3 main classes:

- `Indexer`: Responsible for separate document parsing and processing which are then fed into the indexer. It applies language processing techniques to ensure that searches are relevant, especially given the nuances of the Romanian language.
- `Searcher`: Loads index and performs search on parsed and processed query string. 
- `Main`: Handles arguments processing, calls either `Indexer` for documents indexing initialization or `Searcher` for query parsing and execution.

In addition, `TextProcessor` class consists of common methods for stemming and diacritics removal.

### Text Preprocessing

#### 1. **Diacritics Removal**
Diacritical marks create inconsistencies in how words are stored and queried. To solve this issue, we remove diacritics from both the documents during indexing and from the queries during searching. This means that words like "cămașă" (with diacritics) and "camasa" (without diacritics) are treated as equivalent. The diacritic removal process ensures the system can match documents regardless of whether the user includes diacritics in the search query.

#### 2. **Stemming**
Stemming is applied to reduce words to their root form. This is important because Romanian, like many other languages, has rich morphology. For example, words like "mamei", "mamele", and "mama" are different forms of the same root word. To account for this, the system uses **Lucene's Romanian stemmer**, which reduces words to their base form, such as converting "mamei" and "mamele" to "mama". This increases the recall of the search, ensuring that documents containing different word forms are retrieved.

#### 3. **Stop Word Removal**
Stop words are removed during both indexing and searching. A list of Romanian stop words is used for this purpose, ensuring that the system doesn’t waste resources indexing or searching for common words that would dilute search relevance.

The list of stop words is loaded from an external file (`stopwords.txt`), which can be customized as needed.

### Indexing

The **Indexer** class is responsible for indexing documents. It uses **Apache Tika** to extract text content from various document formats such as `.txt`, `.docx`, and `.pdf`. After extracting the content, the text is preprocessed: diacritics are removed, stemming is applied, and stop words are filtered out. Then, the processed content is indexed using **Lucene**.

- **Lucene Document**: Each document in the index consists of two fields: the document's content and its filename. The content is stored as a **TextField** (Lucene's datatype for full-text fields), which allows for efficient searching. The filename is stored as a **TextField** as well, allowing the system to return the name of the file in search results.

- **Tika**: Apache Tika is used to extract text from documents in various formats. Tika supports numerous file types, including `.txt`, `.docx`, `.pdf`, and many others, making it a flexible tool for extracting document content without worrying about the underlying format.

### Searching

The **Searcher** class is responsible for executing search queries against the indexed data. When a user provides a query, the system first preprocesses the query text: diacritics are removed, stemming is applied, and stop words are filtered. This ensures that the search matches the documents correctly, regardless of minor variations in how the query is written.

- **Lucene QueryParser**: The preprocessed query is parsed using Lucene’s **QueryParser**. This tool allows the system to interpret complex queries, supporting various query types like phrase queries and term queries. The query is then executed, and Lucene returns a list of matching documents, ranked by relevance.

- **Query Matching**: The Lucene index stores tokens and their occurrences across all documents. When a query is executed, Lucene matches the processed query terms with the indexed tokens, ranks the documents by relevance, and returns the best matches.

### Command-Line Interface and Argument Parsing

The **Main** class serves as the entry point for the application and manages the user’s interaction with the system through command-line arguments. The program can either index documents or perform a search query depending on the arguments provided.

#### Argument Parsing:
The program uses **Apache Commons CLI** to parse the command-line arguments. The options supported are:

- **index**: Trigger the indexing process for the documents in the specified directory.
- **search**: Perform a search based on a query string.
- **directory**: The directory containing the documents to index (required when indexing).
- **query**: The search query string (required when searching).

The main logic flow in **Main** class follows these steps:

1. The arguments are parsed using **CommandLineParser** from Apache Commons CLI.
2. If the **index** option is provided along with a valid directory, the **indexDocs** method is called to index all documents from that directory.
3. If the **search** option is provided along with a query, the **search** method is invoked to execute the query on the indexed documents.
4. If neither set of options is provided or if required arguments are missing, the system prints the usage instructions.

This approach allows the user to run the program from the command line, providing flexibility for both indexing new documents and querying the existing index.

### Libraries Used

1. **Apache Lucene**:
    - **Lucene Core**: The core library for full-text indexing and searching. It provides the functionality for creating indexes, adding documents, parsing queries, and scoring results.
    - **Lucene Analysis Common**: This library provides pre-built analyzers, tokenizers, and filters for processing text. It includes the Romanian stemmer used in this project.
    - **Lucene QueryParser**: A tool for parsing and interpreting search queries, allowing for flexible and powerful query syntax.

2. **Apache Tika**:  
   Tika is used for content extraction from documents of various formats. It abstracts away the complexities of parsing different file types, enabling the system to handle `.txt`, `.docx`, `.pdf`, and other file formats without needing custom parsers for each.

3. **Commons CLI**:  
   The **Commons CLI** library is used to handle command-line arguments. This allows users to specify whether they want to index documents or search the index, and to provide paths to documents or search queries.

4. **Apache PDFBox**:  
   PDFBox is used for extracting text from PDF files. This is necessary since Tika alone might not handle all PDF types effectively, and PDFBox provides a more tailored approach for extracting content from PDFs.
