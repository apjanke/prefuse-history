package edu.berkeley.guir.prefuse.activity;

import java.util.ArrayList;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.*;

/**
 * 
 * Feb 5, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ActionPipeline extends Activity implements Action {

    private ItemRegistry   m_registry;
    private ArrayList      m_actions = new ArrayList();
    
    public ActionPipeline(ItemRegistry registry) {
        this(registry, 0);
    }
    
    public ActionPipeline(ItemRegistry registry, long duration) {
        this(registry, duration, Activity.DEFAULT_STEP_TIME);
    } //
    
    public ActionPipeline(ItemRegistry registry, long duration, long stepTime) {
        this(registry, duration, stepTime, System.currentTimeMillis());
    } //
    
    public ActionPipeline(ItemRegistry registry, long duration, long stepTime, long startTime) {
        super(duration, stepTime, startTime);
        m_registry = registry;
    } //
    
    public synchronized int size() {
        return m_actions.size();
    } //
    
    public synchronized void add(Action a) {
        m_actions.add(a);
    } //
    
    public synchronized void add(int i, Action a) {
        m_actions.add(i, a);
    } //
    
    public synchronized Action get(int i) {
        return (Action)m_actions.get(i);
    }
    
    public synchronized boolean remove(Action a) {
        return m_actions.remove(a);
    } //
    
    public synchronized Action remove(int i) {
        return (Action)m_actions.remove(i);
    } //
    
    protected synchronized void run(long elapsedTime) {
        run(m_registry, getPace(elapsedTime));
    } //
    
    public void run(ItemRegistry registry, double frac) {
        synchronized ( m_registry ) {
            Iterator iter = m_actions.iterator();
            while ( iter.hasNext() ) {
                Action a = (Action)iter.next();
                if ( a.isEnabled() ) a.run(m_registry, frac);
            }
        }
    } //

} // end of class Action
