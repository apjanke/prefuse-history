package enronic.data;

import java.util.Date;

import edu.berkeley.guir.prefuse.graph.Node;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> enronic(AT)jheer.org</a>
 */
public class Message implements Comparable {

    private int messageid;
    private String subject;
    private String body;
    private Date timestamp;
    private Node sender;
    private Node[] tos = new Node[0];
    private Node[] ccs = new Node[0];
    private int[] cats;
    private int primaryCat;
    
    public Message(int id) {
        messageid = id;
    } //
    
    /**
     * @return Returns the cats.
     */
    public int[] getCategories() {
        return cats;
    }
    /**
     * @param cats The cats to set.
     */
    public void setCategories(int[] cats) {
        this.cats = cats;
        int max = 0, idx = 0;
        for ( int i=1; i<cats.length; i++ ) {
            if ( cats[i] > max ) {
                max = cats[i];
                idx = i;
            }
        }
        primaryCat = idx;
    }
    public int getPrimaryCategory() {
        return primaryCat;
    } //
    /**
     * @return Returns the body.
     */
    public String getBody() {
        return body;
    }
    /**
     * @param body The body to set.
     */
    public void setBody(String body) {
        this.body = body;
    }
    public void addCc(Node n) {
        Node[] nn = new Node[ccs.length+1];
        System.arraycopy(ccs,0,nn,0,ccs.length);
        nn[ccs.length] = n;
        ccs = nn;
    } //
    /**
     * @return Returns the ccs.
     */
    public Node[] getCcs() {
        return ccs;
    }
    /**
     * @param ccs The ccs to set.
     */
    public void setCcs(Node[] ccs) {
        this.ccs = ccs;
    }
    /**
     * @return Returns the sender.
     */
    public Node getSender() {
        return sender;
    }
    /**
     * @param sender The sender to set.
     */
    public void setSender(Node sender) {
        this.sender = sender;
    }
    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        return subject;
    }
    /**
     * @param subject The subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    /**
     * @return Returns the timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }
    /**
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public void addTo(Node n) {
        Node[] nn = new Node[tos.length+1];
        System.arraycopy(tos,0,nn,0,tos.length);
        nn[tos.length] = n;
        tos = nn;
    } //
    /**
     * @return Returns the tos.
     */
    public Node[] getTos() {
        return tos;
    }
    /**
     * @param tos The tos to set.
     */
    public void setTos(Node[] tos) {
        this.tos = tos;
    }
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("ID: ").append(messageid).append('\n');
        sbuf.append("Subject: ").append(subject).append('\n');
        sbuf.append("From: ").append(getMailString(sender)).append('\n');
        sbuf.append("Date: ").append(timestamp).append('\n');
        
        if ( tos.length > 0 ) {
	        sbuf.append("To: ");
	        for ( int i=0; i<tos.length; i++ ) {
	            if ( i > 0 )
	                sbuf.append(", ");
	            sbuf.append(getMailString(tos[i]));
	        }
        }
        sbuf.append('\n');
        
        if ( ccs.length > 0 ) {
	        sbuf.append("Cc: ");
	        for ( int i=0; i<ccs.length; i++ ) {
	            if ( i > 0 )
	                sbuf.append(", ");
	            sbuf.append(getMailString(ccs[i]));
	        }
        }
        sbuf.append('\n');
        
        sbuf.append('\n');
        sbuf.append(body);
        return sbuf.toString();
    } //
    
    private String getMailString(Node n) {
        String name  = n.getAttribute("name");
        String email = n.getAttribute("email");
        if ( name != null && !name.equals("") ) {
            return name+" <"+email+">";
        } else {
            return "<"+email+">";
        }
    } //
    
    public int compareTo(Object o) {
        try {
            Message m = (Message)o;
            return this.timestamp.compareTo(m.getTimestamp());
        } catch ( Exception e ) {
            return -1;
        }
    } //
    
}
