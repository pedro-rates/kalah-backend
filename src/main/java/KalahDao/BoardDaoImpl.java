package KalahDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

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

import KalahBackend.Board;

public class BoardDaoImpl implements BoardDao {
	
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion(Regions.US_EAST_1)
            .build();

	public Board getBoard(String gameId) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
	    Board board = new Board();
	    board.setBoardId(gameId);
	    try {
	    	System.out.println("Retrieving board with id " + board.getBoardId());
	    	DynamoDBQueryExpression<Board> queryExpression = new DynamoDBQueryExpression<Board>()
	    			.withHashKeyValues(board);
	 	    
	 	    List<Board> boardList = mapper.query(Board.class, queryExpression);
	 	    if(boardList.isEmpty()) {
	 	    	System.out.println("Board not found");
	 	    	return null;
	 	    }
	 	    Board returnBoard = boardList.get(0);
	 	    List<Integer> pitsList = returnBoard.getPitsList();
	 	    int[] pits = ArrayUtils.toPrimitive(pitsList.toArray(new Integer[pitsList.size()]));
	 	    returnBoard.setPits(pits);
	 	    return returnBoard;
	    }
	    catch (Exception e) {
	        System.err.println("Unable to read item: " + gameId);
	        System.err.println(e.getMessage());
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
            System.err.println("Unable to add item: " + board.getBoardId());
            System.err.println(e.getMessage());
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
	            System.out.println("Updating the item...");
	            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
	            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
	        }
	        catch (Exception e) {
	            System.err.println("Unable to update item: " + board.getBoardId());
	            System.err.println(e.getMessage());
	        }
	}
}
