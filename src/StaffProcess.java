import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * 
 * @author Gerald Angerer (00920238), Lukas Ott (01522579)
 *
 */
public class StaffProcess extends SimProcess{

	private Unloading_cargotrain_model model;
	private double remainingWorkingHours;
	private TrainProcess assignedTrain;
	
	public StaffProcess(Model owner, String name, boolean showInTrace, double remainingWorkingHours, TrainProcess assignedTrain){
		super(owner, name, showInTrace);
	
		model = (Unloading_cargotrain_model) owner;
		this.remainingWorkingHours = remainingWorkingHours;
		this.assignedTrain = assignedTrain;
	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		
		// work for (remaining) time
		hold(new TimeSpan(remainingWorkingHours));
		
		// wait for replacement
		assignedTrain.setAssignedStaff(null);
		hold(new TimeSpan(model.getStaffReplacmentTime()));
		
		// assign replacement staff and update statistics
		StaffProcess newStaff = new StaffProcess(model, "staff", true, 0.0, assignedTrain);
		assignedTrain.setAssignedStaff(newStaff);
		assignedTrain.incrementStaffChanges();
		
		newStaff.activateAfter(this);
	}
}
