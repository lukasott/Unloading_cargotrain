import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * 
 * @author Gerald Angerer (00920238), Lukas Ott (01522579)
 *
 */
public class TrainProcess extends SimProcess{

	private Unloading_cargotrain_model model;
	//private double staffWorkingHours;
	//private boolean staffAtWork = true;
	private StaffProcess assignedStaff;
	private int staffChanges = 0;
	
	public StaffProcess getAssignedStaff(){
		return assignedStaff;
	}
	
	public void setAssignedStaff(StaffProcess staff){
		assignedStaff = staff;
	}
	
	public void incrementStaffChanges(){
		staffChanges++;
	}
	
//	public double getStaffWorkingHours(){
//		return staffWorkingHours;
//	}
//	
//	public boolean getStaffAtWork(){
//		return staffAtWork;
//	}
	
	public TrainProcess(Model owner, String name, boolean showInTrace/*, double hours*/){
		super(owner, name, showInTrace);
		
		model = (Unloading_cargotrain_model) owner;
		//staffWorkingHours = hours;
	}
	
	@Override
	public void lifeCycle() throws SuspendExecution{
	
		model.trainQueue.insert(this);
		sendTraceNote("Queue length: " + model.trainQueue.length());
		
		if (!model.terminalQueue.isEmpty()) {
			// next train goes to terminal
			TerminalProcess terminal = model.terminalQueue.first();
			model.terminalQueue.remove(terminal);
			
			terminal.activateAfter(this);
			
			// unloading
			passivate();
		}else{
			// train waits in queue
			passivate();
			
//			while(this.getActivatedBy() != model.terminalQueue.first()){
//				hold(new TimeSpan(model.getStaffHours() - staffWorkingHours));
//				hold(new TimeSpan(model.getStaffReplacmentTime()));
//				staffWorkingHours = 0;
//			}
		}
		
		// train leaves
		sendTraceNote("Train is unloaded and leaves terminal");
	}
}
