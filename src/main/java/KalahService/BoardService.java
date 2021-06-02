package KalahService;

import com.google.inject.Inject;

import KalahApp.GameDTO;
import KalahBackend.Board;
import KalahDao.BoardDaoImpl;

public class BoardService {
	
	@Inject
	BoardDaoImpl boardDao;
	
	public BoardService(BoardDaoImpl boardDao) {
		this.boardDao = boardDao;
	}
	
	public GameDTO executePlayerMove(GameDTO gameDTO) {
		Board board = boardDao.getBoard(gameDTO.getGameId());
		if(board == null) {
			return null;
		}
		System.out.println("gameId: " + board.getBoardId());
		int chosenPit = gameDTO.getChosenPit();
		System.out.println("chosen pit: " + chosenPit);
		boolean validMove = board.isMoveValid(chosenPit);
		if(!validMove) {
			board.setGameStatus(Board.GAME_STATUS_INVALID);
			return mapBoardToGameDTO(board);
		}
		int lastSeededHouse = board.processSowing(chosenPit);
		boolean gameOver = board.checkForGameOver();
		if(gameOver) {
			board.setGameStatus(Board.GAME_STATUS_FINISHED);
			int player1SeedsInStorage = board.getPits()[board.getLargePit1Index()];
			int player2SeedsInStorage = board.getPits()[board.getLargePit2Index()];
			if(player1SeedsInStorage > player2SeedsInStorage) {
				board.setWinner(Board.PLAYER1);
			} else if(player1SeedsInStorage == player2SeedsInStorage) {
				board.setWinner(Board.GAME_RESULT_DRAW);
			} else {
				board.setWinner(Board.PLAYER2);
			}
			boardDao.saveBoard(board);
			return mapBoardToGameDTO(board);
		}	
		boolean playAgain = board.doesPlayerPlayAgain(lastSeededHouse);
		if(playAgain == false) {
			if(board.getCurrentTurn().equals(Board.PLAYER1)) {
				board.setCurrentTurn(Board.PLAYER2);
			} else {
				board.setCurrentTurn(Board.PLAYER1);
			}
		}
		boardDao.saveBoard(board);
		return mapBoardToGameDTO(board);	
	}
	
	public GameDTO mapBoardToGameDTO(Board board) {
		GameDTO gameDTO = new GameDTO();
		gameDTO.setGameId(board.getBoardId());
		gameDTO.setGameStatus(board.getGameStatus());
		gameDTO.setTurn(board.getCurrentTurn());
		gameDTO.setPits(board.getPits());
		gameDTO.setWinner(board.getWinner());
		return gameDTO;
	}
	
	public GameDTO initializeGame() {
		Board board = new Board();
		boardDao.createBoard(board);
		return mapBoardToGameDTO(board);
	}
	
}
