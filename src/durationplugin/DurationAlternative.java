/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package durationplugin;
import com.rma.io.DssFileManagerImpl;
import com.rma.io.RmaFile;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDSSDataAttributes;
import hec.io.DSSIdentifier;
import hec.io.TimeSeriesContainer;
import hec2.model.DataLocation;
import hec2.model.DataLocationComputeType;
import hec2.model.DssDataLocation;
import hec2.plugin.model.ComputeOptions;
import hec2.plugin.selfcontained.SelfContainedPluginAlt;
import hec2.wat.model.tracking.OutputVariableImpl;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
/**
 *
 * @author WatPowerUser
 */
public class DurationAlternative extends SelfContainedPluginAlt{
    private List<DataLocation> _dataLocations = new ArrayList<>();
    private String _pluginVersion;
    private static final String DocumentRoot = "DurationAlternative";
    private static final String AlternativeNameAttribute = "Name";
    private static final String AlternativeDescriptionAttribute = "Desc";
    private ComputeOptions _computeOptions;
    public DurationAlternative(){
        super();
        _dataLocations = new ArrayList<>();
    }
    public DurationAlternative(String name){
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
    public static DataLocation createDataLocation(Element dlElem)
    {
            if ( dlElem == null )
            {
                    return null;
            }
            String clsName = dlElem.getAttributeValue("Class");
            if ( clsName == null )
            {
                    clsName = DataLocation.class.getName();
            }
            return createDataLocation(clsName);

    }
    /**
     * @param cls
     * @return
     */
    private static DataLocation createDataLocation(String clsName) {
            Class cls;
            try
            {
                    cls = Class.forName(clsName);
            }
            catch (ClassNotFoundException e)
            {
                    System.out.println("createDataLocation:failed to find class "+clsName
                                    +" Error:"+e);
                    return new DataLocation();
            }

            Object obj;
            try
            {
                    obj = cls.newInstance();
                    if ( obj instanceof DataLocation )
                    {
                            return (DataLocation)obj;
                    }
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                    System.out.println("createDataLocation: error creating DataLocation "+clsName
                                    +" Error:"+e);
            }
            //default return something.
            return new DataLocation();
    }
    @Override
    protected void loadDataLocations(Element root, List<DataLocation> inputDataLocations){
        Element dlElem = root.getChild("DataLocations");
        if ( dlElem != null )
        {
                List<?> kids = dlElem.getChildren();
                Element child;
                String name, model, param, path, cls, computeType, desc;
                DataLocation dndl;
                DataLocation dl;
                DataLocationComputeType ctype;
                int prevModelIndex;
                for (int i = 0;i < kids.size();i++ )
                {
                        child = (Element) kids.get(i);
                        if ( !"DataLocation".equals(child.getName()))
                        {
                                continue;
                        }
                        dl = createDataLocation(child);
                        if ( dl == null )
                        {
                                continue;
                        }
                        if ( ((DurationLocation)dl).fromXML(child))
                        {
                                dl.setModelAlternative(this.getModelAlt());
                                Integer duration = Integer.parseInt(child.getAttribute("Duration").getValue());
                                ((DurationLocation)dl).setDuration(duration);
                                _dataLocations.add(dl);
                        }
                }
        }
    }
    @Override
    protected void saveDataLocations(Element root, List<DataLocation> inputDataLocations){
	
        Element dlElem = new Element("DataLocations");
        root.addContent(dlElem);
        DataLocation dl,dndl;
        DssDataLocation dssDndl;
        String dssFile, relDssFile;

        for (int i = 0; i< inputDataLocations.size();i++ )
        {
                dl = inputDataLocations.get(i);
                dl.toXML(dlElem);
                ((Element)dlElem.getChildren().get(i)).setAttribute("Duration", Integer.toString(((DurationLocation)dl).getDuration()));
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
            //return defaultDataLocations();
            return trinityInputDataLocations();
        }else{
            return _dataLocations;
        }
	
    }
    public List<DataLocation> getDataLocations(){
        return _dataLocations;
    }
    private List<DataLocation> defaultDataLocations(){
       	if(!_dataLocations.isEmpty()){
            return _dataLocations;
        }
        List<DataLocation> dlList = new ArrayList<>();
        //create a default location so that links can be initialized.
        DataLocation USC00410313 = new DurationLocation(this.getModelAlt(),"USC00410313","Precipitation",1);//2);
        dlList.add(USC00410313);
        DataLocation USC00413668 = new DurationLocation(this.getModelAlt(),"USC00413668","Precipitation",1);
        dlList.add(USC00413668);
        DataLocation USC00410271 = new DurationLocation(this.getModelAlt(),"USC00410271","Precipitation",1);
        dlList.add(USC00410271);
        DataLocation USC00414517 = new DurationLocation(this.getModelAlt(),"USC00414517","Precipitation",1);//2);
        dlList.add(USC00414517);
        DataLocation USC00410984 = new DurationLocation(this.getModelAlt(),"USC00410984","Precipitation",1);//3);
        dlList.add(USC00410984);
        DataLocation USC00414972 = new DurationLocation(this.getModelAlt(),"USC00414972","Precipitation",1);//2);
        dlList.add(USC00414972);
        DataLocation USC00419532 = new DurationLocation(this.getModelAlt(),"USC00419532","Precipitation",1);
        dlList.add(USC00419532);
        DataLocation USC00411063 = new DurationLocation(this.getModelAlt(),"USC00411063","Precipitation",1);
        dlList.add(USC00411063);
        DataLocation USC00412334 = new DurationLocation(this.getModelAlt(),"USC00412334","Precipitation",1);//2);
        dlList.add(USC00412334);
        DataLocation USC00412334T = new DurationLocation(this.getModelAlt(),"temperature","Temp",1);//2);
        dlList.add(USC00412334T);
        DataLocation USC00413247 = new DurationLocation(this.getModelAlt(),"USC00413247","Precipitation",1);//3);
        dlList.add(USC00413247);
        DataLocation USC00411800 = new DurationLocation(this.getModelAlt(),"USC00411800","Precipitation",1);//2);
        dlList.add(USC00411800);
        DataLocation USC00416130 = new DurationLocation(this.getModelAlt(),"USC00416130","Precipitation",1);//3);
        dlList.add(USC00416130);
        DataLocation USC00417659 = new DurationLocation(this.getModelAlt(),"USC00417659","Precipitation",1);//2);
        dlList.add(USC00417659);
        DataLocation USC00345563 = new DurationLocation(this.getModelAlt(),"USC00345563","Precipitation",1);
        dlList.add(USC00345563);
        DataLocation USC00412404 = new DurationLocation(this.getModelAlt(),"USC00412404","Precipitation",1);
        dlList.add(USC00412404);
        DataLocation USC00410337 = new DurationLocation(this.getModelAlt(),"USC00410337","Precipitation",1);//2);
        dlList.add(USC00410337);
        DataLocation USC00413691 = new DurationLocation(this.getModelAlt(),"USC00413691","Precipitation",1);//3);
        dlList.add(USC00413691);
        DataLocation USC00417028 = new DurationLocation(this.getModelAlt(),"USC00417028","Precipitation",1);//2);
        dlList.add(USC00417028);
        DataLocation USC00411490 = new DurationLocation(this.getModelAlt(),"USC00411490","Precipitation",1);
        dlList.add(USC00411490);
        DataLocation USW00013960 = new DurationLocation(this.getModelAlt(),"USW00013960","Precipitation",1);
        dlList.add(USW00013960);
        DataLocation USC00419522 = new DurationLocation(this.getModelAlt(),"USC00419522","Precipitation",1);//2);
        dlList.add(USC00419522);
        DataLocation USC00418274 = new DurationLocation(this.getModelAlt(),"USC00418274","Precipitation",1);//3);
        dlList.add(USC00418274);
        DataLocation USC00415766 = new DurationLocation(this.getModelAlt(),"USC00415766","Precipitation",1);//2);
        dlList.add(USC00415766);
        DataLocation USC00410262 = new DurationLocation(this.getModelAlt(),"USC00410262","Precipitation",1);//3);
        dlList.add(USC00410262);
        _dataLocations = dlList;
	return dlList; 
    }
        private List<DataLocation> trinityInputDataLocations(){
       	if(!_dataLocations.isEmpty()){
            return _dataLocations;
        }
        List<DataLocation> dlList = new ArrayList<>();
        //create datalocations for each location of intrest for trinity, so that it can be linked to output from other models.
        
        //pool inflows
        DataLocation benbrookLoc = new DurationLocation(this.getModelAlt(),"Benbrook-Pool","Inflow",2);
        dlList.add(benbrookLoc);
        DataLocation grapevineLoc = new DurationLocation(this.getModelAlt(),"Grapevine-Pool","Inflow",3);
        dlList.add(grapevineLoc);
        DataLocation joePoolLoc = new DurationLocation(this.getModelAlt(),"Joe Pool-Pool","Inflow",2);
        dlList.add(joePoolLoc);
        DataLocation lewisvilleLoc = new DurationLocation(this.getModelAlt(),"Lewisville-Pool","Inflow",3);
        dlList.add(lewisvilleLoc);
        DataLocation navarroMillsLoc = new DurationLocation(this.getModelAlt(),"Mountain Creek-Pool","Inflow",1);
        dlList.add(navarroMillsLoc);
        DataLocation rayRobertsLoc = new DurationLocation(this.getModelAlt(),"Ray Roberts-Pool","Inflow",2);
        dlList.add(rayRobertsLoc);
        DataLocation richlandChambersLoc = new DurationLocation(this.getModelAlt(),"Eagle Mountain-Pool","Inflow",4);
        dlList.add(richlandChambersLoc);
        DataLocation dallasFloodwayLoc = new DurationLocation(this.getModelAlt(),"Bridgeport-Pool","Inflow",4);
        dlList.add(dallasFloodwayLoc);
        DataLocation lakeWorthLoc = new DurationLocation(this.getModelAlt(),"Lake Worth-Pool","Inflow",3);
        dlList.add(lakeWorthLoc);

        //HMS inflows
        DataLocation weatherfordLoc = new DurationLocation(this.getModelAlt(),"Lake Weatherford Inflows","Flow",2);
        dlList.add(weatherfordLoc);
        DataLocation arlingtonLoc = new DurationLocation(this.getModelAlt(),"Arlington_Inflow","Flow",2);
        dlList.add(arlingtonLoc);
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
        return true;
    }
    private TimeSeriesContainer ReadTimeSeries(String DssFilePath, String dssPath, boolean isFRM){
        DSSPathname pathName = new DSSPathname(dssPath);
        String InputFPart = pathName.getFPart();
        if(isFRM){
            int AltFLastIdx = _computeOptions.getFpart().lastIndexOf(":");
            if(InputFPart.contains(":")){
                int oldFLastIdx = InputFPart.lastIndexOf(":");
                pathName.setFPart(_computeOptions.getFpart().substring(0,AltFLastIdx)+ InputFPart.substring(oldFLastIdx,InputFPart.length()));
            }  
        }
        DSSIdentifier eventDss = new DSSIdentifier(DssFilePath,pathName.getPathname());
        eventDss.setStartTime(_computeOptions.getRunTimeWindow().getStartTime());
	eventDss.setEndTime(_computeOptions.getRunTimeWindow().getEndTime());
        int type = DssFileManagerImpl.getDssFileManager().getRecordType(eventDss);
        if((HecDSSDataAttributes.REGULAR_TIME_SERIES<=type && type < HecDSSDataAttributes.PAIRED)){
            boolean exist = DssFileManagerImpl.getDssFileManager().exists(eventDss);
            TimeSeriesContainer eventTsc = null;
            if (!exist )
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            eventTsc = DssFileManagerImpl.getDssFileManager().readTS(eventDss, true);
            if ( eventTsc != null )
            {
                exist = eventTsc.numberValues > 0;
            }
            if(exist){
                return eventTsc;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    private double ComputeMax(TimeSeriesContainer input, Integer durationInDays, String ePart){
        if(input==null){return 0;}
        double[] vals = input.values;
        Integer stepsPerDay = timeStepsPerDay(ePart,durationInDays);
        if(stepsPerDay == Integer.MAX_VALUE){return 0;}
        Integer stepsPerDuration = stepsPerDay*durationInDays;
        double maxVal = Double.MIN_VALUE;
        double avg = 0;
        double durationVolume = 0;
        for(int i = 0; i<vals.length;i++){
            durationVolume += vals[i];
            if(i==stepsPerDuration){
                avg =durationVolume/stepsPerDuration;
                //avg = durationVolume/duration;
                maxVal = avg;
            }else if(i>stepsPerDuration){
                double oldval = vals[i-stepsPerDuration];
                durationVolume-=oldval;
                avg =durationVolume/stepsPerDuration;
                //avg = durationVolume/duration;
                if(avg>maxVal)maxVal = avg;
            }
        }
        return maxVal;
    }
    private double ComputeTotal(TimeSeriesContainer input){
        if(input==null){return 0;}
        double[] vals = input.values;
        double volume = 0;
        for(int i = 0; i<vals.length;i++){
            volume += vals[i];
        }
        return volume;
    }
    private int timeStepsPerDay(String ePart, Integer duration){
        switch(ePart.toUpperCase()){
            case "1HOUR":
                return 24;
            case "3HOUR":
                return 8;
            case "DAILY":
                return 1;
            case "1MIN":
                return 1440;
            case "5MIN":
                return 1440/5;
            case "15MIN":
                return 1440/15;
            case "30MIN":
                return 1440/30;
            default:
                return Integer.MAX_VALUE;
        }
    }
    protected double getOutputValue(OutputVariableImpl oimpl){
        //String dlName = .split(" - ")[0];//check for volume duration or max volume
        String[] s = oimpl._name.split(" - ");
        String dlName = s[0];
        if(s[1].equals("Temp")){
            dlName = s[0];
        }
        hec2.wat.model.ComputeOptions wco = (hec2.wat.model.ComputeOptions)_computeOptions;
        String dssFilePath = wco.getDssFilename();
        for(DataLocation dl : _dataLocations){
            if(dl.getName().equals(dlName)){
                if(dl.getParameter().equals(s[1])){
                    String dssPath = dl.getLinkedToLocation().getDssPath();
                    DSSPathname pathName = new DSSPathname(dssPath);
                    String inputEPart = pathName.getEPart();
                    TimeSeriesContainer tsc = ReadTimeSeries(dssFilePath,dssPath,true);
                    if(tsc==null)return Double.NaN;
                    if(oimpl.getName().endsWith("max")){
                        if(oimpl.getName().endsWith("Flow max")){
                            double d  = tsc.maxmimumValue();
                            return d;//Collections.max(Arrays.asList(ArrayUtils.toObject(tsc.values)));
                        }else{
                            if(oimpl.getName().contains("30 Day")){
                                return ComputeMax(tsc,30,inputEPart);
                            }else{
                                return ComputeMax(tsc,((DurationLocation)dl).getDuration(),inputEPart);
                            }
                        }
                        
                    }else{
                        return ComputeTotal(tsc);
                    }                    
                }

            }
        }
        return Double.NaN;
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
