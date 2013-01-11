package player;
/**
 * Figured we'd be doing enough with strategy that a dedicated class would be useful.
 * 
 * @author DC
 *
 */
public class Brain {
	private Match match;
	private double absolute_prob_win; 
	
	public Brain(Match _match){
		match = _match;
	}
	
	/**
	 *  Uses a lookup table to calculate the probability of winning at any time 
	 *  without assuming any knowledge of the opponents hand.
	 *  
	 *  @param holeCards; cards in our hole
	 *  @param tableCards; cards on the table
	 *  @modifies absolute_prob_win;
	 */
	private void updateAPW(String[] holeCards, String[] tableCards){
		absolute_prob_win =  -1.0;
	}
}