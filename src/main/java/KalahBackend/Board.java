package KalahBackend;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="kallah_game")
public class Board {
	
	private final static int DEFAULT_GAME_SIZE = 6;
	private final static int DEFAULT_SEEDS_NUMBER = 4;
	public static final String PLAYER1 = "PLAYER1";
	public static final String PLAYER2 = "PLAYER2";
	public static final String GAME_STATUS_FINISHED = "FINISHED";
	public static final String GAME_STATUS_RUNNING = "RUNNING";
	public static final String GAME_STATUS_INVALID = "INVALID";
	public static final String GAME_RESULT_DRAW = "DRAW";
	
	private String boardId;
	private int[] pits;
	private List<Integer> pitsList;
	private String currentTurn;
	private int largePit1Index;
	private int largePit2Index;
	private int gameSize;
	private int seedsNumber;
	private String gameStatus;
	private String winner;

	
	public Board() {
		this.boardId = RandomStringUtils.randomAlphanumeric(8);
		this.gameSize = DEFAULT_GAME_SIZE;
		this.seedsNumber = DEFAULT_SEEDS_NUMBER;
		this.currentTurn = PLAYER1;
		this.gameStatus = GAME_STATUS_RUNNING;
		initializeBoard(gameSize, seedsNumber);
	}
	
	private void initializeBoard(int gameSize, int seedsNumber) {
		// there's two large pits, one at index = gamesize and
		// other at index = gameSize * 2 + 1
		this.largePit1Index = gameSize;
		this.largePit2Index = (gameSize * 2) + 1;
		// total number of pits is twice the number of small pits
		// plus two large pits
		int pitNumber = (gameSize + 1) * 2;
		pits = new int[pitNumber];
		
		// puts the total number of seeds on each house,
		// but not in the large pits
		for(int i = 0; i < pitNumber; i++) {
			if(i == largePit1Index || i == largePit2Index) {
				pits[i] = 0;
			} else {
				pits[i] = seedsNumber;
			}
		}
	}
	
	public int processSowing(int houseIndex) {
		int seeds = this.pits[houseIndex];
		this.pits[houseIndex] = 0;
		int currentHouse = houseIndex + 1;
		for( ; seeds > 0 ; currentHouse++, seeds--) {
			if(currentHouse == pits.length) {
				currentHouse = 0;
			}
			if(this.currentTurn.equals(PLAYER1) && currentHouse != largePit2Index) {
				pits[currentHouse]++;
			} else if(this.currentTurn.equals(PLAYER2) && currentHouse != largePit1Index) {
				pits[currentHouse]++;
			}
		}
		int lastSeededHouse = currentHouse - 1;
		boolean eligibleForCapture = isEligibleForCapture(lastSeededHouse);
		if(eligibleForCapture) {
			performCapture(lastSeededHouse);
		}
		return lastSeededHouse;
	}
	
	public boolean isEligibleForCapture(int lastSeededHouse) {
		int oppositeHouseIndex = largePit2Index - lastSeededHouse - 1;
		if(this.currentTurn.equals(PLAYER1)) {
			// Checks if current house belongs to player
			if(lastSeededHouse > gameSize) {
				return false;
			}
			// Checks if last seed landed on the large pit
			if(lastSeededHouse == largePit1Index) {
				return false;
			}
			// Checks if current house was empty before seeding
			if(pits[lastSeededHouse] != 1) {
				return false;
			}
			// Checks if opponet's opposite house is empty
			if(pits[oppositeHouseIndex] == 0) {
				return false;
			}
			return true;
		} else {
			// Performs same logic as above for player2
			if(lastSeededHouse < gameSize) {
				return false;
			}
			if(lastSeededHouse == largePit2Index) {
				return false;
			}
			if(pits[lastSeededHouse] != 1) {
				return false;
			}
			if(pits[oppositeHouseIndex] == 0) {
				return false;
			}
			return true;
		}
	}
	
