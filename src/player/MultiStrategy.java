package player;
/**
 * Picks a strategy to use at random.
 * Uses exponential back off such that winning strategies will be more likely to be chosen and losing strategies less likely.
 * 
 * TODO: figure out how to pick new strategy every hand
 * TODO: figure out how to signal to the brain if a hand has won or lost.
 * 
 * @author DC
 *
 */
public class MultiStrategy extends StrategyBrain {
	protected StrategyBrain[] strategies;
	protected int[] prob_strategies;
	public MultiStrategy(Match _match, Opponent opponent) {
		super(_match, opponent);
		strategies = new StrategyBrain[2];
		strategies[0] = new ExpectedReturnStrategy(match,opponent);
		strategies[1] = new RandomizedOverBet(match,opponent);
		prob_strategies = new int[strategies.length];
		for(int i=0;i<prob_strategies.length;i++){
			prob_strategies[i] = 1;
		}
	}

	@Override
	String bet(int minBet, int maxBet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String raise(int minBet, int maxBet) {
		// TODO Auto-generated method stub
		return null;
	}

}
