package player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

import player.ExpectedReturnStrategy;
import player.Match;
import player.StrategyBrain;
import player.Opponent;
import tools.OddsGenerator;

/**
 * Class that handles the communication between our brain and game model and the server.
 * 
 *  Lets us separate a lot of messy syntax and I/O from our decision making apparatus.
 * @author DC
 *
 */
public class Player {
	// Decision Making Objects
	private Match thisMatch;
	private StrategyBrain myBrain;
	private Opponent opponent;
	// Communication Objects
	private final PrintWriter outStream;
	private final BufferedReader inStream;
	// Probability Related Objects
	private final OddsGenerator oddsGen;
	private Map<String, Double> APWMap;

	public Player(PrintWriter output, BufferedReader input) {
		this.outStream = output;
		this.inStream = input;
		this.oddsGen = new OddsGenerator();
		initMaps();
	}
	
	public void run() {
		String input;
		try {
			// Block until engine sends us a packet; read it into input.
			while ((input = inStream.readLine()) != null) {
				System.out.println(input);//Should remove before submitting
				String action = this.handleInput(input);
				if(!action.equals("NO_ACTION")){
					System.out.println(action);//Should remove before submitting
					outStream.println(action);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}

		System.out.println("Gameover, engine disconnected");
		
		// Once the server disconnects from us, close our streams and sockets.
		try {
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			System.out.println("Encountered problem shutting down connections");
			e.printStackTrace();
		}
	}
	/**
	 * Handles Input from the server. Makes calls to match, brain, and opponent classes as necessary.
	 * 
	 * @param input
	 * @return
	 */
	private String handleInput(String input){
		// Engine packet types are: 
		// 		NEWGAME yourName oppName stackSize bb numHands timeBank
		// 		KEYVALUE key value
		// 		REQUESTKEYVALUES bytesLeft
		// 		NEWHAND handId button holeCard1 holeCard2 holeCard3 yourBank oppBank timeBank 
		// 		Ex: NEWHAND 10 true Ah Ac Ad 0 0 20.000000
		// 		GETACTION potSize numBoardCards [boardCards] numLastActions [lastActions] numLegalActions [legalActions] timebank 
		// 		Ex: GETACTION 30 5 As Ks Qh Qd Qc 3 CHECK:two CHECK:one DEAL:RIVER 2 CHECK BET:2:198 19.997999999999998
		// 		HANDOVER yourBank oppBank numBoardCards [boardCards] numLastActions [lastActions] timeBank
		String[] toks = input.split(" ");
		if(toks[0].equals("NEWGAME")){
			thisMatch = new Match(input);
			opponent = new Opponent(toks[2], thisMatch, oddsGen, APWMap);
			myBrain = new ExpectedReturnStrategy(thisMatch, opponent);
		}
		else if(toks[0].equals("KEYVALUE")){
			thisMatch.keyVals.put(toks[1], toks[2]);
		}
		else if(toks[0].equals("REQUESTKEYVALUES")){
			System.out.println("PreFlop Datapoints:");
			for (String s : thisMatch.dataPointsPreFlop) {
				System.out.println(s);
			}
			System.out.println("Flop Datapoints:");
			for (String s : thisMatch.dataPointsFlop) {
				System.out.println(s);
			}
			System.out.println("Turn Datapoints:");
			for (String s : thisMatch.dataPointsTurn) {
				System.out.println(s);
			}
			System.out.println("River Datapoints:");
			for (String s : thisMatch.dataPointsRiver) {
				System.out.println(s);
			}
			return "FINISH";
		}
		else if(toks[0].equals("NEWHAND")){
			thisMatch.handId = new Integer(toks[1]);
			thisMatch.haveButton = toks[2].equals("true");
			thisMatch.holeCards.add(toks[3]);
			thisMatch.holeCards.add(toks[4]);
			thisMatch.holeCards.add(toks[5]);
			updateAPW();
		}
		else if(toks[0].equals("GETACTION")){
			thisMatch.pot = new Integer(toks[1]);
			
			int myTableCount = thisMatch.tableCards.size();
			int tableCount = Integer.valueOf(toks[2]);
			int actionIndex;
			if (tableCount != myTableCount) {
				switch (tableCount){
					case 3: thisMatch.potAt[1] = thisMatch.pot;
							break;
					case 4: thisMatch.potAt[1] = thisMatch.pot;
							break;
					case 5: thisMatch.potAt[1] = thisMatch.pot;
							break;
				}
				for (int i = 3+myTableCount; i < tableCount+3; i++) {
					thisMatch.tableCards.add(toks[i]);
				}
				actionIndex = 3 + tableCount;
				if (tableCount == 3) {
					findDiscard();
				}
				updateAPW();
			} else {actionIndex = 3 + tableCount;}
			
			thisMatch.lastActions.clear();
			int actionCount = Integer.valueOf(toks[actionIndex]);
			int legalIndex;
			if (actionCount > 0) {
				for (int i = actionIndex+1; i < actionCount+actionIndex+1; i++) {
					thisMatch.lastActions.add(toks[i]);
				}
				legalIndex = actionIndex + actionCount + 1;
			} else {legalIndex = actionIndex + 1;}
			
			int legalCount = Integer.valueOf(toks[legalIndex]);
			String[] legalActions = new String[legalCount];
			if (legalCount > 0) {
				int index = 0;
				for (int i = legalIndex+1; i < legalCount+legalIndex+1; i++) {
					legalActions[index] = toks[i];
					index++;
				}
			}
			
			boolean aggressReact = false;
			for (String a : thisMatch.lastActions) {
				if (a.contains("BET") || a.contains("RAISE")) {
					String[] aSplit = a.split(":");
					if (aSplit[2].equals(opponent.name)) {
						thisMatch.amtToCall = Integer.valueOf(aSplit[1]);
					}
					aggressReact = true;
				}
			}
			
			if (aggressReact) {
				double maxBetPercent = (thisMatch.amtToCall * 1.0) / (thisMatch.pot - thisMatch.amtToCall);
				switch (thisMatch.tableCards.size()) {
					case 0:
						thisMatch.preFlopMaxBetPercent = maxBetPercent;
					case 3:
						thisMatch.flopMaxBetPercent = maxBetPercent;
					case 4:
						thisMatch.turnMaxBetPercent = maxBetPercent;
					case 5:
						thisMatch.riverMaxBetPercent = maxBetPercent;
				}
			}
			
			
			return myBrain.getAction(legalActions); 
		}
		else if(toks[0].equals("HANDOVER")){
			if (input.contains("SHOW") && thisMatch.tableCards.size() >= 3) {
				int startIdx = input.indexOf(opponent.name, input.indexOf("SHOW"));
				startIdx -= 6;
				String[] holeCards = {input.substring(startIdx, startIdx+2), input.substring(startIdx+3, startIdx+5)};
				
				opponent.updateOpponentAPW(holeCards);
				
				//opponent.updateBluffGraph(holeCards);
			}
			thisMatch.handCleanup();
		}
		else{
			//Unsupported input.
		}
		return "NO_ACTION";
	}
	
	private void updateAPW(){
		int holeCount = thisMatch.holeCards.size();
		int tableCount = thisMatch.tableCards.size();
		
		int[] holeInts = new int[holeCount];
		for (int i = 0; i < holeCount ; i++) {
			holeInts[i] = oddsGen.stringToInt(thisMatch.holeCards.get(i));
		}
		
		int[] tableInts = new int[tableCount];
		for (int i = 0; i < tableCount; i++) {
			tableInts[i] = oddsGen.stringToInt(thisMatch.tableCards.get(i));
		}
		
		switch (tableCount) {
			case 0: // preFlop
				Arrays.sort(holeInts);
				String lookupStr = holeInts[0] + " " + holeInts[1] + " " + holeInts[2];
				if (APWMap.containsKey(lookupStr)) {
					thisMatch.playerAPW = APWMap.get(lookupStr);
				} else {
					thisMatch.playerAPW = 0.0;
				}
				thisMatch.playerAPW /= 100.0;
				return;
			case 3: // flop
				thisMatch.playerAPW = oddsGen.getFlopOdds(holeInts[0], holeInts[1], oddsGen.stringToInt(thisMatch.discard), 
												tableInts[0], tableInts[1], tableInts[2]);
				return;
			case 4: // turn
				thisMatch.playerAPW = oddsGen.getTurnOdds(holeInts[0], holeInts[1], oddsGen.stringToInt(thisMatch.discard), 
						tableInts[0], tableInts[1], tableInts[2], tableInts[3]);
				return;
			case 5: // river
				thisMatch.playerAPW = oddsGen.getRiverOdds(holeInts[0], holeInts[1], oddsGen.stringToInt(thisMatch.discard), 
						tableInts[0], tableInts[1], tableInts[2], tableInts[3], tableInts[4]);
				return;
		}	
	}
	
	private void findDiscard() {
		int[] holeCards = new int[3];
		int[] tableCards = new int[3];
		for (int i = 0; i < 3; i++) {
			holeCards[i] = oddsGen.stringToInt(thisMatch.holeCards.get(i));
			tableCards[i] = oddsGen.stringToInt(thisMatch.tableCards.get(i));
		}
		boolean[] odds = oddsGen.findDiscard(holeCards[0], holeCards[1], holeCards[2], 
											tableCards[0], tableCards[1], tableCards[2]);
		
		int firstTrue = 0;
		for (int i = 0; i < 3; i++) {
			if (odds[i]) {
				firstTrue = i;
				break;
			}
		}
		
		thisMatch.discard = thisMatch.holeCards.get(firstTrue);
		thisMatch.holeCards.remove(firstTrue);
	}
	
	@SuppressWarnings("unchecked")
	private void initMaps() {
		InputStream fis;
        ObjectInputStream ois;
        
        try {
			fis = ClassLoader.getSystemResource("data/APWMap.ser").openStream();
			ois = new ObjectInputStream(fis);
	        APWMap = (Map<String, Double>) ois.readObject();
	        fis.close();
	        ois.close();
		} catch (IOException e) {
			System.out.println("APWMap failed to load");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("APWMap corrupted");
			e.printStackTrace();
		}
	}
}

// LegalActions are:
// 		BET:minBet:maxBet
// 		CALL
// 		CHECK
// 		DISCARD
// 		FOLD
// 		RAISE:minRaise:maxRaise
// PerformedActions:
//		BET:amount[:actor]
//		CALL[:actor]
//		CHECK[:actor]
//		DEAL:STREET
//		DISCARD:card
//		FOLD[:actor]
//		POST:amount:actor
//		RAISE:amount[:actor]
//		REFUND:amount:actor
//		SHOW:card1:card2:actor
//		TIE:amount:actor
//		WIN:amount:actor