package player;

/**
 *  Bets based on expected return to hopefully make a slight profit.
 *  weight is used to bet higher or lower than the expected return.
 *  
 * @author DC
 *
 */
public class ExpectedReturnStrategy extends StrategyBrain{
	private Match match;
	 private double abs_prob_win;
	 private double weight;
	 public ExpectedReturnStrategy(Match _match){
		 match = _match;
		 weight = 1.0;
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
	private void updateAPW(){
		abs_prob_win =  -1.0;
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
