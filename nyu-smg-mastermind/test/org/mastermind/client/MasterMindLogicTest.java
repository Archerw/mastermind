package org.mastermind.client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mastermind.client.GameApi.Delete;
import org.mastermind.client.GameApi.EndGame;
import org.mastermind.client.GameApi.Operation;
import org.mastermind.client.GameApi.Set;
import org.mastermind.client.GameApi.SetTurn;
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
    assertEquals(verifyMove.getLastMovePlayerId(), verifyDone.getHackerPlayerId());
  }


  private final int wId = 41;
  private final int bId = 42;
  private final String PLAYERID = "playerId";
  private final String GUESSHISTORY = "GuessHistory";
  private final String FEEDBACKHISTORY = "FeedbackHistory";
  private final String CURRENTGAME = "CurrentGame";
  private final String CURRENTMOVE = "CurrentMove";
  private static final String CODE = "CODE";
  private static final String GUESS = "GUESS";
  private static final String FEEDBACK = "FEEDBACK";
  private static final String VERIFY = "VERIFY";
  private final String CURRENTTURN = "CurrentTurn";
  private final String END = "End";
  
  
  private final String CODELENGTH= "CodeLength";
  private final int CL = 4;
  private final String MAXTURN= "MaxTurn";
  private final int MT = 10;
  private final String MAXDIGIT= "MaxDigit";
  private final int MD = 9;
  
  
  private final Map<String,Object> mInfo = ImmutableMap.<String, Object>of(PLAYERID, wId);
  private final Map<String,Object> bInfo = ImmutableMap.<String, Object>of(PLAYERID, bId);
  private final List<Map<String, Object>> playersInfo = ImmutableList.of(mInfo, bInfo);
  private final Map<String, Object> emptyState = ImmutableMap.<String, Object>of();
  private final Map<String, Object> nonEmptyState = ImmutableMap.<String, Object>of("k", "v");
  
  
  private final Map<String, Object> initialState = ImmutableMap.<String,Object>builder()
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
      .add(new SetTurn(wId))
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
      .add(new SetTurn(wId))
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
      .add(new SetTurn(wId))
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
  
  private VerifyMove move(
      int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
    return new VerifyMove(playersInfo,
        emptyState,
        lastState, lastMove, lastMovePlayerId, ImmutableMap.<Integer, Integer>of());
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
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(CODE,"1598"))
      .add(new SetVisibility(CODE,ImmutableList.of(wId)))
      .build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithWrongNextTurn() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(CODE,"1598"))
      .add(new SetVisibility(CODE,ImmutableList.of(wId)))
      .build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithWrongNextPhase() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,CODE))
      .add(new Set(CODE,"1598"))
      .add(new SetVisibility(CODE,ImmutableList.of(wId)))
      .build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithInvalidCodeLength() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(CODE,"15981"))
      .add(new SetVisibility(CODE,ImmutableList.of(wId)))
      .build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithInvalidCodeDigit() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(CODE,"123d"))
      .add(new SetVisibility(CODE,ImmutableList.of(wId)))
      .build();
    return init;
  }
  
  private List<Operation> getCodeOperationsWithInvalidVisibility() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
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
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234")))
      .build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithWrongTurn() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234")))
      .build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithWrongMove() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("1239")))
      .build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithMessageLength() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("12345")))
      .build();
    return init;
  }
  
  private List<Operation> getGuessOperationsWithMessageCode() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("123d")))
      .build();
    return init;
  }
  
  private List<Operation> getMoreGuessOperations() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,2))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234","5678")))
      .build();
    return init;
  }
  
  private List<Operation> getIllegalGuessOperationsWithLongerHistory() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234","1234","5678")))
      .build();
    return init;
  }
  
  private List<Operation> getIllegalGuessOperationsWithShorterHistory() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("5678")))
      .build();
    return init;
  }
  
  private List<Operation> getIllegalGuessOperationsWithCHangedHistory() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("5678","5678")))
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
  
  @Test
  public void testLegalMoreGuess(){
    assertMoveOk(move(bId, returnedState, getMoreGuessOperations()));
  }
  
  @Test
  public void testMoreGuessWithIllegalHistory(){
    assertHacker(move(bId, returnedState, getIllegalGuessOperationsWithLongerHistory()));
    assertHacker(move(bId, returnedState, getIllegalGuessOperationsWithShorterHistory()));
    assertHacker(move(bId, returnedState, getIllegalGuessOperationsWithCHangedHistory()));
  }
  
  private List<Operation> getFeedbackOperations() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1b0w")))
      .build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithWrongTurn() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1b0w")))
      .build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithWrongMove() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1b0w")))
      .build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithWrongMessageLength() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("12345")))
      .build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithWrongMessageCode() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1w3b")))
      .build();
    return init;
  }
  
  private List<Operation> getFeedbackOperationsWithWrongHistoryLength() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1b1w","1b0w")))
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
    assertHacker(move(wId, guessedState, getFeedbackOperationsWithWrongMessageLength()));
    assertHacker(move(wId, guessedState, getFeedbackOperationsWithWrongMessageCode()));
    assertHacker(move(wId, guessedState, getFeedbackOperationsWithWrongHistoryLength()));
  }
  
  @Test
  public void testWrongNextMove(){
    assertHacker(move(wId, codedState, getFeedbackOperations()));
    assertHacker(move(wId, guessedState, getGuessOperations()));
  }
  
  //TODO SWITCH INITIAL
  @Test
  public void testSwitchGameWithCorrectGuess() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,2))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,0))
        .add(new Delete(CODE))
        .build();
    assertMoveOk(move(wId, state, operations));
  }
  
  @Test
  public void testSwitchGameWithIncorrectGuess() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,3)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1596","1595"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w","3b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,3)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,3))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,0))
        .add(new Delete(CODE))
        .build();
    assertMoveOk(move(wId, state, operations));
  }
  
  @Test
  public void testIllegalSwitchGameWithCorrectGuessWithWrongHistory() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of("1234")))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of("0b0w")))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,2))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,0))
        .add(new Delete(CODE))
        .build();
    assertHacker(move(wId, state, operations));
  }
  
  @Test
  public void testIllegalSwitchGameWithCorrectGuessWithWrongCurrentGame() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,3))
        .add(new Set(MAXTURN,2))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,0))
        .add(new Delete(CODE))
        .build();
    assertHacker(move(wId, state, operations));
  }
  
  @Test
  public void testIllegalSwitchGameWithCorrectGuessWithWrongMaxTurn() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,5))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,0))
        .add(new Delete(CODE))
        .build();
    assertHacker(move(wId, state, operations));
  }
  
  @Test
  public void testIllegalSwitchGameWithCorrectGuessWithWrongMove() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,2))
        .add(new Set(CURRENTMOVE,VERIFY))
        .add(new Set(CURRENTTURN,0))
        .add(new Delete(CODE))
        .build();
    assertHacker(move(wId, state, operations));
  }
  
  @Test
  public void testIllegalSwitchGameWithCorrectGuessWithWrongCurrentTurn() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,2))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,1))
        .add(new Delete(CODE))
        .build();
    assertHacker(move(wId, state, operations));
  }
  
  @Test
  public void testIllegalSwitchGameWithCorrectGuessWithPreviousCode() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(bId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,2))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,0))
        .build();
    assertHacker(move(wId, state, operations));
  }
  
  @Test
  public void testIllegalSwitchGameWithCorrectGuessWithWrongPlayer() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,1)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>builder()
        .add(new SetTurn(wId))
        .add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
        .add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
        .add(new Set(CURRENTGAME,2))
        .add(new Set(MAXTURN,2))
        .add(new Set(CURRENTMOVE,CODE))
        .add(new Set(CURRENTTURN,0))
        .add(new Delete(CODE))
        .build();
    assertHacker(move(wId, state, operations));
  }
  
  
  private final Map<String, Object> initialSwitchedState = ImmutableMap.<String,Object>builder()
      .put(CODELENGTH, CL)
      .put(MAXTURN,MT)
      .put(MAXDIGIT,MD)
      .put(GUESSHISTORY, ImmutableList.<String>of())
      .put(FEEDBACKHISTORY, ImmutableList.<String>of())
      .put(CURRENTGAME,2)
      .put(CURRENTMOVE,CODE)
      .put(CURRENTTURN,0)
      .build();
  
  private final Map<String, Object> codedSwitchedState = ImmutableMap.<String,Object>builder()
      .put(CODELENGTH, CL)
      .put(MAXTURN,MT)
      .put(MAXDIGIT,MD)
      .put(GUESSHISTORY, ImmutableList.<String>of())
      .put(FEEDBACKHISTORY, ImmutableList.<String>of())
      .put(CURRENTGAME,2)
      .put(CURRENTMOVE,GUESS)
      .put(CURRENTTURN,0)
      .put(CODE,"1598")
      .build();
  
  private final Map<String, Object> guessedSwitchedState = ImmutableMap.<String,Object>builder()
      .put(CODELENGTH, CL)
      .put(MAXTURN,MT)
      .put(MAXDIGIT,MD)
      .put(GUESSHISTORY, ImmutableList.<String>of("1234"))
      .put(FEEDBACKHISTORY, ImmutableList.<String>of())
      .put(CURRENTGAME,2)
      .put(CURRENTMOVE,FEEDBACK)
      .put(CURRENTTURN,1)
      .put(CODE,"1598")
      .build();
  
  private final Map<String, Object> returnedSwitchedState = ImmutableMap.<String,Object>builder()
      .put(CODELENGTH, CL)
      .put(MAXTURN,MT)
      .put(MAXDIGIT,MD)
      .put(GUESSHISTORY, ImmutableList.<String>of("1234"))
      .put(FEEDBACKHISTORY, ImmutableList.<String>of("1b0w"))
      .put(CURRENTGAME,2)
      .put(CURRENTMOVE,GUESS)
      .put(CURRENTTURN,1)
      .put(CODE,"1598")
      .build();
  
  private List<Operation> getSwitchedCodeOperations() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(CODE,"1598"))
      .add(new SetVisibility(CODE,ImmutableList.of(bId)))
      .build();
    return init;
  }
  
  private List<Operation> getSwitchedGuessOperations() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(bId))
      .add(new Set(CURRENTMOVE,FEEDBACK))
      .add(new Set(CURRENTTURN,1))
      .add(new Set(GUESSHISTORY,ImmutableList.<String>of("1234")))
      .build();
    return init;
  }
  
  private List<Operation> getSwitchedFeedbackOperations() {
    List<Operation> init = ImmutableList.<Operation>builder()
      .add(new SetTurn(wId))
      .add(new Set(CURRENTMOVE,GUESS))
      .add(new Set(FEEDBACKHISTORY,ImmutableList.<String>of("1b0w")))
      .build();
    return init;
  }
  
  @Test
  public void testSwitchedGameMoves() {
    assertMoveOk(move(bId, initialSwitchedState, getSwitchedCodeOperations()));
    assertMoveOk(move(wId, codedSwitchedState, getSwitchedGuessOperations()));
    assertMoveOk(move(bId, guessedSwitchedState, getSwitchedFeedbackOperations()));
  }
  
  @Test
  public void testSwitchedGameMovesWithWrongPlayer() {
    assertHacker(move(wId, initialSwitchedState, getSwitchedCodeOperations()));
    assertHacker(move(bId, codedSwitchedState, getSwitchedGuessOperations()));
    assertHacker(move(wId, guessedSwitchedState, getSwitchedFeedbackOperations()));
  }
  
  @Test
  public void testVerifyWithCorrectGuess() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
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
        new SetTurn(bId),
        new Set(CURRENTMOVE,VERIFY),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w")),
        new SetVisibility(CODE));
    assertMoveOk(move(bId, state, operations));
  }
  
  @Test
  public void testVerifyWithIncorrectGuess() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
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
        new SetTurn(bId),
        new Set(CURRENTMOVE,VERIFY),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w")),
        new SetVisibility(CODE));
    assertMoveOk(move(bId, state, operations));
  }
  
  @Test
  public void testIllegalVerifyWithIncorrectTurn() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1596"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w"))
        .put(CURRENTGAME,2)
        .put(CURRENTMOVE,FEEDBACK)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>of(
        new SetTurn(wId),
        new Set(CURRENTMOVE,VERIFY),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w")),
        new SetVisibility(CODE));
    assertHacker(move(bId, state, operations));
  }
  
  @Test
  public void testIllegalVerifyWithIncorrectVisibility() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1596"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w"))
        .put(CURRENTGAME,2)
        .put(CURRENTMOVE,FEEDBACK)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>of(
        new SetTurn(bId),
        new Set(CURRENTMOVE,VERIFY),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w")),
        new SetVisibility((CODE), ImmutableList.<Integer>of(bId)));
    assertHacker(move(bId, state, operations));
  }
  
  @Test
  public void testIllegalVerifyWithIncorrectMove() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1596"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w"))
        .put(CURRENTGAME,2)
        .put(CURRENTMOVE,FEEDBACK)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>of(
        new SetTurn(bId),
        new Set(CURRENTMOVE,GUESS),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w")),
        new SetVisibility(CODE));
    assertHacker(move(bId, state, operations));
  }
  
  @Test
  public void testIllegalVerifyWithIncorrectFeedbackLength() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1596"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w"))
        .put(CURRENTGAME,2)
        .put(CURRENTMOVE,FEEDBACK)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>of(
        new SetTurn(bId),
        new Set(CURRENTMOVE,VERIFY),
        new Set(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w","3b0w")),
        new SetVisibility(CODE));
    assertHacker(move(bId, state, operations));
  }
  
  @Test
  public void testEndGameWithCorrectGuess() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,2)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>of(
        new SetTurn(wId),
        new Set(CURRENTMOVE, END),
        new EndGame(wId));
    assertMoveOk(move(bId, state, operations));
  }
  
  @Test
  public void testIllegalEndGameWithWrongWinner() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,10)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1598"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","4b0w"))
        .put(CURRENTGAME,2)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>of(
        new SetTurn(wId),
        new EndGame(bId));
    assertHacker(move(bId, state, operations));
  }
  
  @Test
  public void testEndGameWithMaxTurn() {
    Map<String, Object> state = ImmutableMap.<String,Object>builder()
        .put(CODELENGTH, CL)
        .put(MAXTURN,2)
        .put(MAXDIGIT,MD)
        .put(GUESSHISTORY, ImmutableList.<String>of("1597","1596"))
        .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w"))
        .put(CURRENTGAME,2)
        .put(CURRENTMOVE,VERIFY)
        .put(CURRENTTURN,2)
        .put(CODE,"1598")
        .build();
    List<Operation> operations = ImmutableList.<Operation>of(
        new SetTurn(wId),
        new Set(CURRENTMOVE, END),
        new EndGame(bId));
    assertMoveOk(move(bId, state, operations));
  }
    
    @Test
    public void testEndGameWithMaxTurnWithWrongWinner() {
      Map<String, Object> state = ImmutableMap.<String,Object>builder()
          .put(CODELENGTH, CL)
          .put(MAXTURN,2)
          .put(MAXDIGIT,MD)
          .put(GUESSHISTORY, ImmutableList.<String>of("1597","1596"))
          .put(FEEDBACKHISTORY, ImmutableList.<String>of("3b0w","3b0w"))
          .put(CURRENTGAME,2)
          .put(CURRENTMOVE,VERIFY)
          .put(CURRENTTURN,2)
          .put(CODE,"1598")
          .build();
      List<Operation> operations = ImmutableList.<Operation>of(
          new SetTurn(wId),
          new EndGame(wId));
      assertHacker(move(bId, state, operations));
  }
  
}
