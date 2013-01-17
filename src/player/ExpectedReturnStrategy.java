package player;


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
		double avrgOppAPW = getAvrgOppAPW(match.holeCards.size());
		if (avrgOppAPW != 0.0) {
			if (avrgOppAPW <= 0.40) {
				weight *= 2;
			}
			
			if (avrgOppAPW >= 0.75) {
				weight *= 0.5;
			}
		}
		
		if (match.abs_prob_win >= 0.80 && avrgOppAPW <= 0.55 && opponent.infoCount < 75) {
			return "RAISE:"+maxBet;
		}
		
		if (match.abs_prob_win <= 0.45) {
			return "CHECK";
		}
		
		if (match.abs_prob_win <= 0.6 && opponent.infoCount < 75) {
			return "CHECK";
		}
		
		double betAmt = weight * match.abs_prob_win * match.pot;
		weight = 1.0;
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
		double avrgOppAPW = getAvrgOppAPW(match.holeCards.size());
		if (avrgOppAPW != 0.0) {
			if (avrgOppAPW <= 0.40) {
				weight *= 2;
			}
			
			if (avrgOppAPW >= 0.75) {
				weight *= 0.5;
			}
		}
		
		if (match.abs_prob_win >= 0.80 && avrgOppAPW <= 0.55 && opponent.infoCount < 75) {
			return "RAISE:"+maxRaise;
		}
		
		if (match.abs_prob_win >= 0.4 && opponent.infoCount < 75) {
			return "CALL";
		}
		
		if (match.abs_prob_win - avrgOppAPW >= 0.3 && opponent.infoCount >= 75) {
			return "CALL";
		}
		
		if (avrgOppAPW - match.abs_prob_win >= 0.3 && opponent.infoCount >= 75) {
			return "FOLD";
		}
		
		double raiseAmt = weight * match.abs_prob_win * match.pot;
		weight = 1.0;
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
	
	public double getAvrgOppAPW(int boardCount) {
		switch (boardCount) {
			case 3:
				return opponent.flopAPW;
			case 4:
				return opponent.turnAPW;
			case 5:
				return opponent.riverAPW;
		}
		return 0;
	}
	
	public double getAvrgOppWin(int boardCount) {
		switch (boardCount) {
			case 3:
				return opponent.flopWin;
			case 4:
				return opponent.turnWin;
			case 5:
				return opponent.riverWin;
		}
		return 0;
	}

}