	public void performCapture(int lastSeededHouse) {
		System.out.println("performing capture");
		int oppositeHouseIndex = largePit2Index - lastSeededHouse - 1;
		if(this.currentTurn.equals(PLAYER1)) {
			int seedsWon = pits[lastSeededHouse];
			pits[lastSeededHouse] = 0;
			seedsWon += pits[oppositeHouseIndex];
			System.out.println("seeds won = " + seedsWon);
			pits[oppositeHouseIndex] = 0;
			System.out.println("opposite pit index:" + oppositeHouseIndex);
			System.out.println("last seeded house:" + lastSeededHouse);
			pits[largePit1Index]+= seedsWon;
		} else {
			int seedsWon = pits[lastSeededHouse];
			pits[lastSeededHouse] = 0;
			seedsWon += pits[oppositeHouseIndex];
			pits[oppositeHouseIndex] = 0;
			pits[largePit2Index]+= seedsWon;
			System.out.println("seeds won = " + seedsWon);
			System.out.println("opposite pit index:" + oppositeHouseIndex);
			System.out.println("last seeded house:" + lastSeededHouse);
		}
	}
	
	public boolean doesPlayerPlayAgain(int lastSeededHouse) {
		if(this.currentTurn.equals(PLAYER1)) {
			if(lastSeededHouse == largePit1Index) {
				return true;
			}
			return false;
		} else {
			if(lastSeededHouse == largePit2Index) {
				return true;
			}
			return false;
		}
	}
	
	public boolean checkForGameOver() {
		int player1SeedsInPits = 0;
		int player2SeedsInPits = 0;
		for(int i = 0; i < gameSize; i++) {
			player1SeedsInPits+= pits[i];
			player2SeedsInPits+= pits[i + gameSize + 1];
		}
		if(player1SeedsInPits == 0 || player2SeedsInPits == 0) {
			return true;
		}
		return false;
	}
	
	public boolean isMoveValid(int chosenPit) {
		System.out.println("current turn: " + this.currentTurn);
		if(pits[chosenPit] == 0) {
			return false;
		}
		if(this.currentTurn.equals(PLAYER1)) {
			if(chosenPit < 0 || chosenPit >= largePit1Index) {
				return false;
			}
		} else {
			if(chosenPit <= largePit1Index || chosenPit >= largePit2Index) {
				return false;
			}
		}
		return true;
	}
	
	@DynamoDBHashKey(attributeName="gameId")
	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}
	
	@DynamoDBIgnore
	public int[] getPits() {
		return pits;
	}
	
	public void setPits(int[] pits) {
		this.pits = pits;
	}
	
	@DynamoDBAttribute(attributeName="pits")
	public List<Integer> getPitsList() {
		return this.pitsList;
	}

	public void setPitsList(List<Integer> pitsList) {
		this.pitsList = pitsList;
	}

	@DynamoDBAttribute(attributeName="turn")
	public String getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(String currentTurn) {
		this.currentTurn = currentTurn;
	}
	
	@DynamoDBAttribute(attributeName="gameSize")
	public int getGameSize() {
		return gameSize;
	}

	public void setGameSize(int gameSize) {
		this.gameSize = gameSize;
	}
	
	@DynamoDBAttribute(attributeName="seedsNumber")
	public int getSeedsNumber() {
		return seedsNumber;
	}

	public void setSeedsNumber(int seedsNumber) {
		this.seedsNumber = seedsNumber;
	}
	
	@DynamoDBAttribute(attributeName="largePit1Index")
	public int getLargePit1Index() {
		return largePit1Index;
	}

	public void setLargePit1Index(int largePit1Index) {
		this.largePit1Index = largePit1Index;
	}
	
	@DynamoDBAttribute(attributeName="largePit2Index")
	public int getLargePit2Index() {
		return largePit2Index;
	}

	public void setLargePit2Index(int largePit2Index) {
		this.largePit2Index = largePit2Index;
	}
	
	@DynamoDBAttribute(attributeName="gameStatus")
	public String getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}
	
	@DynamoDBAttribute(attributeName="winner")
	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	@DynamoDBIgnore
	public static int getDefaultGameSize() {
		return DEFAULT_GAME_SIZE;
	}
	
	@DynamoDBIgnore
	public static int getDefaultSeedsNumber() {
		return DEFAULT_SEEDS_NUMBER;
	}
	
	@DynamoDBIgnore
	public static String getPlayer1() {
		return PLAYER1;
	}
	@DynamoDBIgnore
	public static String getPlayer2() {
		return PLAYER2;
	}
	
	
	
	
	
}
