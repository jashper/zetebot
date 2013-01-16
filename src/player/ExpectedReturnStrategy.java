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

	public ExpectedReturnStrategy(Match _match, Opponent opponent) {
		super(_match, opponent);
	}
	 
	/**
	 *  Uses a lookup table to calculate the probability of winning at any time 
	 *  without assuming any knowledge of the opponents hand.
	 */
	
	public String bet(int minBet,int maxBet){
		double betAmt = weight * match.abs_prob_win * match.pot;
		if(minBet <= betAmt ){
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
		double raiseAmt = weight * match.abs_prob_win * match.pot;
		if(minRaise <= raiseAmt ){
			if(maxRaise < raiseAmt){
				return "RAISE:"+maxRaise;
			}else{
				return "RAISE:"+((int) raiseAmt);
			}
		}else if(match.amtToCall <= raiseAmt){
			return "CALL";
		}else{
			return "FOLD";
		}
	}

}
