/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package durationplugin;
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
public class DurationPlugin extends AbstractSelfContainedWatPlugin<DurationAlternative> implements CreatableWatPlugin, OutputPlugin  {
    public static final String PluginName = "Duration Plugin";
    private static final String _pluginVersion = "1.0.0";
    private static final String _pluginSubDirectory = "DurationPlugin";
    private static final String _pluginExtension = ".dp";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DurationPlugin p = new DurationPlugin();
    }
    public DurationPlugin(){
        super();
        setName(PluginName);
        setProgramOrderItem(new ProgramOrderItem(PluginName,
                "A plugin to compute duration maximums",
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
        for(DurationAlternative alt: _altList){
            if(!alt.saveData()){
                success = false;
                System.out.println("Alternative " + alt.getName() + " could not save");
            }
        }
        return success;
    }
    @Override
    protected DurationAlternative newAlternative(String string) {
        return new DurationAlternative(string);
    }
    @Override
    protected NewObjectFactory getAltObjectFactory() {
        return new DurationAlternativeFactory(this);
    }
    @Override
    public List<DataLocation> getDataLocations(ModelAlternative ma, int i) {
        DurationAlternative alt = getAlt(ma);
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
        DurationAlternative alt = getAlt(ma);
        if(alt!=null){
            return alt.setDataLocations(list);
        }
        return true;
    }
    @Override
    public boolean compute(ModelAlternative ma) {
        DurationAlternative alt = getAlt(ma);
        if(alt!=null){
            alt.setComputeOptions(ma.getComputeOptions());
            return alt.compute();
        }
        return false;
    }
    @Override
    public void editAlternative(DurationAlternative e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public boolean displayApplication() {
        return false;
    }
    @Override
    public List<GraphicElement> getGraphicElements(ModelAlternative ma) {
        return new ArrayList<>();
    }
    @Override
    public List<OutputElement> getOutputReports(ModelAlternative ma) {
        return new ArrayList<>();
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
        return new ArrayList<>();
    }
    @Override
    public void editAction(String string, ModelAlternative ma) {
        
    }

    @Override
    public List<OutputVariable> getAvailOutputVariables(ModelAlternative ma) {
        List<OutputVariable> ret = new ArrayList<>();
        DurationAlternative alt = getAlt(ma);
        return alt.getOutputVariables();

    }

    @Override
    public boolean computeOutputVariables(List<OutputVariable> list, ModelAlternative ma) {
        for(OutputVariable o : list){
            OutputVariableImpl oimpl = (OutputVariableImpl)o;
            DurationAlternative alt = getAlt(ma);
            oimpl.setValue(alt.getOutputValue(oimpl));
        }
        return true;
    }

    @Override
    public boolean hasOutputVariables(ModelAlternative ma) {
        return true;
    }

}
