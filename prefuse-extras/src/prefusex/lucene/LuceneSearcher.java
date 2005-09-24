package prefusex.lucene;

import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
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
    private String[] fields;
    
	private Searcher searcher;
	private IndexReader reader;
    private IndexWriter writer;
    private boolean m_readMode = true;
    private boolean m_readOnly = false;
    
    private HashMap m_hitCountCache;
        
    public LuceneSearcher() {
        this(new RAMDirectory(), FIELD, false);
    } //
    
    public LuceneSearcher(Directory dir, String field, boolean readOnly) {
        this(dir, new String[]{field}, readOnly);
    } //
    
    public LuceneSearcher(Directory dir, String[] fields, boolean readOnly) {
        m_hitCountCache = new HashMap();
        directory = dir;
        analyzer = new StandardAnalyzer();
        this.fields = (String[])fields.clone();
        try {
            // TODO check if it exists first, then create on fail
            writer = new IndexWriter(directory, analyzer, !readOnly);
            writer.close();
            writer = null;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        m_readOnly = readOnly;
        if ( !readOnly ) {
            setReadMode(false);
        } else {
            m_readMode = false;
            setReadMode(true);
        }
    } //
    
    public boolean setReadMode(boolean mode) {
        // return false if this is read-only
        if ( m_readOnly && mode == false ) return false;
        // do nothing if already in the mode
        if ( m_readMode == mode ) return true;
        // otherwise switch modes
        if ( !mode ) {
            // close any open searcher and reader
            try {
    			if ( searcher != null ) searcher.close();
    			if ( reader   != null ) reader.close();
    		} catch ( Exception e ) {
    			e.printStackTrace();
    			return false;
    		}
    		// open the writer
    		try {
                writer = new IndexWriter(directory, analyzer, false);
            } catch (IOException e1) {
                e1.printStackTrace();
                return false;
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
                return false;
            }
            // open the reader and searcher
            try {
    	        reader = IndexReader.open(directory);
    	        searcher = new IndexSearcher(reader);
    	    } catch ( Exception e ) {
    	        e.printStackTrace();
    	        return false;
    	    }
        }
        m_readMode = mode;
        return true;
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
            Query q;
            if ( fields.length == 1 ) {
                q = QueryParser.parse(query, fields[0], analyzer);
            } else {
                q = MultiFieldQueryParser.parse(query, fields, analyzer);
            }
            return searcher.search(q);
        } else {
            throw new IllegalStateException(
	                "Searches can only be performed when " +
	                "the LuceneSearcher is in read mode");
        }
    } //
    
    public int numHits(String query) throws ParseException, IOException {
        Integer count;
        if ( (count=(Integer)m_hitCountCache.get(query)) == null ) {
            Hits hits = search(query);
            count = new Integer(hits.length());
            m_hitCountCache.put(query, count);
        } //
        return count.intValue();
    } //
    
    /**
     * Add a document to the Lucene search index.
     * @param d
     */
	public void addDocument(Document d) {
	    if ( !m_readMode ) {
		    try {
				writer.addDocument(d);
				m_hitCountCache.clear();
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
     * @return Returns the fields.
     */
    public String[] getFields() {
        return (String[])fields.clone();
    } //
    
    /**
     * @param fields The fields to set.
     */
    public void setFields(String[] fields) {
        this.fields = (String[])fields.clone();
    } //
    
    /**
     * @return Returns the reader.
     */
    public IndexReader getIndexReader() {
        return reader;
    } //
    
    /**
     * @return Returns the searcher.
     */
    public Searcher getIndexSearcher() {
        return searcher;
    } //
    
    public boolean isReadOnly() {
        return m_readOnly;
    } //
    
} // end of class LuceneSearcher
