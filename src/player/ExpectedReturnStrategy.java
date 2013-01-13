package player;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Map;

/**
 *  Bets based on expected return to hopefully make a slight profit.
 *  weight is used to bet higher or lower than the expected return.
 *  
 * @author DC
 *
 */
public class ExpectedReturnStrategy extends StrategyBrain{

	public ExpectedReturnStrategy(Match _match) {
		super(_match);
	}
	 
	/**
	 *  Uses a lookup table to calculate the probability of winning at any time 
	 *  without assuming any knowledge of the opponents hand.
	 *  
	 *  TODO: Tommy this is where your table goes.
	 *  
	 *  @param holeCards; cards in our hole
	 *  @param tableCards; cards on the table
	 *  @modifies abs_prob_win;
	 */
	private void updateAPW(String[] holeCards, String[] tableCards){ //TODO: Assumes discard is the third holeCard index
		int tableCount = tableCards.length;
		
		int[] holeInts = new int[3];
		for (int i = 0; i < 3 ; i++) {
			holeInts[i] = odds.stringToInt(holeCards[i]);
		}
		
		int[] tableInts = new int[tableCount];
		for (int i = 0; i < tableCount; i++) {
			tableInts[i] = odds.stringToInt(tableCards[i]);
		}
		
		switch (tableCount) {
			case 0: // preFlop
				Arrays.sort(holeInts);
				String lookupStr = "";
				for (int i = 0; i < 3 ; i++) {
					lookupStr += holeInts[i];
				}
				abs_prob_win = APWMap.get(lookupStr);
				break;
			case 3: // flop
				abs_prob_win = odds.getFlopOdds(holeInts[0], holeInts[1], holeInts[2], tableInts[0], tableInts[1], tableInts[2]);
				break;
			case 4: // TODO: turn
				break;
			case 5: // TODO: river
				break;
		}	
	}
	
	public String bet(int minBet,int maxBet){
		updateAPW(match.holeCards, match.tableCards);
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
		updateAPW(match.holeCards, match.tableCards);
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
