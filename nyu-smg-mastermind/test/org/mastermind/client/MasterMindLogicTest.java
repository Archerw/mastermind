package org.mastermind.client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mastermind.client.GameApi.Delete;
import org.mastermind.client.GameApi.EndGame;
import org.mastermind.client.GameApi.Operation;
import org.mastermind.client.GameApi.Set;
import org.mastermind.client.GameApi.SetVisibility;
import org.mastermind.client.GameApi.VerifyMove;
import org.mastermind.client.GameApi.VerifyMoveDone;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MasterMindLogicTest {

  private void assertMoveOk(VerifyMove verifyMove) {
    VerifyMoveDone verifyDone = new MasterMindLogic().verify(verifyMove);
    assertEquals(new VerifyMoveDone(), verifyDone);
  }

  private void assertHacker(VerifyMove verifyMove) {
    VerifyMoveDone verifyDone = new MasterMindLogic().verify(verifyMove);
    assertEquals(new VerifyMoveDone(verifyMove.getLastMovePlayerId(), "Hacker found"), verifyDone);
  }


  private final int wId = 41;
  private final int bId = 42;
  private final String PLAYERID = "playerId";
  private final String TURN = "turn";
  private static final String W = "W";
  private static final String B = "B";
  private final String GUESSHISTORY = "GuessHistory";
  private final String FEEDBACKHISTORY = "FeedbackHistory";
  private final String CURRENTGAME = "CurrentGame";
  private final String CURRENTMOVE = "CurrentMove";
  private static final String CODE = "CODE";
  private static final String GUESS = "GUESS";
  private static final String FEEDBACK = "FEEDBACK";
  private final String CURRENTTURN = "CurrentTurn";
  private final String SCORE = "Score";
  
  
  private final String CODELENGTH= "CodeLength";
  private final int CL = 4;
  private final String MAXTURN= "MaxTurn";
  private final int MT = 2;
  private final String MAXDIGIT= "MaxDigit";
  private final int MD = 10;
  
  
  private final Map<String,Object> mInfo = ImmutableMap.<String, Object>of(PLAYERID, wId);
  private final Map<String,Object> bInfo = ImmutableMap.<String, Object>of(PLAYERID, bId);
  private final List<Map<String, Object>> playersInfo = ImmutableList.of(mInfo, bInfo);
  private final Map<String, Object> emptyState = ImmutableMap.<String, Object>of();
  private final Map<String, Object> nonEmptyState = ImmutableMap.<String, Object>of("k", "v");
  
  
  private final Map<String, Object> initialState = ImmutableMap.<String,Object>builder()
  		.put(TURN,W)
  		.put(CODELENGTH, CL)
  		.put(MAXTURN,MT)
  		.put(MAXDIGIT,MD)
  		.put(GUESSHISTORY, ImmutableList.<String>of())
  		.put(FEEDBACKHISTORY, ImmutableList.<String>of())
  		.put(CURRENTGAME,1)
  		.put(CURRENTMOVE,CODE)
  		.put(CURRENTTURN,0)
  		.build();
  
  private final Map<String, Object> codedState = ImmutableMap.<String,Object>builder()
  		.put(TURN,B)
  		.put(CODELENGTH, CL)
  		.put(MAXTURN,MT)
  		.put(MAXDIGIT,MD)
  		.put(GUESSHISTORY, ImmutableList.<String>of())
  		.put(FEEDBACKHISTORY, ImmutableList.<String>of())
  		.put(CURRENTGAME,1)
  		.put(CURRENTMOVE,GUESS)
  		.put(CURRENTTURN,0)
  		.put(CODE,"1598")
  		.build();
  
  private final Map<String, Object> guessedState = ImmutableMap.<String,Object>builder()
  		.put(TURN,W)
  		.put(CODELENGTH, CL)
  		.put(MAXTURN,MT)
  		.put(MAXDIGIT,MD)
  		.put(GUESSHISTORY, ImmutableList.<String>of("1234"))
  		.put(FEEDBACKHISTORY, ImmutableList.<String>of())
  		.put(CURRENTGAME,1)
  		.put(CURRENTMOVE,FEEDBACK)
  		.put(CURRENTTURN,1)
  		.put(CODE,"1598")
  		.build();
  
  private final Map<String, Object> returnedState = ImmutableMap.<String,Object>builder()
  		.put(TURN,B)
  		.put(CODELENGTH, CL)
  		.put(MAXTURN,MT)
  		.put(MAXDIGIT,MD)
  		.put(GUESSHISTORY, ImmutableList.<String>of("1234"))
  		.put(FEEDBACKHISTORY, ImmutableList.<String>of("1b0w"))
  		.put(CURRENTGAME,1)
  		.put(CURRENTMOVE,GUESS)
  		.put(CURRENTTURN,1)
  		.put(CODE,"1598")
  		.build();
  
  private List<Operation> getInitialOperations() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CODELENGTH, CL))
  		.add(new Set(MAXTURN,MT))
  		.add(new Set(MAXDIGIT,MD))
  		.add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
  		.add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
  		.add(new Set(CURRENTGAME,1))
  		.add(new Set(CURRENTMOVE,CODE))
  		.add(new Set(CURRENTTURN,0))
  		.build();
    return init;
  }
  
  private List<Operation> getAddedInitialOperations() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CODELENGTH, CL))
  		.add(new Set(MAXTURN,MT))
  		.add(new Set(MAXDIGIT,MD))
  		.add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
  		.add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
  		.add(new Set(CURRENTGAME,1))
  		.add(new Set(CURRENTMOVE,CODE))
  		.add(new Set(CURRENTTURN,0))
  		.add(new Set(CODE,"1234"))
  		.build();
    return init;
  }
  
  private List<Operation> getModifiedInitialOperations() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CODELENGTH, CL))
  		.add(new Set(MAXTURN,MT))
  		.add(new Set(MAXDIGIT,MD))
  		.add(new Set(GUESSHISTORY, ImmutableList.<String>of(" ")))
  		.add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
  		.add(new Set(CURRENTGAME,1))
  		.add(new Set(CURRENTMOVE,CODE))
  		.add(new Set(CURRENTTURN,0))
  		.build();
    return init;
  }
  
  private VerifyMove move(int lastMovePlayerId, 
  		Map<String, Object> lastState, List<Operation> lastMove){
  	return new VerifyMove(wId, playersInfo,
  			emptyState,
  			lastState, lastMove, lastMovePlayerId);
  }
  
  @Test
  public void testLegalInitial(){
  	assertMoveOk(move(wId, emptyState, getInitialOperations()));
  }
  
  @Test
  public void testWrongPlayerInitial(){
  	assertHacker(move(bId, emptyState, getInitialOperations()));
  }
  
  @Test
  public void testInitialwithExtraMove(){
  	assertHacker(move(wId, emptyState, getAddedInitialOperations()));
  }
  
  @Test
  public void testInitialwithModifiedMove(){
  	assertHacker(move(wId, emptyState, getModifiedInitialOperations()));
  }
  
  @Test
  public void testInitialMoveFromNonEmptyState() {
    assertHacker(move(wId, nonEmptyState, getInitialOperations()));
  }
  
  private List<Operation> getCodeOperations() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(CODE,"1598"))
  		.add(new SetVisibility(CODE,ImmutableList.of(wId)))
  		.build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithWrongNextTurn() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(CODE,"1598"))
  		.add(new SetVisibility(CODE,ImmutableList.of(wId)))
  		.build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithWrongNextPhase() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,CODE))
  		.add(new Set(CODE,"1598"))
  		.add(new SetVisibility(CODE,ImmutableList.of(wId)))
  		.build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithInvalidCodeLength() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(CODE,"15981"))
  		.add(new SetVisibility(CODE,ImmutableList.of(wId)))
  		.build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithInvalidCodeDigit() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(CODE,"123d"))
  		.add(new SetVisibility(CODE,ImmutableList.of(wId)))
  		.build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithInvalidVisibility() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(CODE,"123d"))
  		.add(new SetVisibility(CODE,ImmutableList.of(wId,bId)))
  		.build();
    return init;
  }
  
  @Test
  public void testLegalCode(){
  	assertMoveOk(move(wId, initialState, getCodeOperations()));
  }

  @Test
  public void testIllegalCodePlayer(){
  	assertHacker(move(wId, initialState, getCodeOperations()));
  }
  
  @Test
  public void testIllegalCodeWithWrongNextTurnOrPhase(){
  	assertHacker(move(wId, initialState, getCodeOperationsWithWrongNextTurn()));
  	assertHacker(move(wId, initialState, getCodeOperationsWithWrongNextPhase()));
  	}
  
  @Test
  public void testIllegalCodeWithWrongCode(){
  	assertHacker(move(wId, initialState, getCodeOperationsWithInvalidCodeLength()));
  	assertHacker(move(wId, initialState, getCodeOperationsWithInvalidCodeDigit()));
  }
  
  @Test
  public void testIllegalCodeWithVisibility(){
  	assertHacker(move(wId, initialState, getCodeOperationsWithInvalidVisibility()));
  }
  
  private List<Operation> getGuessOperations() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CURRENTMOVE,FEEDBACK))
  		.add(new Set(CURRENTTURN,1))
  		.add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234")))
  		.build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithWrongTurn() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,FEEDBACK))
  		.add(new Set(CURRENTTURN,1))
  		.add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234")))
  		.build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithWrongMove() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(CURRENTTURN,1))
  		.add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234")))
  		.build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithMessageLength() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CURRENTMOVE,FEEDBACK))
  		.add(new Set(CURRENTTURN,1))
  		.add(new Set(GUESSHISTORY,ImmutableList.<String>of("12345")))
  		.build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithMessageCode() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CURRENTMOVE,FEEDBACK))
  		.add(new Set(CURRENTTURN,1))
  		.add(new Set(GUESSHISTORY,ImmutableList.<String>of("123d")))
  		.build();
    return init;
  }
  
  @Test
  public void testLegalGuess(){
  	assertMoveOk(move(bId, codedState, getGuessOperations()));
  }
  
  @Test
  public void testIllegalGuesswithWrongPlayer(){
  	assertHacker(move(wId, codedState, getGuessOperations()));
  }
  
  @Test
  public void testIllegalGuesswithWrongNextTurnOrPhase(){
  	assertHacker(move(bId, codedState, getGuessOperationsWithWrongMove()));
  	assertHacker(move(bId, codedState, getGuessOperationsWithWrongTurn()));
  }
  
  @Test
  public void testIllegalGuesswithWrongMessage(){
  	assertHacker(move(bId, codedState, getGuessOperationsWithMessageLength()));
  	assertHacker(move(bId, codedState, getGuessOperationsWithMessageCode()));
  }
  
  
  private List<Operation> getFeedbackOperations() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1234")))
  		.build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithWrongTurn() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,W))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1234")))
  		.build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithWrongMove() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,FEEDBACK))
  		.add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1234")))
  		.build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithMessageLength() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("12345")))
  		.build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithMessageCode() {
  	List<Operation> init = ImmutableList.<Operation>builder()
  		.add(new Set(TURN,B))
  		.add(new Set(CURRENTMOVE,GUESS))
  		.add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("123d")))
  		.build();
    return init;
  }
  
  @Test
  public void testLegalFeedback(){
  	assertMoveOk(move(wId, guessedState, getFeedbackOperations()));
  }
  
  @Test
  public void testIllegalFeedbackWithWrongPlayer(){
  	assertHacker(move(bId, guessedState, getFeedbackOperations()));
  }
  
  @Test
  public void testIllegalFeedbackWithWrongNextTurnOrPhase(){
  	assertHacker(move(wId, guessedState, getFeedbackOperationsWithWrongMove()));
  	assertHacker(move(wId, guessedState, getFeedbackOperationsWithWrongTurn()));
  }
  
  @Test
  public void testIllegalFeedbackWithWrongMessage(){
  	assertHacker(move(wId, guessedState, getFeedbackOperationsWithMessageLength()));
  	assertHacker(move(wId, guessedState, getFeedbackOperationsWithMessageCode()));
  }
  
  @Test
  public void testWrongNextMove(){
  	assertHacker(move(wId, codedState, getFeedbackOperations()));
  	assertHacker(move(wId, guessedState, getGuessOperations()));
  }
  
  @Test
  public void testSwitchGameWithCorrectGuess() {
  	Map<String, Object> state = ImmutableMap.<String,Object>builder()
    		.put(TURN,B)
    		.put(CODELENGTH, CL)
    		.put(MAXTURN,MT)
    		.put(MAXDIGIT,MD)
    		.put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
    		.put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
    		.put(CURRENTGAME,1)
    		.put(CURRENTMOVE,FEEDBACK)
    		.put(CURRENTTURN,2)
    		.put(CODE,"1598")
    		.build();
  	List<Operation> operations = ImmutableList.<Operation>builder()
    		.add(new Set(TURN,B))
    		.add(new Set(CODELENGTH, CL))
    		.add(new Set(MAXTURN,MT))
    		.add(new Set(MAXDIGIT,MD))
    		.add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
    		.add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
    		.add(new Set(CURRENTGAME,2))
    		.add(new Set(CURRENTMOVE,CODE))
    		.add(new Set(CURRENTTURN,0))
    		.add(new Delete(CODE))
    		.build();
    assertMoveOk(move(bId, state, operations));
  }
  
  @Test
  public void testEndGameWithCorrectGuess() {
  	Map<String, Object> state = ImmutableMap.<String,Object>builder()
    		.put(TURN,B)
    		.put(CODELENGTH, CL)
    		.put(MAXTURN,10)
    		.put(MAXDIGIT,MD)
    		.put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
    		.put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w"))
    		.put(CURRENTGAME,2)
    		.put(CURRENTMOVE,FEEDBACK)
    		.put(CURRENTTURN,2)
    		.put(CODE,"1598")
    		.build();
  	List<Operation> operations = ImmutableList.<Operation>of(
        new Set(TURN, W),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("4b0w")),
        new SetVisibility(CODE,ImmutableList.of(wId)),
        new EndGame(wId));
    assertMoveOk(move(bId, state, operations));
  }
  
  @Test
  public void testIllegalEndGameWithCorrectGuess() {
  	Map<String, Object> state = ImmutableMap.<String,Object>builder()
    		.put(TURN,B)
    		.put(CODELENGTH, CL)
    		.put(MAXTURN,10)
    		.put(MAXDIGIT,MD)
    		.put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
    		.put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w"))
    		.put(CURRENTGAME,2)
    		.put(CURRENTMOVE,FEEDBACK)
    		.put(CURRENTTURN,2)
    		.put(CODE,"1598")
    		.build();
  	List<Operation> operations = ImmutableList.<Operation>of(
        new Set(TURN, W),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("4b0w")),
        new SetVisibility(CODE,ImmutableList.of(wId)),
        new EndGame(bId));
    assertHacker(move(bId, state, operations));
  }
  
  @Test
  public void testEndGameWithMaxTurn() {
  	Map<String, Object> state = ImmutableMap.<String,Object>builder()
    		.put(TURN,B)
    		.put(CODELENGTH, CL)
    		.put(MAXTURN,2)
    		.put(MAXDIGIT,MD)
    		.put(GUESSHISTORY, ImmutableList.<String>of("1597","1596"))
    		.put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w"))
    		.put(CURRENTGAME,2)
    		.put(CURRENTMOVE,FEEDBACK)
    		.put(CURRENTTURN,2)
    		.put(CODE,"1598")
    		.build();
  	List<Operation> operations = ImmutableList.<Operation>of(
        new Set(TURN, W),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w")),
        new SetVisibility(CODE,ImmutableList.of(wId)),
        new EndGame(bId));

    assertMoveOk(move(bId, state, operations));
  }
  
}
