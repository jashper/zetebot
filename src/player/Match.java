package player;
import java.util.HashMap;
public class Match {
	public String matchInfo;
	public String oppName;
	public int stackSize;
	public int bb;
	public int numHands;
	public int pot = 0;
	public HashMap<String,String> keyVals;
	
	// NEWGAME yourName oppName stackSize bb numHands timeBank
	public Match(String _matchInfo){
		matchInfo = _matchInfo;
		String[] info = _matchInfo.split(" ");
		oppName = info[2];
		stackSize = Integer.parseInt(info[3]);
		bb = Integer.parseInt(info[4]);
		numHands = Integer.parseInt(info[5]);
	}

}