package player;
import java.util.HashMap;
import java.util.ArrayList;
/**
 * Models the game state at any given time.
 * 
 * Could also be used to track the game between hands if we decide this is important
 * 
 * @author DC
 *
 */
public class Match {
	// Match configuration variables
	public int stackSize;
	public int bb;
	public int sb;
	public int numHands;
	
	// Intrahand tracking variables
	public boolean haveButton;
	public int pot;
	public int amtToCall;
	public double playerAPW;
	public ArrayList<String> holeCards;
	public ArrayList<String> tableCards;
	public String discard;
	public ArrayList<String> lastActions;
	
	// Interhand tracking variables.
	public HashMap<String,String> keyVals;
	public int handId;
	
	// NEWGAME yourName oppName stackSize bb numHands timeBank
	public Match(String _matchInfo){
		String[] info = _matchInfo.split(" ");
		stackSize = Integer.parseInt(info[3]);
		bb = Integer.parseInt(info[4]);
		sb = bb / 2;
		numHands = Integer.parseInt(info[5]);
		
		pot = 0;
		amtToCall = 0;
		playerAPW = 0;
		
		holeCards = new ArrayList<String>();
		tableCards = new ArrayList<String>();
		discard = null;
		lastActions = new ArrayList<String>();
	}
	
	public void handCleanup() {
		pot = 0;
		amtToCall = 0;
		playerAPW = 0;
		
		holeCards.clear();
		tableCards.clear();
		discard = null;
		lastActions.clear();
	}

}