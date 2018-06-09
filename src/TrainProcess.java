import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * 
 * @author Gerald Angerer (00920238), Lukas Ott (01522579)
 *
 */
public class TrainProcess extends SimProcess{

	private Unloading_cargotrain_model model;
	private StaffProcess assignedStaff;
	private int staffChanges = 0;
	private boolean doneUnloading = false;
	
	public StaffProcess getAssignedStaff(){
		return assignedStaff;
	}
	
	public void setAssignedStaff(StaffProcess staff){
		assignedStaff = staff;
	}
	
	public void incrementStaffChanges(){
		staffChanges++;
	}
	
	public boolean getDoneUnloading(){
		return doneUnloading;
	}
	
	public void setDoneUnloading(){
		doneUnloading = true;
	}
	
	public TrainProcess(Model owner, String name, boolean showInTrace){
		super(owner, name, showInTrace);
		
		model = (Unloading_cargotrain_model) owner;
	}
	
	@Override
	public void lifeCycle() throws SuspendExecution{
		model.trainInSystem.insert(this);
	
		// train queues up
		model.trainQueue.insert(this);
		sendTraceNote("Queue length: " + model.trainQueue.length());
		
		if (!model.terminalWaitingQueue.isEmpty()) {
			// next train goes to terminal
			TerminalProcess terminal = model.terminalWaitingQueue.first();
			model.terminalWaitingQueue.remove(terminal);
			
			terminal.activateAfter(this);
			
			// unloading
			passivate();
		}else{
			// train waits in queue
			passivate();
		}
		
		// train leaves
		setDoneUnloading();
		
		model.trainInSystem.remove(this);
		
		if(staffChanges == 0) {
			model.NoneStaffChange.insert(this);
		}
		if(staffChanges == 1) {
			model.OneStaffChange.insert(this);
		}
		if(staffChanges == 2) {
			model.TwoStaffChange.insert(this);
		}
		
		
		// cancel scheduled events for the assigned staff and activate them now,
		// so they terminate the same time as their assigned train
		assignedStaff.cancel();
		assignedStaff.activate();
		sendTraceNote("Train is unloaded and leaves terminal");
	}
}
