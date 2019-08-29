/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package durationplugin;
import com.rma.io.DssFileManagerImpl;
import com.rma.io.RmaFile;
import hec.data.Parameter;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDSSDataAttributes;
import hec.io.DSSIdentifier;
import hec.io.TimeSeriesContainer;
import hec.model.OutputVariable;
import hec2.model.DataLocation;
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
    private List<OutputVariable> _outputVariables;
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
            //determine how to store durations here
            
            
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
    private double ComputeMinimumForMaximumWindow(TimeSeriesContainer input, Integer duration, boolean durationInDays, String ePart){
        if(input==null){return 0;}
        double[] vals = input.values;
        
        Integer stepsPerDuration = Integer.MAX_VALUE;
        if(durationInDays){
            Integer stepsPerDay = timeStepsPerDay(ePart);
            stepsPerDuration = stepsPerDay*duration;
        }else{
            stepsPerDuration = timeStepsPerDuration(ePart,duration);
        }
        if(stepsPerDuration == Integer.MAX_VALUE){return 0;}

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
    private double ComputeDurationMax(TimeSeriesContainer input, Integer duration, boolean durationInDays, String ePart){
        if(input==null){return 0;}
        double[] vals = input.values;
        Integer stepsPerDuration = Integer.MAX_VALUE;
        if(durationInDays){
            Integer stepsPerDay = timeStepsPerDay(ePart);
            stepsPerDuration = stepsPerDay*duration;
        }else{
            stepsPerDuration = timeStepsPerDuration(ePart,duration);
        }
        if(stepsPerDuration == Integer.MAX_VALUE){return 0;}
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
    private int timeStepsPerDay(String ePart){
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
    private int timeStepsPerDuration(String ePart, Integer durationInHours){
        switch(ePart.toUpperCase()){
            case "1HOUR":
                return 1*durationInHours;
            case "3HOUR":
                if(durationInHours%3==0){
                    return durationInHours/3;
                }else{
                    return Integer.MAX_VALUE;
                }
            case "6HOUR":
                if(durationInHours%6==0){
                    return durationInHours/6;
                }else{
                    return Integer.MAX_VALUE;
                }
            case "DAILY":
                if(durationInHours%24==0){
                    return durationInHours/24;
                }else{
                    return Integer.MAX_VALUE;
                }
            case "1MIN":
                return 60*durationInHours;
            case "5MIN":
                return 12*durationInHours;
            case "15MIN":
                return 4*durationInHours;
            case "30MIN":
                return 2*durationInHours;
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
                                return ComputeDurationMax(tsc,30, true, inputEPart);
                            }else{
                                return ComputeDurationMax(tsc,1, true, inputEPart);//1 day duration
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
    
    public List<OutputVariable> getOutputVariables(){
        List<OutputVariable> ret = new ArrayList<>();
                for(DataLocation loc : getDataLocations())
        {
            DataLocation tl = loc;
            OutputVariableImpl output = new OutputVariableImpl();
            output.setName(loc.getName() + " - " + loc.getParameter() +  " - " + getName() + " " + 1 + " Day volume duration max" );
            output.setDescription("Duration Plugin Volume Duration Max for " + getName());
            if(loc.getParameter().equals("Flow")){
                output.setParamId(Parameter.PARAMID_FLOW);
            }else if(loc.getParameter().equals("Inflow")){
                output.setParamId(Parameter.PARAMID_FLOW);
            }else if(loc.getParameter().equals("Flow-Unreg")){
                output.setParamId(Parameter.PARAMID_FLOW);
                output.setName(loc.getName() + " - " + loc.getParameter() +  " - " + getName() + " Unregulated Flow max" );
                output.setDescription("Duration Plugin Max Unregulated Flow for " + getName());
            }
            else{
                output.setParamId(Parameter.PARAMID_PRECIP);
            }
            
            if(tl.getLinkedToLocation().getParameter().equals("Stage")){
                //dont accumulate
                output.setParamId(Parameter.PARAMID_STAGE);
                output.setName(loc.getName() + " - " + loc.getParameter() + " - " + getName() + " " + 1 + " Day average - max" );
                output.setDescription("Duration Plugin Max Average Stage for " + getName());
            }else if(tl.getLinkedToLocation().getParameter().equals("Temp")){
                //dont accumulate
                output.setParamId(Parameter.PARAMID_TEMP);
                output.setName(loc.getName() + " - " + loc.getParameter() + " - " + getName() + " " + 1 + " Day average - max" );
                output.setDescription("Duration Plugin Max Average Temperature for " + getName());
            }
            else{
            }

            ret.add(output);
//            ret.add(output30Day);
        }
                return ret;
    }
    public boolean hasOutputVariables(){
        if (_outputVariables != null){
            if(_outputVariables.size() == 0){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }
}
