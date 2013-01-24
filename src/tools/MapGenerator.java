package tools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapGenerator {
	private static Map<String, Double> APWMap;
	private static OddsGenerator gen;
	
	static int[] ints = {1, 2, 3, 4, 5, 6, 7, 8,  9, 10, 11, 12, 13,
		14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
		27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
		40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52};
	
	private static ArrayList<Integer> intlist;
	
	
	public static void main(String[] args) throws IOException {
		APWMap = new HashMap<String, Double>();
		gen = new OddsGenerator();
		
		intlist = new ArrayList<Integer>();
		for (int card : ints) {
			intlist.add(card);
		}
		
		//generateMaps();
				
	}
	
	private static void generateMaps() throws IOException {
		generatePreFlop();
		
		FileOutputStream fos = new FileOutputStream("APWMap.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(APWMap);
        fos.close();
        oos.close();
	}
	
	private static void generatePreFlop() throws IOException {
		HashMap<String, Double> probMap = parsePokerStove();
		
		for (int a = 0; a < 52; a++) {
			int holeA = ints[a];
			System.out.println("A = "+a);
			for (int b = a+1; b < 52; b++) {
				int holeB = ints[b];
				System.out.println("B = "+b);
				for (int c = b+1; c < 52; c++) {
					int holeC = ints[c];
	
					int[] ABints = {holeA, holeB};
					int[] BCints = {holeB, holeC};
					int[] ACints = {holeA, holeC};
					
					Arrays.sort(ABints);
					Arrays.sort(BCints);
					Arrays.sort(ACints);
					
					String ABKey = String.valueOf(ABints[0])+" "+String.valueOf(ABints[1])+" "+String.valueOf(holeC);
					String BCKey = String.valueOf(BCints[0])+" "+String.valueOf(BCints[1])+" "+String.valueOf(holeA);
					String ACKey = String.valueOf(ACints[0])+" "+String.valueOf(ACints[1])+" "+String.valueOf(holeB);
					
					double termAB = probMap.get(ABKey);
					double termBC = probMap.get(BCKey);
					double termAC = probMap.get(ACKey);
					
					double[] odds = gen.getDiscardOdds(holeA, holeB, holeC);
					
					double value = termAB*odds[2] + termBC*odds[0] + termAC*odds[1];
					
					int[] keyInts = {holeA, holeB, holeC};
					Arrays.sort(keyInts);
					APWMap.put(String.valueOf(keyInts[0])+" "+String.valueOf(keyInts[1])+" "+String.valueOf(keyInts[2]), value);
						
				}
			}
		}
	}
	
	private static HashMap<String, Double> parsePokerStove() throws IOException {
		HashMap<String, Double> results = new HashMap<String, Double>();
		
		BufferedReader br = new BufferedReader(new FileReader("pokerstove.txt"));
		String line = br.readLine();
		ArrayList<String> input = new ArrayList<String>();
        while (line != null) {
        	input.add(line);
            line = br.readLine();
        }
        br.close();
        
        String discard = null;
        for (int i = 0; i < input.size(); i++) {
        	String l = input.get(i);
        	if (l.contains("Dead:")) {
        		discard = l.substring(7);
        		i = i+2;
        	}
        	if (l.contains("Hand 0:")) {
        		int length = l.length();
        		String holeCardA = l.substring(length-4, length-2);
        		String holeCardB = l.substring(length-6, length-4);
        		double odds = Double.valueOf(l.substring(9, 15));
        		
        		int[] holeCards = {gen.stringToInt(holeCardA), gen.stringToInt(holeCardB)};
        		Arrays.sort(holeCards);
        		int discardInt = gen.stringToInt(discard);
        		String key = String.valueOf(holeCards[0]) + " " + String.valueOf(holeCards[1]) + " " + String.valueOf(discardInt);
        		results.put(key, odds);
        	}
        }
        
        //sanity check
        int count = 0;
		for (int a = 0; a < 52; a++) {
			int holeA = intlist.get(a);
			for (int b = a+1; b < 52; b++) {
				int holeB = intlist.get(b);
				for (int c = 0; c < 50; c++) {
					@SuppressWarnings("unchecked")
					ArrayList<Integer> intlistB = (ArrayList<Integer>) intlist.clone();
					intlistB.remove(a);
					intlistB.remove(b-1);
					
					int disc = intlistB.get(c);
					int[] holes = {holeA, holeB};
					Arrays.sort(holes);
					
					String key = String.valueOf(holes[0])+" "+String.valueOf(holes[1])+" "+String.valueOf(disc);
					
					if (!results.containsKey(key)) {
						count++;
					}
				}
			}
		}
		
		System.out.println("Total Missing: "+count);
		return results;
	}
	
	// Takes way too long
	/*private static void generateDiscard() {
		long work = 0;
		for (int a = 0; a < 52; a++) {
			int holeCardA = intlist.get(a);
			for (int b = a+1; b < 52; b++) {
				System.out.println((work/1326.0)+"% of work done");
				int holeCardB = intlist.get(b);
				for (int c = b+1; c < 52; c++) {
					int holeCardC = intlist.get(c);
					
					ArrayList<Integer> intlistB = (ArrayList<Integer>) intlist.clone();
					intlistB.remove(a);
					intlistB.remove(b-1);
					intlistB.remove(c-2);
					
					for (int d = 0; d < 49; d++) {
						int boardCardA = intlistB.get(d);
						for (int e = d+1; e < 49; e++) {
							int boardCardB = intlistB.get(e);
							for (int f = e+1; f < 49; f++) {
								int boardCardC = intlistB.get(f);
								
								boolean[] toDiscard = gen.findDiscard(holeCardA, holeCardB, holeCardC, boardCardA, boardCardB, boardCardC);
								
								
							}
						}
					}
				}
				work++;
			}
		}
	}*/
	
	// Takes way too long
	/*private static void generatePostFlop() {
		long work = 0;
		for (int a = 0; a < 52; a++) {
			int holeCardA = intlist.get(a);
			for (int b = a+1; b < 52; b++) {
				int holeCardB = intlist.get(b);
				
				ArrayList<Integer> intlistB = (ArrayList<Integer>) intlist.clone();
				intlistB.remove(a);
				intlistB.remove(b-1);
				
				for (int c = 0; c < 50; c++) {
					int discardCard = intlistB.get(c);
					
					ArrayList<Integer> intlistC = (ArrayList<Integer>) intlistB.clone();
					intlistC.remove(c);
					
					for (int d = 0; d < 49; d++) {
						System.out.println((work/3248700.0)+"% done");
						int boardCardA = intlistC.get(d);
						for (int e = d+1; e < 49; e++) {
							int boardCardB = intlistC.get(e);
							for (int f = e+1; f < 49; f++) {
								int boardCardC = intlistC.get(f);
								
								gen.getPostFlopOdds(holeCardA, holeCardB, discardCard, boardCardA, boardCardB, boardCardC);
							}
						}
						work++;
					}
				}
			}
		}
	}*/
}
