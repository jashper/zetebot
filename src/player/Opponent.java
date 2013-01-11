package src.player;

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
	
	
	public double
	
	public Opponent(String _name){
		name = _name;
		
	}
	/**
	 * 
	 * @return
	 */
	public int estimateHandScore(){
		
	}
}
