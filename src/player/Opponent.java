package player;

import java.util.ArrayList;
import java.util.Map;
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
	private final Map<String, Double> APWMap;
	
	public double preFlopAPW;
	public double flopAPW;
	public double turnAPW;
	public double riverAPW;
	public double[] flopAPWCat;
	public double[] turnAPWCat;
	public double[] riverAPWCat;
	
	public int preFlopInfoCount;
	public int flopInfoCount;
	public int turnInfoCount;
	public int riverInfoCount;
	
	public int aggresiveActions; 
	public int passiveActions;
	
	//Bluffing Data, each list corresponds to an axis on the graph.
	private ArrayList<Integer> time_bg;
	private ArrayList<Double> degree_bg;
	private ArrayList<Double> amt_bg;
	
	
	public Opponent(String _name, Match match, OddsGenerator oddsGen, Map<String, Double> APWMap){
		name = _name;
		this.match = match;
		this.oddsGen = oddsGen;
		this.APWMap = APWMap;
		
		preFlopAPW = 0;
		flopAPW = 0;
		turnAPW = 0;
		riverAPW = 0;
		
		flopAPWCat = new double[9];
		turnAPWCat = new double[9];
		riverAPWCat = new double[9];

		preFlopInfoCount = 0;
		flopInfoCount = 0;
		turnInfoCount = 0;
		riverInfoCount = 0;
		
		this.aggresiveActions = 0;
		this.passiveActions = 0;
	}
	
	public String opponentType(){
		double betRatio = 0;
		if (this.passiveActions == 0) {
			return "TIGHT_AGGRESSIVE";
		} else {betRatio = this.aggresiveActions/this.passiveActions;}
		
		double aveAPW = 0.0;
		int count = 0;
		
		if (preFlopAPW != 0) {
			aveAPW += preFlopAPW;
			count++;
		}
		if (flopAPW != 0) {
			aveAPW += flopAPW;
			count++;
		}
		if (turnAPW != 0) {
			aveAPW += turnAPW;
			count++;
		}
		if (riverAPW != 0) {
			aveAPW += riverAPW;
			count++;
		}
		
		if (count == 0) {
			return "TIGHT_AGGRESSIVE";
		}
		
		aveAPW /= count;
		
		if(betRatio<2){
			if(aveAPW >.8) return "TIGHT_PASSIVE";
			else if(aveAPW >.4) return "LOOSE_PASSIVE";
			else return "MANIAC_PASSIVE";
		}
		else{
			if(aveAPW >.8) return "TIGHT_AGGRESSIVE";
			else if(aveAPW >.4) return "LOOSE_AGGRESSIVE";
			else return "MANIAC_AGGRESSIVE";
		}
	}

	public void updateOpponentAPW(String[] holeCards) {
		int[] holeInts = new int[2];
		for (int i = 0; i < 2 ; i++) {
			holeInts[i] = oddsGen.stringToInt(holeCards[i]);
		}
		
		int tableSize = match.tableCards.size();
		int[] tableInts = new int[tableSize];
		for (int i = 0; i < tableSize; i++) {
			tableInts[i] = oddsGen.stringToInt(match.tableCards.get(i));
		}
		
		double newPreFlopAPW;
		double[] newFlopAPWCat = null;
		double[] newTurnAPWCat = null;
		double[] newRiverAPWCat = null;
		
		String dataPoint;
		if (tableSize >= 3) {
			flopInfoCount++;
			flopAPW = 0;
			newFlopAPWCat = oddsGen.getFlopOddsEnemy(holeInts[0], holeInts[1], tableInts[0], tableInts[1], tableInts[2]);
			if (match.flopMaxBetPercent > 0) {
				double newFlopAPW = 0;
				for (double num : newFlopAPWCat) {
					newFlopAPW += num;
				}
				dataPoint = match.flopMaxBetPercent + " " + newFlopAPW;
				match.dataPointsFlop.add(dataPoint);
			}
			
			if (tableSize >= 4) {
				turnInfoCount++;
				turnAPW = 0;
				newTurnAPWCat = oddsGen.getTurnOddsEnemy(holeInts[0], holeInts[1], tableInts[0], tableInts[1], tableInts[2], tableInts[3]);
				if (match.turnMaxBetPercent > 0) {
					double newTurnAPW = 0;
					for (double num : newTurnAPWCat) {
						newTurnAPW += num;
					}
					dataPoint = match.turnMaxBetPercent + " " + newTurnAPW;
					match.dataPointsTurn.add(dataPoint);
				}
				
				if (tableSize == 5) {
					riverInfoCount++;
					riverAPW = 0;
					newRiverAPWCat = oddsGen.getRiverOddsEnemy(holeInts[0], holeInts[1], tableInts[0], tableInts[1], tableInts[2], tableInts[3], tableInts[4]);
					if (match.riverMaxBetPercent > 0) {
						double newRiverAPW = 0;
						for (double num : newRiverAPWCat) {
							newRiverAPW += num;
						}
						dataPoint = match.riverMaxBetPercent + " " + newRiverAPW;
						match.dataPointsRiver.add(dataPoint);
					}
				}
			}
			
			
			newPreFlopAPW = oddsGen.getPreFlopOddsEnemy(holeInts[0], holeInts[1], tableInts[0], tableInts[1], tableInts[2], APWMap);
			if (newPreFlopAPW > 0) {
				preFlopInfoCount++;
				preFlopAPW = (1.0 / preFlopInfoCount)*newPreFlopAPW + ((preFlopInfoCount - 1.0) / preFlopInfoCount) * preFlopAPW;;
				if (match.preFlopMaxBetPercent > 0) {
					dataPoint = match.preFlopMaxBetPercent + " " + newPreFlopAPW;
					match.dataPointsPreFlop.add(dataPoint);
				}
			}
			
			for (int i = 0; i < 9; i++) {
				flopAPWCat[i] = (1.0 / flopInfoCount)*newFlopAPWCat[i] + ((flopInfoCount - 1.0) / flopInfoCount) * flopAPWCat[i];
				flopAPW += flopAPWCat[i];
				
				if (newTurnAPWCat != null) {
					turnAPWCat[i] = (1.0 / turnInfoCount)*newTurnAPWCat[i] + ((turnInfoCount - 1.0) / turnInfoCount) * turnAPWCat[i];
					turnAPW += turnAPWCat[i];
				}
				
				if (newRiverAPWCat != null) {
					riverAPWCat[i] = (1.0 / riverInfoCount)*newRiverAPWCat[i] + ((riverInfoCount - 1.0) / riverInfoCount) * riverAPWCat[i];
					riverAPW += riverAPWCat[i];
				}
			}
		}
		
	}

	private double degreeBluffing(String[] tableCards, int pot, int wager){
		double APW = 0.0;
		switch(tableCards.length){
			case 0: APW =  this.preFlopAPW;
					break;
			case 3: APW =  this.flopAPW;
					break;
			case 4: APW =  this.turnAPW;
					break;
			case 5: APW =  this.riverAPW;
					break;
			}
		return APW;
		//return new Double((wager-pot*APW)/match.stackSize);
	}
	
	/**
	 * Returns a double that is inversely proportional to the similarity of two scatter plots
	 * 
	 * Some of our opponents won't vary their strategies based on what part of the game they're in
	 * this is part of a pair of functions that determines if we need to separate the graph based on time.
	 * 
	 * TODO: modify this function so it doesn't run in n-squared. Should be able to compare a random set of points
	 * from the larger graph to the smaller graph. That's at least n.
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
		Double minDist;
		for(int iS=0;iS<xS.size();iS++){
			minDist = null;
			for(int iG=0;iG<xG.size();iG++){
				double dist = (xS.get(iS)-xG.get(iG))*(xS.get(iS)-xG.get(iG)) +
								(yS.get(iS)-yG.get(iG))*(yS.get(iS)-yG.get(iG));
				if(minDist.equals(null) || dist < minDist){
					minDist = dist;}
			totalDist += minDist;
			}
		}
		return new Double(totalDist/xS.size());
	}
	/**
	 * Like compare scatter plot but uses a subset of random points from the larger graph instead of comparing every point.
	 * 
	 * Should be much faster.
	 * 
	 * @param xA
	 * @param yA
	 * @param xB
	 * @param yB
	 * @return
	 */
	private double approxCompareScatterPlots(ArrayList<Double> xA, ArrayList<Double> yA, ArrayList<Double> xB, ArrayList<Double> yB){
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
		int numRandPts = 50;
		if(xG.size()<=numRandPts) return this.compareScatterPlots(xA, yA, xB, yB);
		double totalDist = 0.0;
		Double minDist;
		int[] indicesToTest = new int[numRandPts];
		for(int i=0;i<numRandPts;i++){
			indicesToTest[i] = (int) Math.floor(xG.size()*Math.random());
		}
		for(int ind: indicesToTest){
			minDist = null;
			for(int iS=0;iS<xS.size();iS++){
				double dist = (xS.get(iS)-xG.get(ind))*(xS.get(iS)-xG.get(ind)) +
								(yS.get(iS)-yG.get(ind))*(yS.get(iS)-yG.get(ind));
				if(minDist.equals(null) || dist < minDist){
					minDist = dist;}
			totalDist += minDist;
			}
		}
		return new Double(totalDist/numRandPts);
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
		double amtHeight = 0.4/2;
		double bluffHeight = 0.6/2;
		double THRESHOLD = amtHeight*amtHeight + bluffHeight*bluffHeight;
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
			totalDiff += this.approxCompareScatterPlots(xA, yA, xB, yB);
		}
		return totalDiff > THRESHOLD;
		
	}
	
	public double estimateOppAPW(int time, double normalAmt){
		int MIN_NUM_DATA_PTS = 20;
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
		if(!(!useTime && time_bg.size()>MIN_NUM_DATA_PTS || time_bg.size()>MIN_NUM_DATA_PTS*4)) return -1.0;
		return (degreeBluffing/totalWeight);
	}
	public void updateBluffGraph(String[] holeCards){
		String[] tableCards = (String[]) match.tableCards.toArray();
		//PreFlop
		String[] pf_tc = new String[0]; 
		time_bg.add(0);
		degree_bg.add(degreeBluffing(pf_tc,match.potAt[0],match.oppWagerAt[0]));
		amt_bg.add(new Double(match.oppWagerAt[0]/match.stackSize));
		//Flop
		String[] f_tc = new String[3]; 
		for(int i=0;i<3;i++){
			f_tc[i] = tableCards[i];
		}
		time_bg.add(1);
		degree_bg.add(degreeBluffing(f_tc,match.potAt[1],match.oppWagerAt[1]));
		amt_bg.add(new Double(match.oppWagerAt[1]/match.stackSize));
		//Turn
		String[] t_tc = new String[4];
		for(int i=0;i<4;i++){
			t_tc[i] = tableCards[i];
		}
		time_bg.add(2);
		degree_bg.add(degreeBluffing(t_tc,match.potAt[2],match.oppWagerAt[2]));
		amt_bg.add(new Double(match.oppWagerAt[2]/match.stackSize));
		//River
		String[] r_tc = new String[5]; 
		for(int i=0;i<5;i++){
			f_tc[i] = tableCards[i];
		}
		time_bg.add(3);
		degree_bg.add(degreeBluffing(r_tc,match.potAt[3],match.oppWagerAt[3]));
		amt_bg.add(new Double(match.oppWagerAt[3]/match.stackSize));
	}
	

}
