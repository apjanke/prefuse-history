package enronic.data;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class EnronicQueryFactory {
    
    public String getAllProfilesQuery(EnronicDBParams p) {
        return "select * from "+p.getProfileTable();
    } //
    
    public String getAllEdgesQuery(EnronicDBParams p) {
        return "select * from "+p.getGraphTable();
    } //
    
    /**
     * Query for retrieving a single profile.
     */
    public String getProfileQuery(EnronicDBParams p) {
        return "select * from "+p.getProfileTable()
        		+" where "+p.getProfileIDField()+" = ?";
    } //
    
    /**
     * Query for retrieving all friends of a person
     */
    public String getNeighborQuery(EnronicDBParams p) {
        String profiles = p.getProfileTable();
        String graph = p.getGraphTable();
        String id = profiles+"."+p.getProfileIDField();
        String src = graph+"."+p.getGraphSrcIDField();
        String dst = graph+"."+p.getGraphDstIDField();

        return "select "+profiles+".* from "+profiles+", "+graph
        	+" where ("+src+" = ? AND "+id+" = "+dst+") OR "
        	+"("+dst+" = ? AND "+id+" = "+src+")";
    } //
    
    /**
     * Query for retrieving all edges incident on all friends of a person
     */
    public String getEdgeQuery(EnronicDBParams p) {
        String graph = p.getGraphTable();
        String src = p.getGraphSrcIDField();
        String dst = p.getGraphDstIDField();
        
        return "select g1.* from "+graph+" as g1 left join "+graph+" as g2 on "
        	+"g1."+src+" = g2."+dst+" where g2."+src+" = ?";
    } //
    
    public String getMessageQuery(EnronicDBParams p) {
        return "select m.subject, m.messagedt, m.senderid, b.BODY " +
        		"from messages as m, bodies as b " +
        		"where m.messageid = ? and b.messageid = m.messageid";
    } //
    
    public String getRecipientsQuery(EnronicDBParams p) {
        return "select personid, reciptype " +
        		"from recipients " +
        		"where messageid = ? and not(reciptype = 'bcc')";
    } //
    
    public String getMessageCategoriesQuery(EnronicDBParams p) {
        return "select * from messagecats where messageid = ?";
    } //
    
    public String getMessageIDs1Query(EnronicDBParams p) {
        return "select messageid " +
        		"from edgemap " +
        		"where senderid = ? or recipientid = ? " +
        		"group by messageid";
    } //
    
    public String getMessageIDs2Query(EnronicDBParams p) {
        return "select messageid " +
        		"from edgemap " +
        		"where (senderid = ? and recipientid = ?) or " +
        		" (senderid = ? and recipientid = ?)";
    } //
    
} // end of class enronicQueryFactory
