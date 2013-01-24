package player;

import java.util.ArrayList;
import java.lang.Math;
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
	
	//Bluffing Data, each list corresponds to an axis on the graph.
	private ArrayList<Integer> time_bg;
	private ArrayList<Double> degree_bg;
	private ArrayList<Double> amt_bg;
	
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
	
	// TODO: implement
	private double getAPW(String[] tableCards, String[] holeCards){
		return 0.0;
	}
	private double degreeBluffing(String[] tableCards, String[] holeCards, int pot, int wager){
		double APW = getAPW(tableCards,holeCards);
		return new Double((wager-pot*APW)/match.stackSize);
	}
	
	/**
	 * Returns a double that is inversely proportional to the similarity of two scatter plots
	 * 
	 * Some of our opponents won't vary their strategies based on what part of the game they're in
	 * this is part of a pair of functions that determines if we need to separate the graph based on time.
	 * 
	 * @param xA
	 * @param yA
	 * @param xB
	 * @param yB
	 * @return
	 */
	private double compareScatterPlots(ArrayList<Double> xA, ArrayList<Double> yA, ArrayList<Double> xB, ArrayList<Double> yB){
		if(xA.size() != yA.size() || xB.size() != yB.size()) return -1.0; //Lists do not specify a valid graph
		ArrayList<Double> xS = xA;
		ArrayList<Double> yS = yA;
		ArrayList<Double> xG = xB;
		ArrayList<Double> yG = yB;
		if(xA.size() > xB.size()){
			xS = xB;
			yS = yB;
			xG = xA;
			yG = yA;
		}
		double totalDist = 0.0;
		double minDist;
		for(int iS=0;iS<xS.size();iS++){
			minDist = -1.0;
			for(int iG=0;iG<xG.size();iG++){
				double dist = (xS.get(iS)-xG.get(iG))*(xS.get(iS)-xG.get(iG)) +
								(yS.get(iS)-yG.get(iG))*(yS.get(iS)-yG.get(iG));
				if(Math.abs(minDist+1) < .8 || dist < minDist){
					minDist = dist;}
			totalDist += minDist;
			}
		}
		return new Double(totalDist/xS.size());
	}
	/**
	 * Compares every pair of plots to determine what graph we should use.
	 * 	
	 * Some of our opponents won't vary their strategies based on what part of the game they're in
	 * this is part of a pair of functions that determines if we need to separate the graph based on time.
	 * 
	 * @return
	 */
	private boolean useTimePlot(){
		double totalDiff = 0.0;
		double THRESHOLD = -1;
		ArrayList<Double> xA = new ArrayList<Double>(0);
		ArrayList<Double> yA = new ArrayList<Double>(0);
		ArrayList<Double> xB = new ArrayList<Double>(0);
		ArrayList<Double> yB = new ArrayList<Double>(0);
		for( int t1=0; t1<=2; t1++){
			for(int t2=t1+1;t2<=3;t2++){
				for(int i=0;i<time_bg.size();i++){
					if(t1==time_bg.get(i)){
						xA.add(degree_bg.get(i));
						yA.add(amt_bg.get(i));
					}
					else if(t2==time_bg.get(i)){
						xB.add(degree_bg.get(i));
						yB.add(amt_bg.get(i));
					}
				}
			}
			totalDiff += this.compareScatterPlots(xA, yA, xB, yB);
		}
		return totalDiff > THRESHOLD;
		
	}
	
	public double estimateDegreeBluffing(int time, double normalAmt){
		double totalWeight = 0.0;
		double degreeBluffing = 0.0;
		boolean useTime = this.useTimePlot();
		for(int i=0;i<time_bg.size();i++){
			if(time_bg.get(i)==time || !useTime){
				double dist = Math.abs(normalAmt-amt_bg.get(i));
				double weight = 1/(dist*dist);
				degreeBluffing += weight*degree_bg.get(i);
				totalWeight += weight;
			}
		}
		return degreeBluffing/totalWeight;
	}
	public void updateBluffGraph(String[] holeCards){
		String[] tableCards = (String[]) match.tableCards.toArray();
		//PreFlop
		String[] pf_tc = new String[0]; 
		time_bg.add(0);
		degree_bg.add(degreeBluffing(pf_tc,holeCards,match.potAt[0],match.oppWagerAt[0]));
		amt_bg.add(new Double(match.oppWagerAt[0]/(match.potAt[0]+match.oppWagerAt[0])));
		//Flop
		String[] f_tc = new String[3]; 
		for(int i=0;i<3;i++){
			f_tc[i] = tableCards[i];
		}
		time_bg.add(1);
		degree_bg.add(degreeBluffing(f_tc,holeCards,match.potAt[1],match.oppWagerAt[1]));
		amt_bg.add(new Double(match.oppWagerAt[1]/(match.potAt[1]+match.oppWagerAt[1])));
		//Turn
		String[] t_tc = new String[4];
		for(int i=0;i<4;i++){
			t_tc[i] = tableCards[i];
		}
		time_bg.add(2);
		degree_bg.add(degreeBluffing(t_tc,holeCards,match.potAt[2],match.oppWagerAt[2]));
		amt_bg.add(new Double(match.oppWagerAt[2]/(match.potAt[2]+match.oppWagerAt[2])));
		//River
		String[] r_tc = new String[5]; 
		for(int i=0;i<5;i++){
			f_tc[i] = tableCards[i];
		}
		time_bg.add(3);
		degree_bg.add(degreeBluffing(r_tc,holeCards,match.potAt[3],match.oppWagerAt[3]));
		amt_bg.add(new Double(match.oppWagerAt[3]/(match.potAt[3]+match.oppWagerAt[3])));
	}
	

}
