package player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Simple example pokerbot, written in Java.
 * 
 * This is an example of a bare bones, pokerbot. It only sets up the socket
 * necessary to connect with the engine and then always returns the same action.
 * It is meant as an example of how a pokerbot should communicate with the
 * engine.
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
				System.out.println(input);
				outStream.println(this.handleInput(input));
				String word = input.split(" ")[0];
				if ("GETACTION".compareToIgnoreCase(word) == 0) {
					// When appropriate, reply to the engine with a legal
					// action.
					// The engine will ignore all spurious packets you send.
					// The engine will also check/fold for you if you return an
					// illegal action.
					outStream.println("CHECK");
				} else if ("REQUESTKEYVALUES".compareToIgnoreCase(word) == 0) {
					// At the end, engine will allow bot to send key/value pairs to store.
					// FINISH indicates no more to store.
					outStream.println("FINISH");
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
		String[] toks = input.split(" ");
		if(toks[0] == "NEWGAME"){
			thisMatch = new Match(input);
		}else if(toks[0] == "KEYVALUE"){
			thisMatch.keyVals.put(toks[1], toks[2]);
		}else if(toks[0] == "REQUESTKEYVALUES"){
			
		}else if(toks[0] == "NEWHAND"){
			
		}else if(toks[0] == "GETACTION"){
			
		}else if(toks[0] == "HANDOVER"){
			
		}else{
			//Unsupported input.
		}
		return "";
	}	
}