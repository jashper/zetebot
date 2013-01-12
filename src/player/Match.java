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
	public String[] holeCards;
	public String[] tableCards;
	public int pot;
	public int amtToCall;
	// Intrahand tracking variables
	public String[] lastActions;
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
	}
	
	public void addBankVals(int ourVal,int oppVal){
		ourBankVals.add(ourVal);
		oppBankVals.add(oppVal);
	}

}