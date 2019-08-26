/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package durationplugin;
import com.rma.util.XMLUtilities;
import hec2.model.DataLocation;
import hec2.model.DataLocationComputeType;
import hec2.plugin.model.ModelAlternative;
import hec2.plugin.util.DataLocationUtilities;
import org.jdom.Element;
/**
 *
 * @author WatPowerUser
 */
public class DurationLocation extends DataLocation {
    private Integer _duration;
    public DurationLocation(ModelAlternative malt, String name, String parameter, Integer duration){
        super(malt,name,parameter);
        _duration = duration;
    }
    public DurationLocation(){
        super();
    }
    public DurationLocation(DataLocation dl, Integer duration){
        super(dl.getModelAlternative(),dl.getName(),dl.getParameter());
        _duration = duration;
    }
    public Integer getDuration(){
        return _duration;
    }
    public void setDuration(Integer duration){
        _duration = duration;
    }
    public boolean fromXML(Element myElement){
       		if ( !"DataLocation".equals(myElement.getName()) && !"DownStreamLocation".equals(myElement.getName()))
		{
			return false;
		}

		String name = myElement.getAttributeValue("Name");
		String path = myElement.getAttributeValue("DssPath");
		if ( path == null )
		{ //backward compatibility
			path = myElement.getAttributeValue("DSSPath");
		}
		String model = myElement.getAttributeValue("Model");
		String param = myElement.getAttributeValue("Parameter");
		String computeType = myElement.getAttributeValue("ComputeType");
		String desc = myElement.getAttributeValue("Description");
		String altName = myElement.getAttributeValue("AltName");
		
		int prevModelIndex = XMLUtilities.getAttributeValueAsInt(myElement, "PrevModelIndex", 0);
		DataLocationComputeType ctype;
		if ( computeType != null )
		{
			try
			{
				ctype = DataLocationComputeType.valueOf(computeType);
			}
			catch ( Exception e)
			{ 
				ctype = DataLocationComputeType.Computed;
			}
		}
		else
		{
			ctype = DataLocationComputeType.Computed;
		}
		
		//String cls = myElement.getAttributeValue("Class");
		//if ( cls != null )
		{
			setName(name);
			if ( desc != null )
			{
				setDescription(desc);
			}
			setParameter(param);
			//setModelAlternative(mAlt);
		}
		Element mAltElem = myElement.getChild("ModelAlternative");
		if ( mAltElem != null )
		{
			name = mAltElem.getAttributeValue("Name");
			String program = mAltElem.getAttributeValue("Program");
			ModelAlternative malt = new ModelAlternative();
			malt.setName(name);
			malt.setProgram(program);
			setModelAlternative(malt);
		}
		setAlternativeName(altName);
		setDssPath(path);
		setModelToLinkTo(model);
		setComputeType(ctype);
		setPrevModelIndex(prevModelIndex);
		Element dndlElem = myElement.getChild("DownStreamLocation");
		if ( dndlElem != null )
		{
			Element dndlChild = dndlElem.getChild("DataLocation");
			if ( dndlChild == null )
			{ // for backward compatibility...
				dndlChild = dndlElem;
			}
			DataLocation dndl = DataLocationUtilities.createDataLocation(dndlChild);
			dndl.fromXML(dndlChild);
			setLinkedToLocation(dndl);
		}
		return true; 
    }
            
}
