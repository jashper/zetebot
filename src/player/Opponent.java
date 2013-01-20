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
	
	private final OddsGenerator oddsGen;
	public double flopAPW;
	public double turnAPW;
	public double riverAPW;

	public int infoCount;
	
	
	public Opponent(String _name, Match match, OddsGenerator oddsGen){
		name = _name;
		this.match = match;
		this.oddsGen = oddsGen;
		
		flopAPW = 0.0;
		turnAPW = 0.0;
		riverAPW = 0.0;

		infoCount = 0;
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
		
		flopAPW = (1.0 / infoCount)*newFlopAPW + ((infoCount - 1.0) / infoCount) * flopAPW;
		turnAPW = (1.0 / infoCount)*newTurnAPW + ((infoCount - 1.0) / infoCount) * turnAPW;
		riverAPW = (1.0 / infoCount)*newRiverAPW + ((infoCount - 1.0) / infoCount) * riverAPW;
		
	}

}
