package player;
/**
 * A model of our opponent.
 * Will contain a number of methods to asses the probability that they are bluffing,
 * what their hand is, etc..
 * TODO: Decide on a modeling strategy.
 * TODO: Implement. 
 * @author DC
 *
 */
public class Opponent {
	public String name;
	public double freqBluff; //Total frequency of bluffing
	/** Each of these denotes the frequency with which this opponent was bluffing 
	 *  at a certain point in the game weighted by how much they were bluffing.
	 * 
	 * Bluffing is determined by: amount_bet - abs_prob_win*pot
	 */
	public double holeBluff; 
	public double flopBluff;
	public double turnBluff;
	public double riverBluff;
	public double newWeight,oldWeight; // newWeight +oldWeight = 1
	
	
	public Opponent(String _name){
		name = _name;
		
	}
	/**
	 * Uses linear deviation to update their respective bluff.
	 * 
	 * @param amtBet
	 * @param probWin
	 * @param pot
	 * @modifies holeBluff
	 */
	public void updateHoleBluff(int amtBet, int probWin, int pot){
		holeBluff = newWeight*((amtBet-probWin*pot)/(probWin*pot)) + oldWeight*holeBluff;
	}
	public void updateFlopBluff(int amtBet, int probWin, int pot){
		flopBluff = newWeight*((amtBet-probWin*pot)/(probWin*pot)) + oldWeight*flopBluff;
	}
	public void updateTurnBluff(int amtBet, int probWin, int pot){
		turnBluff = newWeight*((amtBet-probWin*pot)/(probWin*pot)) + oldWeight*turnBluff;
	}
	public void updateRiverBluff(int amtBet, int probWin, int pot){
		riverBluff = newWeight*((amtBet-probWin*pot)/(probWin*pot)) + oldWeight*riverBluff;
	}

}
