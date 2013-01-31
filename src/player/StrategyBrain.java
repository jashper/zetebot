package player;

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
	protected final Opponent opponent;
	
	protected double weight;
	
	public StrategyBrain(Match _match, Opponent opponent){
		 this.match = _match;
		 this.opponent = opponent;
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
	
	protected double getAvrgOppAPW(int boardCount) {
		switch (boardCount) {
			case 0:
				return opponent.preFlopAPW;
			case 3:
				return opponent.flopAPW;
			case 4:
				return opponent.turnAPW;
			case 5:
				return opponent.riverAPW;
		}
		return 0;
	}
	
	protected double[] getAvrgOppAPWCat(int boardCount) {
		switch (boardCount) {
			case 3:
				return opponent.flopAPWCat;
			case 4:
				return opponent.turnAPWCat;
			case 5:
				return opponent.riverAPWCat;
		}
		return new double[9];
	}
	
}
