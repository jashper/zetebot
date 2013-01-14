package player;
import java.util.HashMap;
import java.util.ArrayList;
/**
 * Models the game state at any given time.
 * 
 * Could also be used to track the game between hands if we decide this is important
 * 
 * TODO: Finish implementation.
 * @author DC
 *
 */
public class Match {
	// Game state variables
	public int stackSize;
	public int bb;
	public boolean haveButton;
	public ArrayList<String> holeCards;
	public ArrayList<String> tableCards;
	public String discard;
	public int pot;
	public int amtToCall;
	// Intrahand tracking variables
	public ArrayList<String> lastActions;
	// Interhand tracking variables.
	public HashMap<String,String> keyVals;
	public ArrayList<Integer> ourBankVals;
	public ArrayList<Integer> oppBankVals;
	public int numHands;
	public int handId;
	
	// NEWGAME yourName oppName stackSize bb numHands timeBank
	public Match(String _matchInfo){
		String[] info = _matchInfo.split(" ");
		stackSize = Integer.parseInt(info[3]);
		bb = Integer.parseInt(info[4]);
		numHands = Integer.parseInt(info[5]);
		pot = 0;
		discard = null;
		holeCards = new ArrayList<String>();
		tableCards = new ArrayList<String>();
		lastActions = new ArrayList<String>();
	}
	
	public void addBankVals(int ourVal,int oppVal){
		ourBankVals.add(ourVal);
		oppBankVals.add(oppVal);
	}
	
	public void handCleanup() {
		holeCards.clear();
		tableCards.clear();
		lastActions.clear();
		discard = null;
	}

}