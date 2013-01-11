package player;
import java.util.HashMap;
public class Match {
	public String matchInfo;
	public Opponent opponent;
	public int stackSize;
	public int bb;
	public int numHands;
	public int pot = 0;
	public int minRaise;
	public int maxRaise;
	public int callAmount;
	public int numHandsPlayed;
	public String[] holeCards;
	public String[] tableCards;
	public HashMap<String,String> keyVals;
	
	// NEWGAME yourName oppName stackSize bb numHands timeBank
	public Match(String _matchInfo){
		matchInfo = _matchInfo;
		String[] info = _matchInfo.split(" ");
		opponent = new Opponent(info[2]);
		stackSize = Integer.parseInt(info[3]);
		bb = Integer.parseInt(info[4]);
		numHands = Integer.parseInt(info[5]);
	}

}