package prefusex.lucene.database;

import java.sql.ResultSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import prefusex.database.QueryProcessor;

/**
 * LuceneQueryProcessor
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class LuceneQueryProcessor implements QueryProcessor {
 
    public final static int TEXT       = 0;
    public final static int KEYWORD    = 1;
    public final static int UNINDEXED  = 2;
    public final static int UNSTORED   = 3;
    
    private String  dbTable;
    private String  columns[];
    private int     indexType[];
    private String  conditions;
    private String  groupBy;
    private int     curRecord = 0;
    private int     pageSize = 1000;
    private boolean verbose = false;
    
    private IndexWriter writer;
    
    public LuceneQueryProcessor(String dbTable, String col[], int types[]) {
        this(dbTable,col,types,null);
    } //
    
    public LuceneQueryProcessor(String dbTable, String col[], int types[], 
            IndexWriter writer)
    {
        this.dbTable = dbTable;
        this.columns = col;
        this.indexType = types;
        this.writer = writer;
    } //
    
    public void start() {
        curRecord = 0;
    } //
    
    public boolean hasNext() {
        return (curRecord%pageSize) == 0;
    } //
    
    public String getQuery() {
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        for ( int i=0; i<columns.length; i++ ) {
            sb.append(columns[i]);
            if ( i < columns.length-1 ) {
                sb.append(", ");
            }
        }
        sb.append(" from ").append(dbTable);
        if ( conditions != null ) {
            sb.append(" where ").append(conditions);
        }
        if ( groupBy != null ) {
            sb.append(" group by ").append(groupBy);
        }
        if ( pageSize > 0 ) {
            sb.append(" limit ").append(pageSize);
            sb.append(" offset ").append(curRecord);
        }
        //if (verbose) System.out.println(sb);
        return sb.toString();
    } //

    public void processResults(ResultSet rs) throws Exception {
        if (verbose) System.out.print("Processing query results... ");
        int count = 0;
        while ( rs.next() ) {
            count++;
            Document d = new Document();
            for ( int i=0; i<columns.length; i++ ) {
                String s = rs.getString(i+1);
                int type = indexType[i];
                if ( s == null ) continue;
                switch (type) {
                case TEXT:
                    d.add(Field.Text(columns[i], s));
                    break;
                case KEYWORD:
                    d.add(Field.Keyword(columns[i], s));
                    break;
                case UNSTORED:
                    d.add(Field.UnStored(columns[i], s));
                    break;
                case UNINDEXED:
                    d.add(Field.UnIndexed(columns[i], s));
                    break;
                default:
                    throw new IllegalStateException(
                       "Encountered unknown Lucene field type.");
                }
            }
            writer.addDocument(d);
        }
        curRecord += count;
        if (verbose) System.out.println("DONE ("+count+"/"+curRecord+" rows)");
    } //
    
    /**
     * @return Returns the current Lucene index writer.
     */
    public IndexWriter getIndexWriter() {
        return writer;
    }
    /**
     * @param writer The Lucene index writer to set.
     */
    public void setIndexWriter(IndexWriter writer) {
        this.writer = writer;
    }
    /**
     * @return Returns the verbose.
     */
    public boolean isVerbose() {
        return verbose;
    }
    /**
     * @param verbose The verbose to set.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    /**
     * @return Returns the conditions.
     */
    public String getConditions() {
        return conditions;
    }
    /**
     * @param conditions The conditions to set.
     */
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
    /**
     * @return Returns the pageSize.
     */
    public int getPageSize() {
        return pageSize;
    }
    /**
     * @param pageSize The pageSize to set.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    /**
     * @return Returns the groupBy.
     */
    public String getGroupBy() {
        return groupBy;
    }
    /**
     * @param groupBy The groupBy to set.
     */
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
} // end of class LuceneQueryProcessor
