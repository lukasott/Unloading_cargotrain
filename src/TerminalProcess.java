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
				if(!model.terminalWaitingQueue.contains(this))
					model.terminalWaitingQueue.insert(this);
				
				passivate();
			}else{
			    
				TrainProcess train = model.trainQueue.first();
				if(train.getAssignedStaff() != null){
					
					// train staff is working, unload train
					model.trainQueue.remove(train);
					
					model.terminalWaitingQueue.remove(this);
					model.terminalUnloadingQueue.insert(this);
					
					double unloadTime = model.getUnloadTime();
					hold(new TimeSpan(unloadTime));
					
					// unloading is done, signal train to leave
					model.terminalUnloadingQueue.remove(this);
					train.activate(new TimeSpan(0.0));
				}else{
					// train staff isn't working, waiting on replacement staff
					if(!model.terminalWaitingQueue.contains(this))
						model.terminalWaitingQueue.insert(this);
					
					passivate();
				}
			}
	    }
	}
}
