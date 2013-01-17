package player;
/**
 * Starts each hand with relatively modest bets increasing rapidly if it detects any weakness in the opponent.
 * @author DC
 *
 */

public class AggressiveStrategy extends StrategyBrain {
	protected double AGGRESSION_THRESHOLD = .33;//If our odds are intitially below this threshold we won't try to be aggressive.
	protected double AGGRESSION_FACTOR = 1.5;
	public AggressiveStrategy(Match _match, Opponent opponent) {
		super(_match, opponent);
	}

	private int amtBet(int minBet, int maxBet){
		double betAmt = 0;
		//Decide if its worth being aggressive
		if(match.tableCards.size()==0){
			if(match.abs_prob_win < AGGRESSION_THRESHOLD){
				return -1; 
			}else{
				betAmt = match.abs_prob_win*match.pot;
			}
		}else if(match.tableCards.size()==3){
			if(opponent.bluffMetric(1) > 50){
				betAmt = (opponent.bluffMetric(1)/50)*AGGRESSION_FACTOR*match.abs_prob_win*match.pot;
			}else{
				return -1;
			}
		}else if(match.tableCards.size()==4){
			if(opponent.bluffMetric(2) > 50){
				betAmt = (opponent.bluffMetric(1)/50)*AGGRESSION_FACTOR*AGGRESSION_FACTOR*match.abs_prob_win*match.pot;
			}else{
				return -1;
			}
		}else if(match.tableCards.size()==5){
			if(opponent.bluffMetric(2) > 50){
				betAmt = (opponent.bluffMetric(1)/50)*AGGRESSION_FACTOR*AGGRESSION_FACTOR*AGGRESSION_FACTOR*match.abs_prob_win*match.pot;
			}else{
				return -1;
			}
		}
		betAmt += (betAmt/10)*Math.random() - (betAmt/20); //add some noise to confuse machine learning
		return (int) Math.round(betAmt);
	}
	@Override
	public String bet(int minBet, int maxBet) {
		int betAmt = this.amtBet(minBet, maxBet);
		if(betAmt == -1) return "FOLD";
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

	@Override
	public String raise(int minRaise, int maxRaise) {
		int raiseAmt = this.amtBet(minRaise,maxRaise);
		if(raiseAmt == -1) return "FOLD";
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
