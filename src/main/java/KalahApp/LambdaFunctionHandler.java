package KalahApp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;

import KalahDao.BoardDaoImpl;
import KalahService.BoardService;

public class LambdaFunctionHandler implements RequestHandler<GameDTO, GameDTO> {
	LambdaLogger logger;
	
	Injector injector = Guice.createInjector(new BasicModule());
	
	@Override
	public GameDTO handleRequest(GameDTO event, Context context) {
		this.logger = context.getLogger();
		return handleGameInput(event);
	}
	
	public GameDTO handleGameInput(GameDTO event) {
	    BoardService boardService = new BoardService(injector.getInstance(BoardDaoImpl.class));
		String gameId = event.getGameId();
		if(gameId.isEmpty()) {
			return boardService.initializeGame();
		}
		return boardService.executePlayerMove(event);
		
	}

}