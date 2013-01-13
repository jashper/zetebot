package tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapGenerator {
	private static Map<String, Double> APWMap;
	private static Map<String, String> DiscardMap;
	private static OddsGenerator gen;
	
	static int[] ints = {1, 2, 3, 4, 5, 6, 7, 8,  9, 10, 11, 12, 13,
		14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
		27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
		40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52};
	
	private static ArrayList<Integer> intlist;
	
	
	public static void main(String[] args) {
		APWMap = new HashMap<String, Double>();
		DiscardMap = new HashMap<String, String>();
		gen = new OddsGenerator();
		
		intlist = new ArrayList<Integer>();
		for (int card : ints) {
			intlist.add(card);
		}
		
		try {
			generateMaps();
		} catch (IOException e) {e.printStackTrace();}
				
	}
	
	private static void generateMaps() throws IOException {
		generatePreFlop();
		generateDiscard();
		
		FileOutputStream fos = new FileOutputStream("APWMap.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(APWMap);
        fos.close();
        oos.close();
        
        fos = new FileOutputStream("DiscardMap.ser");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(DiscardMap);
        fos.close();
        oos.close();
	}
	
	private static void generatePreFlop() { //TODO: implement after data scrape finishes
		
	}
	
	private static void generateDiscard() { //TODO: map with discard function
		
	}
	
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
