package player;

import player.Match;

/**
 * Figured we'd be doing enough with strategy that a dedicated class would be useful.
 * 
 * A note on terminology: I'm using wager to denote a generic amount that our bot will 
 * be willing to bet, raise to or check on.
 * 
 * @author DC
 *
 */
public class Brain {
	private Match match;
	private double abs_prob_win; 
	
	public Brain(Match _match){
		match = _match;
	}
	public void processTurn(){
		updateAPW(match.holeCards,match.tableCards);
	}
	/**
	 *  Uses a lookup table to calculate the probability of winning at any time 
	 *  without assuming any knowledge of the opponents hand.
	 *  
	 *  @param holeCards; cards in our hole
	 *  @param tableCards; cards on the table
	 *  @modifies abs_prob_win;
	 */
	private void updateAPW(String[] holeCards, String[] tableCards){
		abs_prob_win =  -1.0;
	}
	/**
	 * STRATEGY FUNCTION
	 * 
	 * Determines an amount to wager based entirely on expected return.
	 * This is simplistic. Every complex strategy should outperform this.
	 * 
	 * The wager will be turned into a valid integer method by a later method.
	 * 
	 * @returns double 
	 */
	private double expectedReturnWager(){
		return abs_prob_win*match.pot -1;
	}
	
	/**
	 * Allows us to change our strategy without having to rewrite large sections of code.
	 * Of course more complex strategies will make a distinction between betting, raising, 
	 * and calling so this might not last long.
	 * 
	 * @return  double specifying the amount we are willing to wager based on strategy functions
	 */
	private double wagerAmt(){
		return expectedReturnWager();
	}
	/**
	 * 
	 * @return String specifying the action that our bot will take.
	 */
	public String getAction(){
		
	}
	
	
}