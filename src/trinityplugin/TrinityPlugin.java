/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trinityplugin;
import com.rma.factories.NewObjectFactory;
import hec.data.Parameter;
import hec.model.OutputVariable;
import hec2.map.GraphicElement;
import hec2.model.DataLocation;
import hec2.model.ProgramOrderItem;
import hec2.plugin.action.EditAction;
import hec2.plugin.action.OutputElement;
import hec2.plugin.lang.ModelLinkingException;
import hec2.plugin.lang.OutputException;
import hec2.plugin.model.ModelAlternative;
import hec2.wat.model.tracking.OutputPlugin;
import hec2.wat.model.tracking.OutputVariableImpl;
import hec2.wat.plugin.AbstractSelfContainedWatPlugin;
import hec2.wat.plugin.CreatableWatPlugin;
import hec2.wat.plugin.WatPluginManager;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author WatPowerUser
 */
public class TrinityPlugin extends AbstractSelfContainedWatPlugin<TrinityAlternative> implements CreatableWatPlugin, OutputPlugin  {
    public static final String PluginName = "Trinity Plugin";
    private static final String _pluginVersion = "1.0.0";
    private static final String _pluginSubDirectory = "TrinityPlugin";
    private static final String _pluginExtension = ".tp";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TrinityPlugin p = new TrinityPlugin();
    }
    public TrinityPlugin(){
        super();
        setName(PluginName);
        setProgramOrderItem(new ProgramOrderItem(PluginName,
                "A plugin constructed for Trinity River project",
                false,1,"shortname","Images/fda/wsp.png"));
        WatPluginManager.register(this);
    }
    @Override
    protected String getAltFileExtension() {
        return _pluginExtension;
    }
    @Override
    public String getPluginDirectory() {
        return _pluginSubDirectory;
    }
    @Override
    public String getVersion() {
        return _pluginVersion;
    }
    @Override
    public boolean saveProject() {
        boolean success = true;
        for(TrinityAlternative alt: _altList){
            if(!alt.saveData()){
                success = false;
                System.out.println("Alternative " + alt.getName() + " could not save");
            }
        }
        return success;
    }
    @Override
    protected TrinityAlternative newAlternative(String string) {
        return new TrinityAlternative(string);
    }
    @Override
    protected NewObjectFactory getAltObjectFactory() {
        return new TrinityAlternativeFactory(this);
    }
    @Override
    public List<DataLocation> getDataLocations(ModelAlternative ma, int i) {
        TrinityAlternative alt = getAlt(ma);
        if(alt==null)return null;
        if(DataLocation.INPUT_LOCATIONS == i){
            //input
            return alt.getInputDataLocations();
        }else{
            //ouput
            return alt.getOutputDataLocations();
        }
    }
    @Override
    public boolean setDataLocations(ModelAlternative ma, List<DataLocation> list) throws ModelLinkingException {
        TrinityAlternative alt = getAlt(ma);
        if(alt!=null){
            return alt.setDataLocations(list);
        }
        return true;
    }
    @Override
    public boolean compute(ModelAlternative ma) {
        TrinityAlternative alt = getAlt(ma);
        if(alt!=null){
            alt.setComputeOptions(ma.getComputeOptions());
            return alt.compute();
        }
        return false;
    }
    @Override
    public void editAlternative(TrinityAlternative e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public boolean displayApplication() {
        return false;
    }
    @Override
    public List<GraphicElement> getGraphicElements(ModelAlternative ma) {
        return new ArrayList<GraphicElement>();
    }
    @Override
    public List<OutputElement> getOutputReports(ModelAlternative ma) {
        return new ArrayList<OutputElement>();
    }
    @Override
    public boolean displayEditor(GraphicElement ge) {
        return false;
    }
    @Override
    public boolean displayOutput(OutputElement oe, List<ModelAlternative> list) throws OutputException {
        return false;
    }
    @Override
    public List<EditAction> getEditActions(ModelAlternative ma) {
        return new ArrayList<EditAction>();
    }
    @Override
    public void editAction(String string, ModelAlternative ma) {
        
    }

    @Override
    public List<OutputVariable> getAvailOutputVariables(ModelAlternative ma) {
        List<OutputVariable> ret = new ArrayList<>();
        TrinityAlternative alt = getAlt(ma);
        for(DataLocation loc : alt.getDataLocations())
        {
            TrinityLocation tl = (TrinityLocation)loc;
            OutputVariableImpl output = new OutputVariableImpl();
            output.setName(loc.getName() + " - " + loc.getParameter() +  " - " + ma.getName() + " " + tl.getDuration() + " Day volume duration max" );
            output.setDescription("Trinity Plugin Volume Duration Max for " + ma.getName());
            if(loc.getParameter().equals("Flow")){
                output.setParamId(Parameter.PARAMID_FLOW);
            }else if(loc.getParameter().equals("Inflow")){
                output.setParamId(Parameter.PARAMID_FLOW);
            }else if(loc.getParameter().equals("Flow-Unreg")){
                output.setParamId(Parameter.PARAMID_FLOW);
                output.setName(loc.getName() + " - " + loc.getParameter() +  " - " + ma.getName() + " Unregulated Flow max" );
                output.setDescription("Trinity Plugin Max Unregulated Flow for " + ma.getName());
            }
            else{
                output.setParamId(Parameter.PARAMID_PRECIP);
            }
            
            if(tl.getLinkedToLocation().getParameter().equals("Stage")){
                //dont accumulate
                output.setParamId(Parameter.PARAMID_STAGE);
                output.setName(loc.getName() + " - " + loc.getParameter() + " - " + ma.getName() + " " + tl.getDuration() + " Day average - max" );
                output.setDescription("Trinity Plugin Max Average Stage for " + ma.getName());
            }else if(tl.getLinkedToLocation().getParameter().equals("Temp")){
                //dont accumulate
                output.setParamId(Parameter.PARAMID_TEMP);
                output.setName(loc.getName() + " - " + loc.getParameter() + " - " + ma.getName() + " " + tl.getDuration() + " Day average - max" );
                output.setDescription("Trinity Plugin Max Average Temperature for " + ma.getName());
            }
            else{
//                //this block is for cumulative flow or precip... not for production runs.
//                OutputVariableImpl outputTot = new OutputVariableImpl();
//                outputTot.setName(loc.getName()  + " - " + loc.getParameter() + " - " + ma.getName() + " total");
//                if(loc.getParameter().equals("Flow")){
//                    outputTot.setParamId(Parameter.PARAMID_FLOW);
//                }else if(loc.getParameter().equals("Flow-Unreg")){
//                    outputTot.setParamId(Parameter.PARAMID_FLOW);
//                }else if(loc.getParameter().equals("Inflow")){
//                    outputTot.setParamId(Parameter.PARAMID_FLOW);
//                }else{
//                    outputTot.setParamId(Parameter.PARAMID_PRECIP);
//                }
//                outputTot.setDescription("Trinity Plugin Total Volume for " + ma.getName());
//                ret.add(outputTot);
            }
//            //this block is for 30 day duration.
//            OutputVariableImpl output30Day = new OutputVariableImpl();
//            output30Day.setName(loc.getName() + " - " + loc.getParameter() +  " - " + ma.getName() + " 30 Day volume duration max" );
//            output30Day.setDescription("Trinity Plugin 30 Day Volume Duration Max for " + ma.getName());
//            if(loc.getParameter().equals("Flow")){
//                output30Day.setParamId(Parameter.PARAMID_FLOW);
//            }else if(loc.getParameter().equals("Inflow")){
//                output30Day.setParamId(Parameter.PARAMID_FLOW);
//            }else if(loc.getParameter().equals("Flow-Unreg")){
//                output30Day.setParamId(Parameter.PARAMID_FLOW);
//                output30Day.setName(loc.getName() + " - " + loc.getParameter() +  " - " + ma.getName() + " Unregulated Flow max" );
//                output30Day.setDescription("Trinity Plugin Max Unregulated Flow for " + ma.getName());
//            }
//            if(tl.getLinkedToLocation().getParameter().equals("Stage")){
//                //dont accumulate
//                output30Day.setParamId(Parameter.PARAMID_STAGE);
//                output30Day.setName(loc.getName() + " - " + loc.getParameter() + " - " + ma.getName() + " 30 Day average - max" );
//                output30Day.setDescription("Trinity Plugin Max 30 Day Average Stage for " + ma.getName());
//            }else if(tl.getLinkedToLocation().getParameter().equals("Temp")){
//                //dont accumulate
//                output30Day.setParamId(Parameter.PARAMID_TEMP);
//                output30Day.setName(loc.getName() + " - " + loc.getParameter() + " - " + ma.getName() + " 30 Day average - max" );
//                output30Day.setDescription("Trinity Plugin Max 30 Day Average Temperature for " + ma.getName());
//            }
            ret.add(output);
//            ret.add(output30Day);
        }
        return ret;
    }

    @Override
    public boolean computeOutputVariables(List<OutputVariable> list, ModelAlternative ma) {
        for(OutputVariable o : list){
            OutputVariableImpl oimpl = (OutputVariableImpl)o;
            TrinityAlternative alt = getAlt(ma);
            oimpl.setValue(alt.getOutputValue(oimpl));
        }
        return true;
    }

    @Override
    public boolean hasOutputVariables(ModelAlternative ma) {
        return true;
    }

}
