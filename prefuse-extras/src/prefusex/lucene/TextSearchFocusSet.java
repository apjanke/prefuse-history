package prefusex.lucene;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;

import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusEventMulticaster;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Entity;

/**
 * <p>
 * A {@link FocusSet FocusSet} implementation that performs text searches
 * on graph data using the Lucene search engine. The 
 * {@link #index(Iterator, String) index} method
 * should be used to register searchable graph data. Then the
 * {@link #search(String) search} method can be used to perform a search. The
 * matching search results then become the members of this 
 * <code>FocusSet</code>.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> The {@link #add(Entity) add}, (@link #remove(Entity) remove},
 * and {@link #set(Entity) set} methods are not supported by this 
 * implementation, and will generate exceptions if called. Instead, the focus
 * membership is determined by the search matches found using the
 * {@link #search(String) search} method.
 * </p>
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class TextSearchFocusSet implements FocusSet {

    private FocusListener m_listener = null;
    private LinkedHashSet m_set = new LinkedHashSet();
    private HashMap m_entityMap = new HashMap();
    private String m_query = null;
    private LuceneSearcher m_lucene = null;
    
    private int m_id = 1;
    
    /**
     * Creates a new TextSearchFocusSet.
     */
    public TextSearchFocusSet() {
        m_lucene = new LuceneSearcher();
    } //
    
    /**
     * Adds a listener to monitor changes to this FocusSet.
     * @param fl the FocusListener to add
     */
    public void addFocusListener(FocusListener fl) {
        m_listener = FocusEventMulticaster.add(m_listener, fl);
    } //

    /**
     * Removes a listener currently monitoring this FocusSet.
     * @param fl the FocusListener to remove
     */
    public void removeFocusListener(FocusListener fl) {
        m_listener = FocusEventMulticaster.remove(m_listener, fl);
    } //
    
    /**
     * Returns the current search query, if any
     * @return the current;y active search query
     */
    public String getQuery() {
        return m_query;
    } //
    
    /**
     * Searches the indexed attributes of this FocusSet for matching
     * string prefixes, adding the Entity instances for each search match
     * to the FocusSet.
     * @param query the query string to search for. Indexed attributes
     *  with a matching prefix will be added to the FocusSet.
     */
    public void search(String query) {
        m_lucene.setReadMode(true);
        Entity[] rem = (Entity[])m_set.toArray(FocusEvent.EMPTY);
        m_set.clear();
        m_query = query;
        
        try {
            Hits hits = m_lucene.search(query);
            for ( int i=0; i < hits.length(); i++ ) {
                Document d = hits.doc(i);
                Integer id = new Integer(d.get(LuceneSearcher.ID));
                Entity entity = (Entity)m_entityMap.get(id);
                if ( entity != null ) {
                    m_set.add(entity);
                } else {
                    System.err.println("Missing entity -- "+id);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        
        Entity[] add = (Entity[])m_set.toArray(FocusEvent.EMPTY);
        FocusEvent fe = new FocusEvent(this, FocusEvent.FOCUS_SET, add, rem);
        m_listener.focusChanged(fe);
    } //
    
    /**
     * Indexes the attribute values for the given attribute name for
     * each Entity in the provided Iterator. These values are used
     * to construct an internal data structure allowing fast searches
     * over these attributes. To index multiple attributes, simply call
     * this method multiple times with the desired attribute names.
     * @param entities an Iterator over Entity instances to index
     * @param attrName the name of the attribute to index
     */
    public void index(Iterator entities, String attrName) {
        m_lucene.setReadMode(false);
        while ( entities.hasNext() ) {
            Entity e = (Entity)entities.next();
            index(e, attrName);
        }
    } //
    
    public void index(Entity e, String attrName) {
        m_lucene.setReadMode(false);
        String s;
        if ( (s=e.getAttribute(attrName)) == null ) return;
        
        int id = m_id++;
        
        Document d = new Document();
        d.add(Field.Text(LuceneSearcher.FIELD, s));
        d.add(Field.Keyword(LuceneSearcher.ID, String.valueOf(id)));
        m_lucene.addDocument(d);
        
        m_entityMap.put(new Integer(id), e);
    } //
    
    /**
     * Clears this focus set, invalidating any previous search.
     * @see edu.berkeley.guir.prefuse.focus.FocusSet#clear()
     */
    public void clear() {
        m_query = null;
        Entity[] rem = (Entity[])m_set.toArray(FocusEvent.EMPTY);
        m_set.clear();
        FocusEvent fe = new FocusEvent(this, FocusEvent.FOCUS_REMOVED, null, rem);
        m_listener.focusChanged(fe);
    } //

    /**
     * Returns an Iterator over the Entity instances matching
     * the most recent search query.
     * @return an Iterator over the Entity instances matching
     * the most recent search query.
     */
    public Iterator iterator() {
        return m_set.iterator();
    } //

    /**
     * Returns the number of matches for the most recent search query.
     * @return the number of matches for the most recent search query.
     */
    public int size() {
        return m_set.size();
    } //

    /**
     * Indicates if a given Entity is contained within this FocusSet (i.e.
     * the Entity is currently a matching search result).
     * @param entity the Entity to check for containment
     * @return true if this Entity is in the FocusSet, false otherwise
     */
    public boolean contains(Entity entity) {
        return m_set.contains(entity);
    } //
    
    // ========================================================================
    // == UNSUPPORTED OPERATIONS ==============================================
    
    /**
     * This method is not supported by this implementation. Don't call it!
     * Instead, use the {@link #search(String) search} or
     * {@link #clear() clear} methods.
     */
    public void add(Entity focus) {
        throw new UnsupportedOperationException();
    } //
    /**
     * This method is not supported by this implementation. Don't call it!
     * Instead, use the {@link #search(String) search} or
     * {@link #clear() clear} methods.
     */
    public void add(Collection foci) {
        throw new UnsupportedOperationException();
    } //
    /**
     * This method is not supported by this implementation. Don't call it!
     * Instead, use the {@link #search(String) search} or
     * {@link #clear() clear} methods.
     */
    public void remove(Entity focus) {
        throw new UnsupportedOperationException();
    } //
    /**
     * This method is not supported by this implementation. Don't call it!
     * Instead, use the {@link #search(String) search} or
     * {@link #clear() clear} methods.
     */
    public void remove(Collection foci) {
        throw new UnsupportedOperationException();
    } //
    /**
     * This method is not supported by this implementation. Don't call it!
     * Instead, use the {@link #search(String) search} or
     * {@link #clear() clear} methods.
     */
    public void set(Entity focus) {
        throw new UnsupportedOperationException();
    } //
    /**
     * This method is not supported by this implementation. Don't call it!
     * Instead, use the {@link #search(String) search} or
     * {@link #clear() clear} methods.
     */
    public void set(Collection foci) {
        throw new UnsupportedOperationException();
    } //
    
}  // end of class TextSearchFocusSet
