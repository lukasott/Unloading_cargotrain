import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * 
 * @author Gerald Angerer (00920238), Lukas Ott (01522579)
 *
 */
public class NewTrainProcess extends SimProcess{

	private Unloading_cargotrain_model model;
	
	public NewTrainProcess(Model owner, String name, boolean showInTrace){
		super(owner, name, showInTrace);
	
		model = (Unloading_cargotrain_model) owner;
	}
	
	@Override
	public void lifeCycle() throws SuspendExecution{
		while(true){
			    	
			hold(new TimeSpan(model.getTrainArrivalTime()));
 
			TrainProcess newTrain = new TrainProcess(model, "train", true/*, model.getStaffWorkingHours()*/);
			StaffProcess newStaff = new StaffProcess(model, "staff", true, model.getRemainingWorkingHours(), newTrain);
			newTrain.setAssignedStaff(newStaff);

			newTrain.activateAfter(this);
			newStaff.activateAfter(this);
		}
	}
}
