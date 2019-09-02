/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package savelifecyclerecordsplugin;
import com.rma.io.DssFileManagerImpl;
import com.rma.io.RmaFile;
import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.DSSPathname;
import hec2.model.DataLocation;
import hec2.plugin.model.ComputeOptions;
import hec2.plugin.selfcontained.SelfContainedPluginAlt;
import hec2.wat.client.WatFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;
/**
 *
 * @author WatPowerUser
 */
public class SaveLifecycleRecordsAlternative extends SelfContainedPluginAlt{
    private List<DataLocation> _dataLocations = new ArrayList<>();
    private String _pluginVersion;
    private static final String DocumentRoot = "SLRPAlternative";
    private static final String AlternativeNameAttribute = "Name";
    private static final String AlternativeDescriptionAttribute = "Desc";
    private ComputeOptions _computeOptions;
    public SaveLifecycleRecordsAlternative(){
        super();
        _dataLocations = new ArrayList<>();
    }
    public SaveLifecycleRecordsAlternative(String name){
        this();
        setName(name);
    }
    @Override
    public boolean saveData(RmaFile file){
        if(file!=null){
            Element root = new Element(DocumentRoot);
            root.setAttribute(AlternativeNameAttribute,getName());
            root.setAttribute(AlternativeDescriptionAttribute,getDescription());
            if(_dataLocations!=null){
                saveDataLocations(root,_dataLocations);
            }
            Document doc = new Document(root);
            return writeXMLFile(doc,file);
        }
        return false;
    }
    @Override
    protected boolean loadDocument(org.jdom.Document dcmnt) {
        if(dcmnt!=null){
            org.jdom.Element ele = dcmnt.getRootElement();
            if(ele==null){
                System.out.println("No root element on the provided XML document.");
                return false;   
            }
            if(ele.getName().equals(DocumentRoot)){
                setName(ele.getAttributeValue(AlternativeNameAttribute));
                setDescription(ele.getAttributeValue(AlternativeDescriptionAttribute));
            }else{
                System.out.println("XML document root was imporoperly named.");
                return false;
            }
            if(_dataLocations==null){
                _dataLocations = new ArrayList<>();
            }
            _dataLocations.clear();
            loadDataLocations(ele, _dataLocations);
            setModified(false);
            return true;
        }else{
            System.out.println("XML document was null.");
            return false;
        }
    }
    public List<DataLocation> getOutputDataLocations(){
       //construct output data locations 
        List<DataLocation> ret = new ArrayList<>();
	return ret;//defaultDataLocations();
    }
    public List<DataLocation> getInputDataLocations(){
        //construct input data locations.
        if(_dataLocations.isEmpty()){
            return defaultInputDataLocations();
        }else{
            return _dataLocations;
        }
    }
    public List<DataLocation> getDataLocations(){
        return _dataLocations;
    }
    private List<DataLocation> defaultInputDataLocations(){
       	if(!_dataLocations.isEmpty()){
            return _dataLocations;
        }
        List<DataLocation> dlList = new ArrayList<>();
        //create datalocations for each location of intrest, so that it can be linked to output from other models.
        
        //pool elevations
        DataLocation FolsomPool_elev = new DataLocation(this.getModelAlt(),"Folsom-Pool","ELEV");
        dlList.add(FolsomPool_elev);
        
        //pool inflows
        DataLocation FolsomPool_flowin = new DataLocation(this.getModelAlt(),"Folsom-Pool","FLOW-IN");
        dlList.add(FolsomPool_flowin);
        
        //pool outflows
        DataLocation FolsomPool_flowout = new DataLocation(this.getModelAlt(),"Folsom-Pool","FLOW-OUT");
        dlList.add(FolsomPool_flowout);
        
        DataLocation FolsomPool_inflow_1D = new DataLocation(this.getModelAlt(),"inflow_1D","State Variable");
        dlList.add(FolsomPool_inflow_1D);
        DataLocation FolsomPool_inflow_2D = new DataLocation(this.getModelAlt(),"inflow_2D","State Variable");
        dlList.add(FolsomPool_inflow_2D);
        DataLocation FolsomPool_inflow_3D = new DataLocation(this.getModelAlt(),"inflow_3D","State Variable");
        dlList.add(FolsomPool_inflow_3D);
        DataLocation FolsomPool_inflow_5D = new DataLocation(this.getModelAlt(),"inflow_5D","State Variable");
        dlList.add(FolsomPool_inflow_5D);
        DataLocation FolsomPool_fcast_TOC = new DataLocation(this.getModelAlt(),"fcast_TOC","State Variable");
        dlList.add(FolsomPool_fcast_TOC);

        
        _dataLocations = dlList;
	return dlList; 
    }
    public boolean setDataLocations(List<DataLocation> dataLocations){
        boolean retval = true;
        for(DataLocation dl : dataLocations){
            if(!_dataLocations.contains(dl)){
                DataLocation linkedTo = dl.getLinkedToLocation();
                if(linkedTo!=null){
                    if(validLinkedToDssPath(dl))
                    {
                        setModified(true);
                        //setDssParts(dl);
                        _dataLocations.add(dl);
                        retval = true;
                    }                    
                }
            }else{
                DataLocation linkedTo = dl.getLinkedToLocation();
                if(linkedTo!=null){
                    if(validLinkedToDssPath(dl))
                    {
                        setModified(true);
                        retval = true;
                    }                    
                }
            }
        }
        if(retval)saveData();
	return retval;
    }
    private boolean validLinkedToDssPath(DataLocation dl){
        DataLocation linkedTo = dl.getLinkedToLocation();
        if(linkedTo==null)return false;
        String dssPath = linkedTo.getDssPath();
        return !(dssPath == null || dssPath.isEmpty());
    }
    public void setComputeOptions(ComputeOptions opts){
        _computeOptions = opts;
    }
    @Override
    public boolean isComputable() {
        return true;
    }
    @Override
    public boolean compute() {
        hec2.wat.model.ComputeOptions wco = (hec2.wat.model.ComputeOptions)_computeOptions;
        //get the lifecycle dss file.
        String lifecycleDssPath = wco.getDssFilename();
        WatFrame fr = hec2.wat.WAT.getWatFrame();
        //gather all dss records in the lifecycleDss file for the given data location
        //in order for distributed computing to work, this needs to not use special files or directories.
        //must delete unnecessary records and simply leave user selected records
        Vector<CondensedReference> paths = DssFileManagerImpl.getDssFileManager().getCondensedCatalog(lifecycleDssPath);
        Vector<String> pathsToDelete = new Vector<>();
        for(CondensedReference ref : paths){
            String[] subPaths = ref.getPathnameList();
            for(String sP : subPaths){
                boolean match = false;
                DSSPathname subPath = new DSSPathname(sP);
                String incomingCollectionID = subPath.getCollectionSequence();
                for(DataLocation d : _dataLocations){
                    //check against linked to location dss record path.
                    String dssPath = d.getLinkedToLocation().getDssPath();
                    DSSPathname pathName = new DSSPathname(dssPath);
                    String comparableCollectionID = pathName.getCollectionSequence();
                    
                    if(!incomingCollectionID.equals(comparableCollectionID)){
                        match = true;//don't delete future lifecycle data from HydrologicEventGenerators.
                        continue;
                    }
                    String InputFPart = pathName.getFPart();
                    //in an FRA compute the F part needs to be mangled to get the correct path from the datalocation.
                    if(wco.isFrmCompute()){
                        int AltFLastIdx = _computeOptions.getFpart().lastIndexOf(":");
                        if(InputFPart.contains(":")){
                            int oldFLastIdx = InputFPart.lastIndexOf(":");
                            pathName.setFPart(_computeOptions.getFpart().substring(0,AltFLastIdx)+ InputFPart.substring(oldFLastIdx,InputFPart.length()));
                        }
                    }
                    fr.addMessage("comparing " + subPath.getPathname() + " and " + pathName.getPathname());
                    
                    if(pathName.getPathname().equals(subPath.getPathname())){
                        match = true; 
                        fr.addMessage("Matching " + subPath.getPathname());
                    }
                }
                if(!match){
                    pathsToDelete.add(sP);
                }
            }
        }
        //delete all of the non matched DssPathNames.
        if(0>DssFileManagerImpl.getDssFileManager().delete(lifecycleDssPath, pathsToDelete)){
            fr.addMessage(DocumentRoot + " Alternative: " + getName() + " failed to delete all records attempted.");
        }
       
        return true;
    }
    @Override
    public boolean cancelCompute() {
        return false;
    }
    @Override
    public String getLogFile() {
        return null;
    }
    @Override
    public int getModelCount() {
        return 1;
    }

}
