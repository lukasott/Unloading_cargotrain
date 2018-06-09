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

			// get arrival interval and wait for next train
			hold(new TimeSpan(model.getTrainArrivalTime()));

			// create train and staff
			TrainProcess newTrain = new TrainProcess(model, "train", true);
			StaffProcess newStaff = new StaffProcess(model, "staff", true, model.getRemainingWorkingHours(), newTrain);
			newTrain.setAssignedStaff(newStaff);

			// activate train and staff
			newTrain.activateAfter(this);
			newStaff.activateAfter(this);
		}
	}
}
