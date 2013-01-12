package player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import player.Brain;
import player.Match;

/**
 * Class that handles the communication between our brain and game model and the server.
 * 
 *  Lets us separate a lot of messy syntax and I/O from our decision making apparatus.
 * @author DC
 *
 */
public class Player {
	private Match thisMatch;
	private Brain myBrain;
	private final PrintWriter outStream;
	private final BufferedReader inStream;

	public Player(PrintWriter output, BufferedReader input) {
		this.outStream = output;
		this.inStream = input;
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
			System.out.println("Encounterd problem shutting down connections");
			e.printStackTrace();
		}
	}
	/**
	 * Handles Input from the server. Makes calls to match, brain, and opponent classes as necessary.
	 * TODO: finish getAction
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
		if(toks[0] == "NEWGAME"){
			thisMatch = new Match(input);
		}else if(toks[0] == "KEYVALUE"){
			thisMatch.keyVals.put(toks[1], toks[2]);
		}else if(toks[0] == "REQUESTKEYVALUES"){
			
		}else if(toks[0] == "NEWHAND"){
			thisMatch.handId = new Integer(toks[1]);
			thisMatch.haveButton = toks[2].equals("true");
			String[] arb = {toks[3],toks[4],toks[5]};
			thisMatch.holeCards = arb;
			thisMatch.addBankVals(new Integer(toks[6]), new Integer(toks[7]));
		}else if(toks[0] == "GETACTION"){
			thisMatch.pot = new Integer(toks[1]);
			ArrayList<String> boardCards = new ArrayList<String>(5);
			ArrayList<String> legalActions = new ArrayList<String>(5);
			return myBrain.getAction((String[]) legalActions.toArray()); 
		}else if(toks[0] == "HANDOVER"){
			
		}else{
			//Unsupported input.
		}
		return "NO_ACTION";
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