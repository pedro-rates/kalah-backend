package kalahDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import kalahModel.Board;

public class BoardDaoImpl implements BoardDao {
	
	private static final Logger logger = LoggerFactory.getLogger(BoardDaoImpl.class);
	
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion(Regions.US_EAST_1)
            .build();

	public Board getBoard(String gameId) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
	    Board board = new Board();
	    board.setBoardId(gameId);
	    try {
	    	logger.debug("Retrieving board with id " + board.getBoardId());
	    	DynamoDBQueryExpression<Board> queryExpression = new DynamoDBQueryExpression<Board>()
	    			.withHashKeyValues(board);
	 	    
	 	    List<Board> boardList = mapper.query(Board.class, queryExpression);
	 	    if(boardList.isEmpty()) {
	 	    	logger.debug("Board not found");
	 	    	return null;
	 	    }
	 	    Board returnBoard = boardList.get(0);
	 	    List<Integer> pitsList = returnBoard.getPitsList();
	 	    int[] pits = ArrayUtils.toPrimitive(pitsList.toArray(new Integer[pitsList.size()]));
	 	    returnBoard.setPits(pits);
	 	    return returnBoard;
	    }
	    catch (Exception e) {
	    	logger.error("Unable to read item: " + gameId);
	    	logger.error(e.getMessage());
	    }
		return null;
	}
	
	public void createBoard(Board board) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
	    board.setPitsList(Arrays.stream(board.getPits()).boxed().collect(Collectors.toList()));
	    try {
	    	mapper.save(board);
	    }
        catch (Exception e) {
        	logger.error("Unable to add item: " + board.getBoardId());
        	logger.error(e.getMessage());
        }
	}
	
	public void saveBoard(Board board) {
		DynamoDB dynamoDB = new DynamoDB(client);
	    Table table = dynamoDB.getTable("kallah_game");
	    List<Integer> pitsList = Arrays.stream(board.getPits()).boxed().collect(Collectors.toList());
	    UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("gameId", board.getBoardId())
	            .withUpdateExpression("set pits=:a, turn=:t")
	            .withValueMap(new ValueMap().withString(":t", board.getCurrentTurn()).withList(":a", pitsList))
	            .withReturnValues(ReturnValue.UPDATED_NEW);
	        try {
	            logger.debug("Updating the item...");
	            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
	            logger.debug("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
	        }
	        catch (Exception e) {
	            logger.error("Unable to update item: " + board.getBoardId());
	            logger.error(e.getMessage());
	        }
	}
}
