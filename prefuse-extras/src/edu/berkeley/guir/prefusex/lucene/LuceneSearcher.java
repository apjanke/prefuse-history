package edu.berkeley.guir.prefusex.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * LuceneSearcher
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class LuceneSearcher {

    public static final String FIELD = "prefuse-text";
    public static final String ID = "prefuse-id";
    
    private Directory directory;
    private Analyzer analyzer;
    private QueryParser parser;
    
	private Searcher searcher;
	private IndexReader reader;
    private IndexWriter writer;
    private boolean m_readMode = true;
    
    public LuceneSearcher() {
        directory = new RAMDirectory();
        analyzer = new StandardAnalyzer();
        parser = new QueryParser(FIELD, analyzer);
        try {
            writer = new IndexWriter(directory, analyzer, true);
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        setReadMode(false);
    } //
    
    public void setReadMode(boolean mode) {
        // do nothing if already in the mode
        if ( m_readMode == mode ) return;
        // otherwise switch modes
        if ( !mode ) {
            // close any open searcher and reader
            try {
    			if ( searcher != null ) searcher.close();
    			if ( reader   != null ) reader.close();
    		} catch ( Exception e ) {
    			e.printStackTrace();
    		}
    		// open the writer
    		try {
                writer = new IndexWriter(directory, analyzer, false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            // optimize index and close writer
            try {
                if ( writer != null ) {
                    writer.optimize();
                    writer.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // open the reader and searcher
            try {
    	        reader = IndexReader.open(directory);
    	        searcher = new IndexSearcher(reader);
    	    } catch ( Exception e ) {
    	        e.printStackTrace();
    	    }
        }
        m_readMode = mode;
    } //
    
    /**
     * Searches the Lucene index using the given query String, returns an object
     * which provides access to the search results.
     * @param query
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public Hits search(String query) throws ParseException, IOException {
        if ( m_readMode ) {
            Query q = parser.parse(query);
            return searcher.search(q);
        } else {
            throw new IllegalStateException(
	                "Searches can only be performed when " +
	                "the LuceneSearcher is in read mode");
        }
    } //
    
    /**
     * Add a document to the Lucene search index.
     * @param d
     */
	public void addDocument(Document d) {
	    if ( !m_readMode ) {
		    try {
				writer.addDocument(d);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    } else {
	        throw new IllegalStateException(
	                "Documents can not be added to the index unless" +
	                "the LuceneSearcher is not in read mode");
	    }
	} //
	
    /**
     * @return Returns the analyzer.
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    } //
    
    /**
     * @param analyzer The analyzer to set.
     */
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    } //
    
    /**
     * @return Returns the parser.
     */
    public QueryParser getQueryParser() {
        return parser;
    } //
    
    /**
     * @param parser The parser to set.
     */
    public void setQueryParser(QueryParser parser) {
        this.parser = parser;
    } //
    
} // end of class LuceneSearcher
