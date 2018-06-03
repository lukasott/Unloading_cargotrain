import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.math.*;

/**
 * 
 * @author Gerald Angerer (00920238), Lukas Ott (01522579)
 *
 */
public class TerminalProcess extends SimProcess{

	private Unloading_cargotrain_model model;

	public TerminalProcess(Model owner, String name, boolean showInTrace){
		super(owner, name, showInTrace);
	
		model = (Unloading_cargotrain_model) owner;
	}
	
	@Override
	public void lifeCycle() throws SuspendExecution{
	    
		while(true){
	        
			if(model.trainQueue.isEmpty()) {
				// wait until train arrives
				model.terminalQueue.insert(this);
				passivate();
			}else{
			    // unload train
				TrainProcess train = model.trainQueue.first();
				if(train.getAssignedStaff() != null){
					// train staff is working
					model.trainQueue.remove(train);
					
					//model.terminalQueue.remove(this);
					
					double unloadTime = model.getUnloadTime();
					
//					double timeUntilReplacementCalled = model.getStaffHours() - train.getStaffWorkingHours();
//					
//					hold(new TimeSpan(Math.min(unloadTime, timeUntilReplacementCalled)));
//					
//					hold(new TimeSpan(model.getStaffReplacmentTime()));
//					
//					hold(new TimeSpan(unloadTime - (timeUntilReplacementCalled)));
					
					train.activate(new TimeSpan(0.0));
				}else{
					// train staff isn't working, waiting on replacement staff
					model.terminalQueue.insert(this);
					passivate();
				}
			}
	    }
	}
}
