package player;

import java.util.ArrayList;

import tools.OddsGenerator;

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
	public final String name;
	public final Match match;
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
	
	private final OddsGenerator oddsGen;
	public double flopAPW;
	public double turnAPW;
	public double riverAPW;
	public double flopWin;
	public double turnWin;
	public double riverWin;
	private int infoCount;
	
	
	public Opponent(String _name, Match match, OddsGenerator oddsGen){
		name = _name;
		this.match = match;
		this.oddsGen = oddsGen;
		
		flopAPW = 0.0;
		turnAPW = 0.0;
		riverAPW = 0.0;
		flopWin = 0.0;
		turnWin = 0.0;
		riverWin = 0.0;
		infoCount = 0;
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
	
	public void updateOpponentAPW(String[] holeCards) {
		infoCount++;
		
		int[] holeInts = new int[2];
		for (int i = 0; i < 2 ; i++) {
			holeInts[i] = oddsGen.stringToInt(holeCards[i]);
		}
		
		int[] tableInts = new int[5];
		for (int i = 0; i < 5; i++) {
			tableInts[i] = oddsGen.stringToInt(match.tableCards.get(i));
		}
		
		double newFlopAPW = oddsGen.getFlopOddsNoDisc(holeInts[0], holeInts[1], tableInts[0], tableInts[1], tableInts[2]);
		double newTurnAPW = oddsGen.getTurnOddsNoDisc(holeInts[0], holeInts[1], tableInts[0], tableInts[1], tableInts[2], tableInts[3]);
		double newRiverAPW = oddsGen.getRiverOddsNoDisc(holeInts[0], holeInts[1], tableInts[0], tableInts[1], tableInts[2], tableInts[3], tableInts[4]);
		
		flopAPW = (flopAPW + newFlopAPW) / infoCount;
		turnAPW = (turnAPW + newTurnAPW) / infoCount;
		riverAPW = (riverAPW + newRiverAPW) / infoCount;
		
		flopWin = (flopWin + match.runningPot.get(0)) / infoCount;
		turnWin = (turnWin + match.runningPot.get(1)) / infoCount;
		riverWin = (riverWin + match.runningPot.get(2)) / infoCount;
	}

}
