package KalahDao;

import KalahBackend.Board;

public interface BoardDao {
	
	public Board getBoard(String gameId);
	public void createBoard(Board board);
	public void saveBoard(Board board);
	
}
