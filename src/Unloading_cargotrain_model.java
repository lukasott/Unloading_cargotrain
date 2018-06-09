import desmoj.core.simulator.*;
import desmoj.core.statistic.Count;
import desmoj.core.dist.*;

/**
 * 
 * @author Gerald Angerer (00920238), Lukas Ott (01522579)
 *
 */
public class Unloading_cargotrain_model extends Model{
	
	private final int STAFFHOURS = 12;
	public double trainTime = 0;
	
	public int getStaffHours(){
		return STAFFHOURS;
	}
	
	private ContDistExponential trainArrivalTime;

    public double getTrainArrivalTime(){
    	return trainArrivalTime.sample();
    }
	
	private ContDistUniform unloadTime;

    public double getUnloadTime(){
    	return unloadTime.sample();
    }
    
    private ContDistUniform remainingWorkingHours;
    
    public double getRemainingWorkingHours(){
    	return remainingWorkingHours.sample();
    }
    
    private ContDistUniform staffReplacementTime;
    
    public double getStaffReplacmentTime(){
    	return staffReplacementTime.sample();
    }
    
    protected ProcessQueue<TrainProcess> trainQueue;
	protected ProcessQueue<TerminalProcess> terminalWaitingQueue;
	protected ProcessQueue<TerminalProcess> terminalUnloadingQueue;
	protected ProcessQueue<TerminalProcess> terminalInterruptedQueue;
	
	protected ProcessQueue<TrainProcess> trainInSystem;
	protected Count noStaffChanges;
	protected Count oneStaffChange;
	protected Count twoStaffChanges;
	
    public Unloading_cargotrain_model(Model owner, String name, boolean showInReport, boolean showIntrace){
    	super(owner, name, showInReport, showIntrace);
    }

    @Override
    public String description(){
    	return "Unloading_cargotrain: process oriented model, trains arrive at a terminal and are queued up. When the terminal is "
    			+ "available the train moves to the terminal and gets unloaded by the train staff. When the train staff have "
    			+ "reached their maximum working hours, they stop unloading and send replacement staff.";
    }
    
    @Override
    public void doInitialSchedules(){

    	NewTrainProcess newTrain = new NewTrainProcess(this, "create next train", true);
        
		newTrain.activate(new TimeSpan(0.0));
		         
		TerminalProcess terminal = new TerminalProcess(this, "terminal", true);
		terminal.activate(new TimeSpan(0.0));
//		TerminalProcess terminal2 = new TerminalProcess(this, "terminal", true);
//		terminal2.activate(new TimeSpan(0.0));
    }

    @Override
    public void init(){
		
    	// distributions
		trainArrivalTime = new ContDistExponential(this, "Arrival time ", 10.0, true, true);
		trainArrivalTime.setNonNegative(true);
		
		unloadTime = new ContDistUniform(this, "Unload time", 3.5, 4.5, true, true);
		
		remainingWorkingHours = new ContDistUniform(this, "remaining working hours when they arrive", 6.0, 11.0, true, true);
		staffReplacementTime = new ContDistUniform(this, "staff replacement time", 2.5, 3.5, true, true);
		
		// queues
		trainQueue = new ProcessQueue<TrainProcess>(this, "FIFO queue for trains",true, true);
		terminalWaitingQueue = new ProcessQueue<TerminalProcess>(this, "FIFO queue for waiting terminals",true, true);
		terminalUnloadingQueue = new ProcessQueue<TerminalProcess>(this, "FIFO queue for terminals unloading cargotrains", true, true);
		terminalInterruptedQueue = new ProcessQueue<TerminalProcess>(this, "FIFO queue for terminals interrupted while unloading", true, true);
		
		// used for statistics
		trainInSystem = new ProcessQueue<TrainProcess>(this, "Trains in the System", true, true);
		noStaffChanges = new Count(this, "# trains with no staff changes", true, true);
		oneStaffChange = new Count(this, "# trains with one staff change", true, true);
		twoStaffChanges = new Count(this, "# trains with two staff changes", true, true);
		
    }
 
    public static void main(String[] args){

		Experiment experiment = new Experiment("Unloading of cargotrain");
		Unloading_cargotrain_model model = new Unloading_cargotrain_model(null, "Unloading of cargotrain model", true, true);  
		
		experiment.setSeedGenerator(74189762);
		experiment.setShowProgressBar(false);
		
		model.connectToExperiment(experiment);
		
		experiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(24*30*12));
		experiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(24*30));
		
		experiment.stop(new TimeInstant(24*30*12));	// 1 year
		
		experiment.start(); 
		
		experiment.report();
		
		experiment.finish();

    }
}
