package player;

import java.util.Arrays;
import java.util.Map;

import tools.OddsGenerator;

/**
 * Abstract class for our strategies.
 * Means that we only need to implement methods for how much to bet or raise by
 * when we build new strategies.
 * 
 * TODO: Reimplement getAction. It's a mess right now.
 * @author DC
 *
 */
public abstract class StrategyBrain {
	protected final Match match;
	protected final OddsGenerator oddsGen;
	protected final Map<String, Double> APWMap;
	protected final Opponent opponent;
	
	protected double abs_prob_win;
	protected double weight;
	
	public StrategyBrain(Match _match, Opponent opponent, OddsGenerator oddsGen, Map<String, Double> APWMap){
		 this.match = _match;
		 this.opponent = opponent;
		 this.oddsGen = oddsGen;
		 this.APWMap = APWMap;
		 weight = 1.0;
	 }

	abstract String bet(int minBet, int maxBet);
	abstract String raise(int minBet, int maxBet);
	
	private String discard(){
		return ("DISCARD:" + match.discard);
	}
	
	public String getAction(String[] legalActions){
		
		for(String action: legalActions){
			if(action.equals("DISCARD")){
				return discard();//If this is a legal action it will be the only legal action
			}else if(action.matches("(BET|RAISE):([0-9]+):([0-9]+)")){
				String[] vals = action.split(":");
				if(vals[0].equals("RAISE")){
					return raise(new Integer(vals[1]),new Integer(vals[2]));
				}
				else{
					return bet(new Integer(vals[1]),new Integer(vals[2]));		
				}
			}
		}
		return "FOLD";
	}
	
	protected void updateAPW(){
		int holeCount = match.holeCards.size();
		int tableCount = match.tableCards.size();
		
		int[] holeInts = new int[holeCount];
		for (int i = 0; i < holeCount ; i++) {
			holeInts[i] = oddsGen.stringToInt(match.holeCards.get(i));
		}
		
		int[] tableInts = new int[tableCount];
		for (int i = 0; i < tableCount; i++) {
			tableInts[i] = oddsGen.stringToInt(match.tableCards.get(i));
		}
		
		switch (tableCount) {
			case 0: // preFlop
				// TODO: remove missing lookup logic
				Arrays.sort(holeInts);
				String lookupStr = holeInts[0] + " " + holeInts[1] + " " + holeInts[2];
				if (APWMap.containsKey(lookupStr)) {
					abs_prob_win = APWMap.get(lookupStr);
				} else {
					abs_prob_win = 0.0;
				}
				return;
			case 3: // flop
				abs_prob_win = oddsGen.getFlopOdds(holeInts[0], holeInts[1], oddsGen.stringToInt(match.discard), 
												tableInts[0], tableInts[1], tableInts[2]);
				return;
			case 4: // turn
				abs_prob_win = oddsGen.getTurnOdds(holeInts[0], holeInts[1], oddsGen.stringToInt(match.discard), 
						tableInts[0], tableInts[1], tableInts[2], tableInts[3]);
				return;
			case 5: // river
				abs_prob_win = oddsGen.getRiverOdds(holeInts[0], holeInts[1], oddsGen.stringToInt(match.discard), 
						tableInts[0], tableInts[1], tableInts[2], tableInts[3], tableInts[4]);
				return;
		}	
	}
	
}
