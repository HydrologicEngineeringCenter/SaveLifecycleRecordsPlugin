/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package savelifecyclerecordsplugin;
import com.rma.factories.NewObjectFactory;
import hec2.map.GraphicElement;
import hec2.model.DataLocation;
import hec2.model.ProgramOrderItem;
import hec2.plugin.action.EditAction;
import hec2.plugin.action.OutputElement;
import hec2.plugin.lang.ModelLinkingException;
import hec2.plugin.lang.OutputException;
import hec2.plugin.model.ModelAlternative;
import hec2.wat.plugin.AbstractSelfContainedWatPlugin;
import hec2.wat.plugin.CreatableWatPlugin;
import hec2.wat.plugin.WatPluginManager;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author WatPowerUser
 */
public class SaveLifecycleRecordsPlugin extends AbstractSelfContainedWatPlugin<SaveLifecycleRecordsAlternative> implements CreatableWatPlugin  {
    public static final String PluginName = "Save Lifecycle Records Plugin";
    private static final String _pluginVersion = "1.0.0";
    private static final String _pluginSubDirectory = "SLRPlugin";
    private static final String _pluginExtension = ".slrp";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SaveLifecycleRecordsPlugin p = new SaveLifecycleRecordsPlugin();
    }
    public SaveLifecycleRecordsPlugin(){
        super();
        setName(PluginName);
        setProgramOrderItem(new ProgramOrderItem(PluginName,
                "A plugin to help manage lifecycle file sizes",
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
        for(SaveLifecycleRecordsAlternative alt: _altList){
            if(!alt.saveData()){
                success = false;
                System.out.println("Alternative " + alt.getName() + " could not save");
            }
        }
        return success;
    }
    @Override
    protected SaveLifecycleRecordsAlternative newAlternative(String string) {
        return new SaveLifecycleRecordsAlternative(string);
    }
    @Override
    protected NewObjectFactory getAltObjectFactory() {
        return new SaveLifecycleRecordsAlternativeFactory(this);
    }
    @Override
    public List<DataLocation> getDataLocations(ModelAlternative ma, int i) {
        SaveLifecycleRecordsAlternative alt = getAlt(ma);
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
        SaveLifecycleRecordsAlternative alt = getAlt(ma);
        if(alt!=null){
            return alt.setDataLocations(list);
        }
        return true;
    }
    @Override
    public boolean compute(ModelAlternative ma) {
        SaveLifecycleRecordsAlternative alt = getAlt(ma);
        if(alt!=null){
            alt.setComputeOptions(ma.getComputeOptions());
            return alt.compute();
        }
        return false;
    }
    @Override
    public void editAlternative(SaveLifecycleRecordsAlternative e) {
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

}
