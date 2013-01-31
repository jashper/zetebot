package player;

public class oddsBasedStrategy extends StrategyBrain{

	public oddsBasedStrategy(Match _match, Opponent opponent) {
		super(_match, opponent);
	}
	 
	/**
	 *  Uses a lookup table to calculate the probability of winning at any time 
	 *  without assuming any knowledge of the opponents hand.
	 */
	
	public String bet(int minBet,int maxBet){
		weight = 1.0;
		double avrgOppAPW = getAvrgOppAPW(match.tableCards.size());
		double[] avrgOppAPWCat = getAvrgOppAPWCat(match.tableCards.size());
		if (avrgOppAPW != 0.0) {
			if (avrgOppAPW <= 0.40 && match.tableCards.size() >= 3) {
				weight *= 2;
			}
			
			if (avrgOppAPW >= 0.75) {
				weight *= 0.5;
			}
		}
		
		System.out.println("#######ZETEAPW: "+match.playerAPW);
		System.out.println("#######oppAPW: "+avrgOppAPW);
		System.out.println("#######oppAPWCat: "+avrgOppAPWCat[0]+" "+avrgOppAPWCat[1]+" "+
							avrgOppAPWCat[2]+" "+avrgOppAPWCat[3]+" "+avrgOppAPWCat[4]+" "+avrgOppAPWCat[5]+" "+
							avrgOppAPWCat[6]+" "+avrgOppAPWCat[7]+" "+avrgOppAPWCat[8]);
		
		if (match.playerAPW >= 0.82 && avrgOppAPW <= 0.55 && opponent.riverInfoCount >= 10) {
			return "BET:"+maxBet;
		}
		
		if (match.playerAPW <= 0.70 && opponent.riverInfoCount < 10) {
			return "CHECK";
		}
		
		if (match.playerAPW <= 0.65) {
			return "CHECK";
		}
		
		
		double betAmt = weight * match.playerAPW * match.pot;
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
		weight = 1.0;
		double avrgOppAPW = getAvrgOppAPW(match.tableCards.size());
		double[] avrgOppAPWCat = getAvrgOppAPWCat(match.tableCards.size());
		if (avrgOppAPW != 0.0) {
			if (avrgOppAPW <= 0.40  && match.tableCards.size() >= 3) {
				weight *= 2;
			}
			
			if (avrgOppAPW >= 0.75) {
				weight *= 0.5;
			}
		}
		System.out.println("#######ZETEAPW: "+match.playerAPW);
		System.out.println("#######oppAPW: "+avrgOppAPW);
		System.out.println("#######oppAPWCat: "+avrgOppAPWCat[0]+" "+avrgOppAPWCat[1]+" "+
							avrgOppAPWCat[2]+" "+avrgOppAPWCat[3]+" "+avrgOppAPWCat[4]+" "+avrgOppAPWCat[5]+" "+
							avrgOppAPWCat[6]+" "+avrgOppAPWCat[7]+" "+avrgOppAPWCat[8]);
		
		if (match.playerAPW >= 0.82 && avrgOppAPW <= 0.55 && opponent.riverInfoCount >= 10) {
			return "RAISE:"+maxRaise;
		}
		
		if (match.playerAPW >= 0.5 && opponent.riverInfoCount < 10) {
			return "CALL";
		}
		
		if (match.playerAPW - avrgOppAPW >= 0.3 && opponent.riverInfoCount >= 10  && match.tableCards.size() >= 3) {
			return "CALL";
		}
		
		if (avrgOppAPW - match.playerAPW >= 0.25 && opponent.riverInfoCount >= 10) {
			return "FOLD";
		}
		
		double raiseAmt = weight * match.playerAPW * match.pot;
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
	

}
