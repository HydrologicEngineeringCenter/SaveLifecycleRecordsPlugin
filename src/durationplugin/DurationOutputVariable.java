/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package durationplugin;

import hec.model.OutputVariable;
import hec2.model.DataLocation;
import hec2.wat.model.tracking.OutputVariableImpl;
import org.jdom.Element;



/**
 *
 * @author Q0HECWPL
 */
public class DurationOutputVariable{
    private Integer _duration;
    private boolean _durationInDays;
    private DurationComputeTypes _computeType;
    private DataLocation _location;
    private String _DataLocationName;
    private final OutputVariable _outputVariable;
    public DataLocation getLocation(){
        return _location;
    }
    public Integer getDuration(){
        return _duration;
    }
    public boolean durationInDays(){
        return _durationInDays;
    }
    public DurationComputeTypes getComputeType(){
        return _computeType;
    }
    public OutputVariable getOutputVariable(){
        return _outputVariable;
    }
    public DurationOutputVariable(DataLocation location, Integer duration, boolean durationInDays, DurationComputeTypes computeType){
        //initailize the output variable
        _outputVariable = new OutputVariableImpl();
        //            DataLocation tl = loc;
//            OutputVariableImpl output = new OutputVariableImpl();
//            output.setName(loc.getName() + " - " + loc.getParameter() +  " - " + getName() + " " + 1 + " Day volume duration max" );
//            output.setDescription("Duration Plugin Volume Duration Max for " + getName());
//            if(loc.getParameter().equals("Flow")){
//                output.setParamId(Parameter.PARAMID_FLOW);
//            }else if(loc.getParameter().equals("Inflow")){
//                output.setParamId(Parameter.PARAMID_FLOW);
//            }else if(loc.getParameter().equals("Flow-Unreg")){
//                output.setParamId(Parameter.PARAMID_FLOW);
//                output.setName(loc.getName() + " - " + loc.getParameter() +  " - " + getName() + " Unregulated Flow max" );
//                output.setDescription("Duration Plugin Max Unregulated Flow for " + getName());
//            }
//            else{
//                output.setParamId(Parameter.PARAMID_PRECIP);
//            }
//            
//            if(tl.getLinkedToLocation().getParameter().equals("Stage")){
//                //dont accumulate
//                output.setParamId(Parameter.PARAMID_STAGE);
//                output.setName(loc.getName() + " - " + loc.getParameter() + " - " + getName() + " " + 1 + " Day average - max" );
//                output.setDescription("Duration Plugin Max Average Stage for " + getName());
//            }else if(tl.getLinkedToLocation().getParameter().equals("Temp")){
//                //dont accumulate
//                output.setParamId(Parameter.PARAMID_TEMP);
//                output.setName(loc.getName() + " - " + loc.getParameter() + " - " + getName() + " " + 1 + " Day average - max" );
//                output.setDescription("Duration Plugin Max Average Temperature for " + getName());
//            }
//            else{
//            }
    }
    public Element writeToXML(){
        Element ele = new Element("DurationOutputVariable");
        ele.setAttribute("Duration", getDuration().toString());
        ele.setAttribute("DurationValueRepresentsDays", Boolean.toString(durationInDays()));
        ele.setAttribute("ComputeType", getComputeType().toString());
        return ele;
    }
    public static DurationOutputVariable readFromElement(Element ele, DataLocation loc){
        Integer duration = Integer.parseInt(ele.getAttribute("Duration").getValue());
        boolean durationInDays = Boolean.parseBoolean(ele.getAttribute("DurationValueRepresentsDays").getValue());
        DurationComputeTypes computeType = DurationComputeTypes.valueOf(ele.getAttribute("ComputeType").getValue());
        return new DurationOutputVariable(loc,duration,durationInDays,computeType);
    }
}
