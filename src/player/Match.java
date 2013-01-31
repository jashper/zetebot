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
	public int[] potAt; // records the amount in the pot before any new bets are made
	public int[] oppWagerAt; // records the amount our opponent bet at a given time
	public int amtToCall;
	public double playerAPW;
	public ArrayList<String> holeCards;
	public ArrayList<String> tableCards;
	public String discard;
	public ArrayList<String> lastActions;
	
	// Interhand tracking variables.
	public HashMap<String,String> keyVals;
	public int handId;
	
	// Debug variables
	public double preFlopMaxBetPercent;
	public double flopMaxBetPercent;
	public double turnMaxBetPercent;
	public double riverMaxBetPercent;
	public ArrayList<String> dataPointsPreFlop;
	public ArrayList<String> dataPointsFlop;
	public ArrayList<String> dataPointsTurn;
	public ArrayList<String> dataPointsRiver;
	
	// NEWGAME yourName oppName stackSize bb numHands timeBank
	public Match(String _matchInfo){
		String[] info = _matchInfo.split(" ");
		stackSize = Integer.parseInt(info[3]);
		bb = Integer.parseInt(info[4]);
		sb = bb / 2;
		numHands = Integer.parseInt(info[5]);
		
		pot = 0;
		potAt = new int[4];
		potAt[0] = this.bb;
		oppWagerAt= new int[4];
		amtToCall = 0;
		playerAPW = 0;
		
		holeCards = new ArrayList<String>();
		tableCards = new ArrayList<String>();
		discard = null;
		lastActions = new ArrayList<String>();
		
		preFlopMaxBetPercent = 0;
		flopMaxBetPercent = 0;
		turnMaxBetPercent = 0;
		riverMaxBetPercent = 0;
		dataPointsPreFlop = new ArrayList<String>();
		dataPointsFlop = new ArrayList<String>();
		dataPointsTurn = new ArrayList<String>();
		dataPointsRiver = new ArrayList<String>();
	}
	
	public void handCleanup() {
		pot = 0;
		amtToCall = 0;
		playerAPW = 0;
		
		holeCards.clear();
		tableCards.clear();
		discard = null;
		lastActions.clear();
		
		preFlopMaxBetPercent = 0;
		flopMaxBetPercent = 0;
		turnMaxBetPercent = 0;
		riverMaxBetPercent = 0;
		
		for (int i = 0; i < 4; i++) {
			potAt[i] = 0;
			oppWagerAt[i] = 0;
		}
	}

}