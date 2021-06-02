package kalahApp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;

import kalahDao.BoardDaoImpl;
import kalahModel.GameDTO;
import kalahService.BoardService;

public class LambdaFunctionHandler implements RequestHandler<GameDTO, GameDTO> {
	LambdaLogger logger;
	
	Injector injector = Guice.createInjector(new BasicModule());
	
	@Override
	public GameDTO handleRequest(GameDTO event, Context context) {
		this.logger = context.getLogger();
		if(event.getGameId() == null) {
			return null;
		}
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