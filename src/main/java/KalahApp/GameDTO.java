package KalahApp;

public class GameDTO {
	
	private String gameId;
	
	private int chosenPit;
	
	private int[] pits;
	
	private String gameStatus;
	
	private String winner;
	
	private String turn;

	public String getGameId() {
		return gameId;
	}
	
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getChosenPit() {
		return chosenPit;
	}

	public void setChosenPit(int chosenPit) {
		this.chosenPit = chosenPit;
	}

	public int[] getPits() {
		return pits;
	}

	public void setPits(int[] pits) {
		this.pits = pits;
	}

	public String getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}
	
}
