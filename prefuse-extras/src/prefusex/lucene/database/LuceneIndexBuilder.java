package prefusex.lucene.database;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import prefusex.database.DatabaseConnection;
import prefusex.database.DatabaseParams;

/**
 * This class will build a Lucene search index from a database.
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class LuceneIndexBuilder {

    private DatabaseConnection dbconn;
    private Directory directory;
    private Analyzer  analyzer;
    
    /**
     * Creates a new LuceneIndexBuilder
     * @param dir the Lucene directory to add documents to
     * @param dbparams the parameters for connecting to a SQL database
     */
    public LuceneIndexBuilder(Directory dir, DatabaseParams dbparams) {
        this.directory = dir;
        this.dbconn = new DatabaseConnection(dbparams);
        this.analyzer = new StandardAnalyzer();
    } //
    
    /**
     * Creates a new LuceneIndexBuilder
     * @param dir the path on the file system at which to create the index
     * @param dbparams the parameters for connecting to a SQL database
     * @param append true if documents should be appended to an existing directory,
     *  false if the directory should be created anew.
     * @throws IOException
     */
    public LuceneIndexBuilder(String dir, DatabaseParams dbparams, boolean append) 
    	throws IOException
    {
        this(FSDirectory.getDirectory(dir,!append), dbparams);
    } //
    
    /**
     * Add documents to the index, according to the LuceneQueryProcessor
     * @param qp
     * @throws Exception
     */
    public void index(LuceneQueryProcessor qp) throws Exception
    {
        IndexWriter writer = new IndexWriter(directory, analyzer, true);
        qp.setIndexWriter(writer);
        dbconn.process(qp);
        writer.optimize();
        writer.close();
    } //
    
    public static void indexDatabase(Directory dir, DatabaseParams dbp, 
       LuceneQueryProcessor qp) throws Exception
    {
        LuceneIndexBuilder builder = new LuceneIndexBuilder(dir,dbp);
        builder.index(qp);
    } //
    
    public static void main(String argv[]) {
        // TODO: write this!
        
        // parse directory location
        String dir = "c:\\tmp\\lucene";
        
        // parse database params
        DatabaseParams dbp = new DatabaseParams();
        dbp.setHost("localhost");
        dbp.setDriver("com.mysql.jdbc.Driver");
        dbp.setProtocol("jdbc:mysql:");
        dbp.setDatabase("friendster");
        dbp.setUser("jheer");
        dbp.setPassword("ripley-04");
        
        // make shorthands for document field types
        int TEXT       = LuceneQueryProcessor.TEXT;
        int KEYWORD    = LuceneQueryProcessor.KEYWORD;
        int UNINDEXED  = LuceneQueryProcessor.UNINDEXED;
        int UNSTORED   = LuceneQueryProcessor.UNSTORED;
        
        // create the query processor
        LuceneQueryProcessor qp = new LuceneQueryProcessor(
                "profiles",
                new String[] {"uid", "name", "age", "location", "hometown",
                        "occupation", "interests", "music", "books", "tvshows",
                        "movies", "about", "want_to_meet"},
                new int[] {KEYWORD, TEXT, KEYWORD, TEXT, TEXT, TEXT, TEXT, TEXT,
                        TEXT, TEXT, TEXT, TEXT, TEXT}
        );
        qp.setVerbose(true);
        qp.setGroupBy("uid");
        
        try {
            // parse query processing commands
            LuceneIndexBuilder builder = new LuceneIndexBuilder(dir,dbp,false);
            // run indexer
            builder.index(qp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //
    
} // end of class LuceneIndexBuilder
