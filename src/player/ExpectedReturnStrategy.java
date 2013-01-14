package player;

import java.util.Arrays;
import java.util.Map;

import tools.OddsGenerator;

/**
 *  Bets based on expected return to hopefully make a slight profit.
 *  weight is used to bet higher or lower than the expected return.
 *  
 * @author DC
 *
 */
public class ExpectedReturnStrategy extends StrategyBrain{

	public ExpectedReturnStrategy(Match _match, OddsGenerator oddsGen, Map<String, Double> APWMap) {
		super(_match, oddsGen, APWMap);
	}
	 
	/**
	 *  Uses a lookup table to calculate the probability of winning at any time 
	 *  without assuming any knowledge of the opponents hand.
	 *  
	 *  @param holeCards; cards in our hole
	 *  @param tableCards; cards on the table
	 *  @modifies abs_prob_win;
	 */
	private void updateAPW(){
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
				Arrays.sort(holeInts);
				String lookupStr = "";
				for (int i = 0; i < 3 ; i++) {
					lookupStr += holeInts[i];
				}
				abs_prob_win = APWMap.get(lookupStr);
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
	
	public String bet(int minBet,int maxBet){
		updateAPW();
		double betAmt = weight*abs_prob_win*match.pot;
		if(minBet < betAmt ){
			if(maxBet < betAmt){
				return "BET:"+maxBet;
			}else{
			return "BET:"+((int) betAmt);
			}
		}else{
			return "CHECK";
		}
	}
	 
	public String raise(int minRaise, int maxRaise){
		updateAPW();
		double raiseAmt = weight*abs_prob_win*match.pot;
		if(minRaise < raiseAmt ){
			if(maxRaise < raiseAmt){
				return "BET:"+maxRaise;
			}else{
			return "BET:"+((int) raiseAmt);
			}
		}else if(match.amtToCall < raiseAmt){
			return "CALL";
		}else{
			return "FOLD";
			}
	}

}
