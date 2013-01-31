package tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OddsGenerator {
	
	private static int[] ints = {1, 2, 3, 4, 5, 6, 7, 8,  9, 10, 11, 12, 13,
						14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
						27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
						40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52};
	
	
	private TwoPlusTwo rankEngine; // helper class for determining hand rank/strength

	public OddsGenerator() {
		rankEngine = new TwoPlusTwo();
		
		//int[] cardsB = {stringToInt("Ks"), stringToInt("6c"), stringToInt("3d"), stringToInt("9s"), stringToInt("4c"), stringToInt("Ts"), stringToInt("8c")};
		//System.out.println(rankEngine.lookupHand7(cardsB, 0) >> 12);
	}
	
	// helper function for generating card combinations -> removes cards from a list of possible cards
	public Integer[] getNewCards(int[] toRemove, int[] original) {
		ArrayList<Integer> newCards = new ArrayList<Integer>();
		for (int x = 0; x < original.length; x++) {
			boolean add = true;
			for (int y = 0; y < toRemove.length; y++) {
				if (original[x] == toRemove[y]) {
					add = false;
					break;
				}
			}
			if (add) {
				newCards.add(original[x]);
			}
		}
		return (Integer[]) newCards.toArray(new Integer[newCards.size()]);
	}
	
	// helper function for converting card name to integer for the rankEngine (ie: As -> 52 or Ac -> 49)
	public int stringToInt(String card) {
		String lower = card.toLowerCase();
		String type = lower.substring(0, 1);
		String suit = lower.substring(1);
		
		int base = 0;
		if (type.equals("2")) {
			base = 1;
		} else if (type.equals("3")) {
			base = 5;
		} else if (type.equals("4")) {
			base = 9;
		} else if (type.equals("5")) {
			base = 13;
		} else if (type.equals("6")) {
			base = 17;
		} else if (type.equals("7")) {
			base = 21;
		} else if (type.equals("8")) {
			base = 25;
		} else if (type.equals("9")) {
			base = 29;
		} else if (type.equals("t")) {
			base = 33;
		} else if (type.equals("j")) {
			base = 37;
		} else if (type.equals("q")) {
			base = 41;
		} else if (type.equals("k")) {
			base = 45;
		} else if (type.equals("a")) {
			base = 49;
		}
		
		if (suit.equals("d")) {
			base += 1;
		} else if (suit.equals("h")) {
			base += 2;
		} else if (suit.equals("s")) {
			base += 3;
		}
		
		return base;
	}
	
	public String intToString(int card) {
		String[] cards = {"2c", "2d", "2h", "2s", "3c", "3d", "3h", "3s", "4c", "4d", "4h", "4s", "5c", "5d", "5h", "5s",
						"6c", "6d", "6h", "6s", "7c", "7d", "7h", "7s", "8c", "8d", "8h", "8s", "9c", "9d", "9h", "9s",
						"Tc", "Td", "Th", "Ts", "Jc", "Jd", "Jh", "Js", "Qc", "Qd", "Qh", "Qs", "Kc", "Kd", "Kh", "Ks",
						"Ac", "Ad", "Ah", "As",};
		
		return cards[card-1];
	}

	// given 3 hole cards and 3 flop cards, returns a boolean[] that indicates which hole card(s)
	// 		should be discarded to maximize your chance of winning; ties/multiple-trues are possible
	public boolean[] findDiscard(int holeCardA, int holeCardB, int holeCardC, int boardCardA, int boardCardB, int boardCardC) {
		long ABRank = 0;
		long BCRank = 0;
		long ACRank = 0;
		
		int[] cardsToRemove = {holeCardA, holeCardB, holeCardC, boardCardA, boardCardB, boardCardC};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		
		for (int x = 0; x < 46; x++) {
			int boardCardD = newCards[x];
			for (int y = x+1; y < 46; y++) {
				int boardCardE = newCards[y];
				
				int[] ABHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
				int[] BCHand = {holeCardB, holeCardC, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
				int[] ACHand = {holeCardA, holeCardC, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
				
				ABRank += rankEngine.lookupHand7(ABHand, 0);
				BCRank += rankEngine.lookupHand7(BCHand, 0);
				ACRank += rankEngine.lookupHand7(ACHand, 0);
			}
		}
		
		if (ABRank < 0 || BCRank < 0 || ACRank < 0) {
			System.out.println("!!!Negative Value - Overflow!!!"); //sanity check
		}
		
		long maxValue = ABRank;
		if (BCRank > maxValue) {
			maxValue = BCRank;
		}
		if (ACRank > maxValue) {
			maxValue = ACRank;
		}
		
		boolean[] toReturn = {false, false, false};
		if (maxValue == ABRank) {
			toReturn[2] = true;
		}
		if (maxValue == BCRank) {
			toReturn[0] = true;
		}
		if (maxValue == ACRank) {
			toReturn[1] = true;
		}
		
		return toReturn;
	}
	
	//given 3 hole cards, returns the chances for each hole card that you will end up discarding that card,
	//		for all combinations of the flop to come
	public double[] getDiscardOdds(int holeCardA, int holeCardB, int holeCardC) {
		long aCount = 0;
		long bCount = 0;
		long cCount = 0;
		
		boolean ABtie = false;
		boolean BCtie = false;
		boolean ACtie = false;
		String ABCtie = "a";
		
		int[] cardsToRemove = {holeCardA, holeCardB, holeCardC};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		
		for (int a = 0; a < 49; a++) {
			int boardCardA = newCards[a];
			for (int b = a+1; b < 49; b++) {
				int boardCardB = newCards[b];
				for (int c = b+1; c < 49; c++) {
					int boardCardC = newCards[c];
					
					boolean[] toDiscard = findDiscard(holeCardA, holeCardB, holeCardC, boardCardA, boardCardB, boardCardC);
					
					if (toDiscard[0] && !toDiscard[1] && !toDiscard[2]) { // just a
						aCount++;
						continue;
					}
					
					if (!toDiscard[0] && toDiscard[1] && !toDiscard[2]) { // just b
						bCount++;
						continue;
					}
					
					if (!toDiscard[0] && !toDiscard[1] && toDiscard[2]) { // just c
						cCount++;
						continue;
					}
					
					if (toDiscard[0] && toDiscard[1] && !toDiscard[2]) { // ABtie
						if (ABtie) {
							aCount++;
						} else {
							bCount++;
						}
						ABtie = !ABtie;
						continue;
					}
					
					if (!toDiscard[0] && toDiscard[1] && toDiscard[2]) { // BCtie
						if (BCtie) {
							bCount++;
						} else {
							cCount++;
						}
						BCtie = !BCtie;
						continue;
					}
					
					if (toDiscard[0] && !toDiscard[1] && toDiscard[2]) { // ACtie
						if (ACtie) {
							aCount++;
						} else {
							cCount++;
						}
						ACtie = !ACtie;
						continue;
					}
					
					if (toDiscard[0] && toDiscard[1] && toDiscard[2]) { // ABCtie
						if (ABCtie.equals("a")) {
							aCount++;
							ABCtie = "b";
						} else if (ABCtie.equals("b")) {
							bCount++;
							ABCtie = "c";
						} else if (ABCtie.equals("c")) {
							cCount++;
							ABCtie = "a";
						}
					}
				}
			}
		}
		
		double[] toReturn = {aCount/18424.0, bCount/18424.0, cCount/18424.0};
		
		return toReturn;
		
	}
	
	// given two hole cards that you'd like to keep and given one card you'd like to discard, returns the
	// 		the chances of winning preFlop with that pair of hole cards, for all possible board and enemy card 
	//		combinations to come (minus combinations that include our discardCard)
	private double getPreFlopDiscardOdds(int holeCardA, int holeCardB, int discardCard) throws IOException {
		long wins = 0;
		long gameCount = 0;
		
		int[] cardsToRemove = {holeCardA, holeCardB, discardCard};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		int[] newCardsArray = new int[49];
		for (int z = 0; z < 49; z++) {
			newCardsArray[z] = newCards[z];
		}
		
		for (int a = 0; a < 49; a++) {
			int enemyCardA = newCards[a];
			for (int b = a+1; b < 49; b++) {
				int enemyCardB = newCards[b];
				int[] newCardsToRemove = {enemyCardA, enemyCardB};
				Integer[] finalNewCards = getNewCards(newCardsToRemove, newCardsArray);
				
				for (int c = 0; c < 47; c++) {
					int boardCardA = finalNewCards[c];
					for (int d = c+1; d < 47; d++) {
						int boardCardB = finalNewCards[d];
						for (int e = d+1; e < 47; e++) {
							int boardCardC = finalNewCards[e];
							for (int f = e+1; f < 47; f++) {
								int boardCardD = finalNewCards[f];
								for (int g = f+1; g < 47; g++) {
									int boardCardE = finalNewCards[g];
									
									int[] myHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
									int[] enemyHand = {enemyCardA, enemyCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
									
									int myRank = rankEngine.lookupHand7(myHand, 0);
									int enemyRank = rankEngine.lookupHand7(enemyHand, 0);
									
									if (myRank >= enemyRank) {
										wins++;
									}
									
									gameCount++;
								}
							}
						}
					}
				}
			}
		}
		
		return ((wins*1.0)/gameCount);
	}
	
	// given three hole cards, returns the chances of winning preFlop
	private double getPreFlopOdds(int holeCardA, int holeCardB, int holeCardC) throws IOException {
		double termAB = getPreFlopDiscardOdds(holeCardA, holeCardB, holeCardC);
		double termBC = getPreFlopDiscardOdds(holeCardB, holeCardC, holeCardA);
		double termAC = getPreFlopDiscardOdds(holeCardA, holeCardC, holeCardB);
		
		double[] odds = getDiscardOdds(holeCardA, holeCardB, holeCardC);
		
		return (termAB*odds[2] + termBC*odds[0] + termAC*odds[1]);
	}
	
	public double getPreFlopOddsEnemy(int holeCardA, int holeCardB, int boardCardA, int boardCardB, int boardCardC, Map<String, Double> APWMap) {
		int[] cardsToRemove = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		ArrayList<Integer> possibleCards = new ArrayList<Integer>();
		ArrayList<Double> possibleOdds = new ArrayList<Double>();
		
		for (int a = 0; a < 47; a++) {
			int discardCard = newCards[a];
			boolean[] toDiscard = findDiscard(holeCardA, holeCardB, discardCard, boardCardA, boardCardB, boardCardC);
			if (toDiscard[2]) {
				possibleCards.add(discardCard);
			}
		}
		

		for (Integer holeCardC : possibleCards) {
			int[] cards = {holeCardA, holeCardB, holeCardC};
			Arrays.sort(cards);
			String lookupStr = cards[0] + " " + cards[1] + " " + cards[2];
			if (APWMap.containsKey(lookupStr)) {
				double odds = APWMap.get(lookupStr) / 100.0;
				possibleOdds.add(odds);
			}
		}
		
		double oddsToReturn = 0.0;
		int count = 0;
		for (Double odds : possibleOdds) {
			count++;
			oddsToReturn += odds;
		}
		if (count != 0) {
			oddsToReturn /= count;
		}
		return oddsToReturn;
	}
	
	public double getFlopOdds(int holeCardA, int holeCardB, int discardCard, int boardCardA, int boardCardB, int boardCardC) {
		long wins = 0;
		long gameCount = 0;
		
		int[] cardsToRemove = {holeCardA, holeCardB, discardCard, boardCardA, boardCardB, boardCardC};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		int[] newCardsArray = new int[46];
		for (int z = 0; z < 46; z++) {
			newCardsArray[z] = newCards[z];
		}
		
		for (int a = 0; a < 46; a++) {
			int enemyCardA = newCards[a];
			for (int b = a+1; b < 46; b++) {
				int enemyCardB = newCards[b];
				int[] newCardsToRemove = {enemyCardA, enemyCardB};
				Integer[] finalNewCards = getNewCards(newCardsToRemove, newCardsArray);
				
				for (int c = 0; c < 44; c++) {
					int boardCardD = finalNewCards[c];
					for (int d = c+1; d < 44; d++) {
						int boardCardE = finalNewCards[d];
						
						int[] myHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
						int[] enemyHand = {enemyCardA, enemyCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
						
						int myRank = rankEngine.lookupHand7(myHand, 0);
						int enemyRank = rankEngine.lookupHand7(enemyHand, 0);
						
						if (myRank >= enemyRank) {
							wins++;
						}
						
						gameCount++;
					}
				}
			}
		}
		
		return ((wins*1.0)/gameCount);
	}
	
	public double[] getFlopOddsEnemy(int holeCardA, int holeCardB, int boardCardA, int boardCardB, int boardCardC) {
		long[] wins = new long[9];
		long gameCount = 0;
		double[] avrgWins = new double[9];
		
		int[] cardsToRemove = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		int[] newCardsArray = new int[47];
		for (int z = 0; z < 47; z++) {
			newCardsArray[z] = newCards[z];
		}
		
		for (int a = 0; a < 47; a++) {
			int enemyCardA = newCards[a];
			for (int b = a+1; b < 47; b++) {
				int enemyCardB = newCards[b];
				int[] newCardsToRemove = {enemyCardA, enemyCardB};
				Integer[] finalNewCards = getNewCards(newCardsToRemove, newCardsArray);
				
				for (int c = 0; c < 45; c++) {
					int boardCardD = finalNewCards[c];
					for (int d = c+1; d < 45; d++) {
						int boardCardE = finalNewCards[d];
						
						int[] myHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
						int[] enemyHand = {enemyCardA, enemyCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
						
						int myRank = rankEngine.lookupHand7(myHand, 0);
						int enemyRank = rankEngine.lookupHand7(enemyHand, 0);
						
						int catIdx = (myRank >> 12) - 1;
						
						if (myRank >= enemyRank) {
							wins[catIdx]++;
						}
						
						gameCount++;
					}
				}
			}
		}
		
		for (int i = 0; i < 9; i++) {
			avrgWins[i] = (wins[i] * 1.0 / gameCount);
		}
		
		return avrgWins;
	}
	
	public double getTurnOdds(int holeCardA, int holeCardB, int discardCard, int boardCardA, int boardCardB, int boardCardC, int boardCardD) {
		long wins = 0;
		long gameCount = 0;
		
		int[] cardsToRemove = {holeCardA, holeCardB, discardCard, boardCardA, boardCardB, boardCardC, boardCardD};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		int[] newCardsArray = new int[45];
		for (int z = 0; z < 45; z++) {
			newCardsArray[z] = newCards[z];
		}
		
		for (int a = 0; a < 45; a++) {
			int enemyCardA = newCards[a];
			for (int b = a+1; b < 45; b++) {
				int enemyCardB = newCards[b];
				int[] newCardsToRemove = {enemyCardA, enemyCardB};
				Integer[] finalNewCards = getNewCards(newCardsToRemove, newCardsArray);
				
				for (int c = 0; c < 43; c++) {
					int boardCardE = finalNewCards[c];
					
					int[] myHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
					int[] enemyHand = {enemyCardA, enemyCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
					
					int myRank = rankEngine.lookupHand7(myHand, 0);
					int enemyRank = rankEngine.lookupHand7(enemyHand, 0);
					
					if (myRank >= enemyRank) {
						wins++;
					}
					
					gameCount++;
				}
			}
		}
		
		return ((wins*1.0)/gameCount);
	}
	
	public double[] getTurnOddsEnemy(int holeCardA, int holeCardB, int boardCardA, int boardCardB, int boardCardC, int boardCardD) {
		long[] wins = new long[9];
		long gameCount = 0;
		double[] avrgWins = new double[9];
		
		int[] cardsToRemove = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		int[] newCardsArray = new int[46];
		for (int z = 0; z < 46; z++) {
			newCardsArray[z] = newCards[z];
		}
		
		for (int a = 0; a < 46; a++) {
			int enemyCardA = newCards[a];
			for (int b = a+1; b < 46; b++) {
				int enemyCardB = newCards[b];
				int[] newCardsToRemove = {enemyCardA, enemyCardB};
				Integer[] finalNewCards = getNewCards(newCardsToRemove, newCardsArray);
				
				for (int c = 0; c < 44; c++) {
					int boardCardE = finalNewCards[c];
					
					int[] myHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
					int[] enemyHand = {enemyCardA, enemyCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
					
					int myRank = rankEngine.lookupHand7(myHand, 0);
					int enemyRank = rankEngine.lookupHand7(enemyHand, 0);
					
					int catIdx = (myRank >> 12) - 1;
					
					if (myRank >= enemyRank) {
						wins[catIdx]++;
					}
					
					gameCount++;
				}
			}
		}
		
		for (int i = 0; i < 9; i++) {
			avrgWins[i] = (wins[i] * 1.0 / gameCount);
		}
		
		return avrgWins;
	}
	
	public double getRiverOdds(int holeCardA, int holeCardB, int discardCard, int boardCardA, int boardCardB, int boardCardC, int boardCardD, int boardCardE) {
		long wins = 0;
		long gameCount = 0;
		
		int[] cardsToRemove = {holeCardA, holeCardB, discardCard, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		
		for (int a = 0; a < 44; a++) {
			int enemyCardA = newCards[a];
			for (int b = a+1; b < 44; b++) {
				int enemyCardB = newCards[b];
				
				int[] myHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
				int[] enemyHand = {enemyCardA, enemyCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
				
				int myRank = rankEngine.lookupHand7(myHand, 0);
				int enemyRank = rankEngine.lookupHand7(enemyHand, 0);
				
				if (myRank >= enemyRank) {
					wins++;
				}
				
				gameCount++;
			}
		}
		
		return ((wins*1.0)/gameCount);
	}
	
	public double[] getRiverOddsEnemy(int holeCardA, int holeCardB, int boardCardA, int boardCardB, int boardCardC, int boardCardD, int boardCardE) {
		long[] wins = new long[9];
		long gameCount = 0;
		double[] avrgWins = new double[9];
		
		int[] cardsToRemove = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
		Integer[] newCards = getNewCards(cardsToRemove, ints);
		
		for (int a = 0; a < 45; a++) {
			int enemyCardA = newCards[a];
			for (int b = a+1; b < 45; b++) {
				int enemyCardB = newCards[b];
				
				int[] myHand = {holeCardA, holeCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
				int[] enemyHand = {enemyCardA, enemyCardB, boardCardA, boardCardB, boardCardC, boardCardD, boardCardE};
				
				int myRank = rankEngine.lookupHand7(myHand, 0);
				int enemyRank = rankEngine.lookupHand7(enemyHand, 0);
				
				int catIdx = (myRank >> 12) - 1;
				
				if (myRank >= enemyRank) {
					wins[catIdx]++;
				}
				
				gameCount++;
			}
		}
		
		for (int i = 0; i < 9; i++) {
			avrgWins[i] = (wins[i] * 1.0 / gameCount);
		}
		
		return avrgWins;
	}
	
}