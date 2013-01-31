package player;

public class TypeBasedStrat extends StrategyBrain {

	public TypeBasedStrat(Match _match, Opponent opponent) {
		super(_match, opponent);
	}
	
	@Override
	public String bet(int minBet, int maxBet) {
		String oppType = opponent.opponentType();
		if(oppType.equals("TIGHT_PASSIVE")){
			switch(match.tableCards.size()){
			case 0: return "CHECK";
			default:
				if(match.playerAPW>.925) return "CHECK";
				else return "FOLD";
			}
		}
		else if(oppType.equals("LOOSE_PASSIVE")){
			if(match.playerAPW>.85){
				double weight = 2.0; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "BET:"+maxBet;
					}else{
						return "BET:"+((int) betAmt);
					}
				}else{
					return "CHECK";
				}
			}
			else{
				return "FOLD";
			}
		}
		else if(oppType.equals("MANIAC_PASSIVE")){
			if(match.playerAPW>.7){
				double weight = 1.5; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "BET:"+maxBet;
					}else{
						return "BET:"+((int) betAmt);
					}
				}else{
					return "CHECK";
				}
			}
			else{
				return "FOLD";
			}
		}
		else if(oppType.equals("TIGHT_AGGRESSIVE")){
			if(oppType.equals("TIGHT_PASSIVE")){
				switch(match.tableCards.size()){
				case 0: return "BET:"+3*match.bb;//Try to get them to fold 
				default:
					if(match.playerAPW>.925) return "CHECK";
					else return "FOLD";
				}
		}
		else if(oppType.equals("LOOSE_AGGRESSIVE")){
			if(match.playerAPW>.85){
				double weight = 1.5; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "BET:"+maxBet;
					}else{
						return "BET:"+((int) betAmt);
					}
				}else{
					return "CHECK";
				}
			}
			else{
				return "CHECK";
			}
		}
		else if(oppType.equals("MANIAC_AGGRESSIVE")){
			if(match.playerAPW>.7){
				double weight = 1.5; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "BET:"+maxBet;
					}else{
						return "BET:"+((int) betAmt);
					}
				}else{
					return "CHECK";
				}
			}
			else{
				return "CHECK";
			}
		}
		}
	}

	@Override
	public String raise(int minBet, int maxBet) {
		String oppType = opponent.opponentType();
		if(oppType.equals("TIGHT_PASSIVE")){
			switch(match.tableCards.size()){
			case 0: return "RAISE:"+3*match.bb;
			default:
				if(match.playerAPW>.925) return "CALL";
				else return "FOLD";
			}
		}
		else if(oppType.equals("LOOSE_PASSIVE")){
			if(match.playerAPW>.85){
				double weight = 2.0; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "RAISE:"+maxBet;
					}else{
						return "RAISE:"+((int) betAmt);
					}
				}else{
					return "CALL";
				}
			}
			else{
				return "FOLD";
			}
		}
		else if(oppType.equals("MANIAC_PASSIVE")){
			if(match.playerAPW>.7){
				double weight = 1.5; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "RAISE:"+maxBet;
					}else{
						return "RAISE:"+((int) betAmt);
					}
				}else{
					return "CALL";
				}
			}
			else{
				return "FOLD";
			}
		}
		else if(oppType.equals("TIGHT_AGGRESSIVE")){
			if(oppType.equals("TIGHT_PASSIVE")){
				switch(match.tableCards.size()){
				case 0: return "RAISE:"+3*match.bb;//Try to get them to fold 
				default:
					if(match.playerAPW>.925) return "CALL";
					else return "FOLD";
				}
		}
		else if(oppType.equals("LOOSE_AGGRESSIVE")){
			if(match.playerAPW>.85){
				double weight = 1.5; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "RAISE:"+maxBet;
					}else{
						return "RAISE:"+((int) betAmt);
					}
				}else{
					return "CALL";
				}
			}
			else{
				return "CALL";
			}
		}
		else if(oppType.equals("MANIAC_AGGRESSIVE")){
			if(match.playerAPW>.7){
				double weight = 1.5; //Play aggressively
				double betAmt = weight * match.playerAPW * match.pot;
				weight = 1.0;
				if(minBet <= betAmt ){
					if(maxBet < betAmt){
						return "RAISE:"+maxBet;
					}else{
						return "RAISE:"+((int) betAmt);
					}
				}else{
					return "CALL";
				}
			}
			else{
				return "CALL";
			}
		}
	}

}
