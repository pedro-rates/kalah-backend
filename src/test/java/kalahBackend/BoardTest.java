package kalahBackend;

import static org.junit.Assert.*;

import org.junit.Test;

import kalahModel.Board;

public class BoardTest {
	
	
	@Test
	public void shouldInitializeBoardWithCorrectNumberOfSeedsOnPits() {
		Board board = new Board();
		int[] pits = board.getPits();
		int defaultSeedsNumber = 4;
		for(int i = 0; i < pits.length; i++) {
			if(i == board.getLargePit1Index() || i == board.getLargePit2Index()) {
				assertEquals(0, pits[i]);
			} else {
				assertEquals(defaultSeedsNumber, pits[i]);
			}
		}
		assertEquals("PLAYER1", board.getCurrentTurn());
	}
	
	@Test
	public void playerShouldNotPlayAgain_WhenLastSowedSeedDoesNotEndUpInTheirLargePit() {
		Board board = new Board();
		assertEquals("PLAYER1", board.getCurrentTurn());
		int lastSeededHouse = board.processSowing(1);
		assertFalse(board.doesPlayerPlayAgain(lastSeededHouse));
		
		board = new Board();
		board.setCurrentTurn("PLAYER2");
		lastSeededHouse = board.processSowing(7);
		assertFalse(board.doesPlayerPlayAgain(lastSeededHouse));
	}
	@Test
	public void playerShouldPlayAgain_WhenLastSowedSeedEndsUpOnTheirLargePit() {
		Board board = new Board();
		assertEquals("PLAYER1", board.getCurrentTurn());
		int lastSeededHouse = board.processSowing(2);
		assertTrue(board.doesPlayerPlayAgain(lastSeededHouse));
		assertEquals(lastSeededHouse, board.getLargePit1Index());
		
		board = new Board();
		board.setCurrentTurn("PLAYER2");
		lastSeededHouse = board.processSowing(9);
		assertTrue(board.doesPlayerPlayAgain(lastSeededHouse));
		assertEquals(lastSeededHouse, board.getLargePit2Index());
	}
	
	@Test
	public void shouldPerformCapture_WhenLastSeedEndsUpInAEmptyPitAndOppositePitHasSeeds() {
		Board board = new Board();
		int[] pits = {4, 4, 4, 4, 0, 5, 1, 0, 6, 5, 5, 5, 4, 0};
		// {0, 5, 5, 5, 0, 5, 8, 0, 0, 5, 5, 5, 4, 0};
		board.setPits(pits);
		int lastSeeededHouse = board.processSowing(0);
		int[] reorganizedPits = board.getPits();
		assertEquals(4, lastSeeededHouse);
		assertEquals(reorganizedPits[0], 0);
		assertEquals(reorganizedPits[4], 0);
		assertEquals(reorganizedPits[8], 0);
		assertEquals(reorganizedPits[6], 8);
		
		pits = new int[]{5, 5, 5, 4, 4, 0, 1, 5, 5, 5, 4, 4, 0, 1};
		// {0, 6, 6, 5, 5, 0, 7, 0, 5, 5, 4, 4, 0, 1};
		board.setPits(pits);
		lastSeeededHouse = board.processSowing(0);
		reorganizedPits = board.getPits();
		assertEquals(5, lastSeeededHouse);
		assertEquals(reorganizedPits[0], 0);
		assertEquals(reorganizedPits[5], 0);
		assertEquals(reorganizedPits[7], 0);
		assertEquals(reorganizedPits[6], 7);
		
		board.setCurrentTurn("PLAYER2");
		pits = new int[]{2, 7, 0, 8, 6, 5, 1, 5, 4, 4, 4, 0, 0, 2};
		// {0, 7, 0, 8, 6, 5, 1, 0, 5, 5, 5, 1, 0, 5};
		board.setPits(pits);
		lastSeeededHouse = board.processSowing(7);
		reorganizedPits = board.getPits();
		assertEquals(12, lastSeeededHouse);
		assertEquals(reorganizedPits[0], 0);
		assertEquals(reorganizedPits[7], 0);
		assertEquals(reorganizedPits[13], 5);
		assertEquals(reorganizedPits[12], 0);
		
	}
	
}
