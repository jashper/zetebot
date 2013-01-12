package player;

import player.Match;

/**
 * Figured we'd be doing enough with strategy that a dedicated class would be useful.
 * 
 * A note on terminology: I'm using wager to denote a generic amount that our bot will 
 * be willing to bet, raise to, or check on.
 * 
 * @author DC
 *
 */
public class Brain {
	private Match match;
	private double abs_prob_win;
	private int[] wagerAmts; //{amountToBet, amountToCheckTo, amountToRaise, amountToReraiseTo}
	
	public Brain(Match _match){
		match = _match;
		wagerAmts = new int[4];
	}
	//########################################################################################
	//### CONTROL ############################################################################
	//########################################################################################
	public void processTurn(){
		updateAPW(match.holeCards,match.tableCards);
	}
	/**
	 * This function interfaces between the brains strategy functions and the Player class.
	 * 
	 * LegalActions are:
	 * 	BET:minBet:maxBet
	 *  RAISE:minRaise:maxRaise
	 * 	CALL
	 * 	CHECK
	 *  DISCARD
	 *  FOLD
	 * TODO: are there other legalActions scenarios we need to deal with?
	 * 
	 * @param String[] specifying possible legalActions
	 * @return String specifying the action that our bot will take.
	 */
	public String getAction(String[] legalActions){
		for(String action: legalActions){
			if(action.equals("DISCARD")){
				return discard();//If this is a legal action it will be the only legal action
			}else if(action.matches("(BET|RAISE):([0-9]+):([0-9]+)")){
				if(fold()) return "FOLD";//Any time you can bet or raise folding will be an option.
				else{
					String[] vals = action.split(":");
					if(vals[0].equals("RAISE")) return raise(new Integer(vals[1]),new Integer(vals[2]));
					else return bet(new Integer(vals[1]),new Integer(vals[2]));
				}		
			}
		}
		return "FOLD";
	}
	//########################################################################################
	//### STRATEGY ###########################################################################
	//########################################################################################
	/**
	 * All of our strategies should implement these four functions. 
	 * It might be worth making this into an interface.
	 * 
	 * It's worth noting that bet and raise also need to be able to return CALL or CHECK if 
	 * those actions are desirable.
	 */
	private String bet(int minBet, int maxBet){
		
	}
	private String raise(int minRaise, int maxRaise){
		
	}
	private String discard(){
		
	}
	private boolean fold(){
		
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
}