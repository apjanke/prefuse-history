package enronic.data;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class EnronicDBParams {

    private String profileTable;
    private String profileIDField;
    private String graphTable;
    private String graphSrcIDField;
    private String graphDstIDField;

    /**
     * @param profileTable
     * @param profileIDField
     * @param graphTable
     * @param graphSrcIDField
     * @param graphDstIDField
     */
    public EnronicDBParams(String profileTable, String profileIDField,
            String graphTable, String graphSrcIDField, String graphDstIDField) {
        super();
        this.profileTable = profileTable;
        this.profileIDField = profileIDField;
        this.graphTable = graphTable;
        this.graphSrcIDField = graphSrcIDField;
        this.graphDstIDField = graphDstIDField;
    } //
    
    /**
     * @return Returns the graphDstIDField.
     */
    public String getGraphDstIDField() {
        return graphDstIDField;
    } //
    
    /**
     * @param graphDstIDField The graphDstIDField to set.
     */
    public void setGraphDstIDField(String graphDstIDField) {
        this.graphDstIDField = graphDstIDField;
    } //
    
    /**
     * @return Returns the graphSrcIDField.
     */
    public String getGraphSrcIDField() {
        return graphSrcIDField;
    } //
    
    /**
     * @param graphSrcIDField The graphSrcIDField to set.
     */
    public void setGraphSrcIDField(String graphSrcIDField) {
        this.graphSrcIDField = graphSrcIDField;
    } //
    
    /**
     * @return Returns the graphTable.
     */
    public String getGraphTable() {
        return graphTable;
    } //
    
    /**
     * @param graphTable The graphTable to set.
     */
    public void setGraphTable(String graphTable) {
        this.graphTable = graphTable;
    } //
    
    /**
     * @return Returns the profileIDField.
     */
    public String getProfileIDField() {
        return profileIDField;
    } //
    
    /**
     * @param profileIDField The profileIDField to set.
     */
    public void setProfileIDField(String profileIDField) {
        this.profileIDField = profileIDField;
    } //
    
    /**
     * @return Returns the profileTable.
     */
    public String getProfileTable() {
        return profileTable;
    } //
    
    /**
     * @param profileTable The profileTable to set.
     */
    public void setProfileTable(String profileTable) {
        this.profileTable = profileTable;
    } //
    
} // end of class enronicDBParams
