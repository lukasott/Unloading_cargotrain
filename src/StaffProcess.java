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
	private int counter;
	
	public StaffProcess(Model owner, String name, boolean showInTrace, double remainingWorkingHours, TrainProcess assignedTrain){
		super(owner, name, showInTrace);
	
		model = (Unloading_cargotrain_model) owner;
		this.remainingWorkingHours = remainingWorkingHours;
		this.assignedTrain = assignedTrain;
	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		
		double remainingUnloadingTime = 0;
		
		// work for (remaining) time
		hold(new TimeSpan(remainingWorkingHours));
		
		if(!assignedTrain.getDoneUnloading()){
			
			// interrupt unloading process
			if(!model.trainQueue.contains(assignedTrain) && !model.terminalUnloadingQueue.isEmpty()){
				
				TerminalProcess terminal = model.terminalUnloadingQueue.first();
				model.terminalUnloadingQueue.remove(terminal);
				model.terminalInterruptedQueue.insert(terminal);
				
				// calculate remaining time of the unloading process
				remainingUnloadingTime = terminal.scheduledNext().getTimeAsDouble() - presentTime().getTimeAsDouble();
				terminal.cancel();
			}
			
			// wait for replacement staff
			assignedTrain.setAssignedStaff(null);
			hold(new TimeSpan(model.getStaffReplacmentTime()));
		}
		
		StaffProcess newStaff = null;
		
		// when unloading isn't done, send for replacement staff
		if(!assignedTrain.getDoneUnloading()){
			
			// assign replacement staff and update statistics
			newStaff = new StaffProcess(model, "staff", true, 12.0, assignedTrain);
			assignedTrain.setAssignedStaff(newStaff);
			assignedTrain.incrementStaffChanges();
			
			// continue unloading
			if(!model.trainQueue.contains(assignedTrain) && !model.terminalInterruptedQueue.isEmpty()){
				
				TerminalProcess terminal = model.terminalInterruptedQueue.first();
				model.terminalInterruptedQueue.remove(terminal);
				model.terminalUnloadingQueue.insert(terminal);
				
				// continue unloading process for the remaining time
				terminal.activate(new TimeSpan(remainingUnloadingTime));
			}
			
			// replacement staff starts working
			newStaff.activate(new TimeSpan(0.0));
		}
		
		// activate waiting terminal if train is the first in the queue
		if(!model.terminalWaitingQueue.isEmpty() && model.trainQueue.first() != null && model.trainQueue.first().equals(assignedTrain))
			model.terminalWaitingQueue.first().activate(new TimeSpan(0.0));
		
	}
}
