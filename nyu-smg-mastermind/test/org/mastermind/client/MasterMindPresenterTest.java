package org.mastermind.client;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mastermind.client.GameApi.Container;
import org.mastermind.client.GameApi.Operation;
import org.mastermind.client.GameApi.SetTurn;
import org.mastermind.client.GameApi.UpdateUI;
import org.mastermind.client.MasterMindPresenter.View;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests for {@link MasterMindPresenter}
 * Test plan:
 * We will go over all possible states transaction:
 * 1) empty state -> code state
 * 2) code state -> guess state
 * 3) guess state -> feedback state
 * 4) feedback state -> guess state
 * 5) feedback state -> verify state
 * 6) verify state -> Switch initial state
 * 7) verify state -> endGame
 * 
 * we will test all the transaction for W, B and Viewer
 * 
 * We will also test that view call the following function
 * 
 * 1) void sendCodeMove(String code)
 * 2) void sendGuessMove(String guess)
 * 3) void sendFeedbackMove(String feedback)
 * 
 * @author Jinxuan Wu
 *
 */
public class MasterMindPresenterTest {
  private MasterMindPresenter masterMindPresenter;
  private View mockView;
  private Container mockContainer;
  private MasterMindLogic masterMindLogic = new MasterMindLogic();
  
  private final int viewerId = GameApi.VIEWER_ID;
  private final int wId = 41;
  private final int bId = 42;
  private final ImmutableList<Integer> playerIds = ImmutableList.of(wId, bId);
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
  
  private final Map<String, Object> guessedCorrectState = ImmutableMap.<String,Object>builder()
      .put(CODELENGTH, CL)
      .put(MAXTURN,MT)
      .put(MAXDIGIT,MD)
      .put(GUESSHISTORY, ImmutableList.<String>of("1598"))
      .put(FEEDBACKHISTORY, ImmutableList.<String>of())
      .put(CURRENTGAME,1)
      .put(CURRENTMOVE,FEEDBACK)
      .put(CURRENTTURN,1)
      .put(CODE,"1598")
      .build();
  
  private final Map<String, Object> guessedOutOfMaxturnState = ImmutableMap.<String,Object>builder()
      .put(CODELENGTH, CL)
      .put(MAXTURN,1)
      .put(MAXDIGIT,MD)
      .put(GUESSHISTORY, ImmutableList.<String>of("1234"))
      .put(FEEDBACKHISTORY, ImmutableList.<String>of())
      .put(CURRENTGAME,1)
      .put(CURRENTMOVE,FEEDBACK)
      .put(CURRENTTURN,1)
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
  
  Map<String, Object> SwitchStateWithCorrectGuess = ImmutableMap.<String,Object>builder()
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
  
  Map<String, Object> SwitchStateWithIncorrectGuess = ImmutableMap.<String,Object>builder()
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
  
  Map<String, Object> EndGameStateWithCorrectGuess = ImmutableMap.<String,Object>builder()
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
  
  Map<String, Object> EndGameStateWithWrongGuess = ImmutableMap.<String,Object>builder()
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
  
  @Before
  public void runBefore() {
    mockView = Mockito.mock(View.class);
    mockContainer = Mockito.mock(Container.class);
    masterMindPresenter = new MasterMindPresenter(mockView, mockContainer);
    verify(mockView).setPresenter(masterMindPresenter);
  }

  @After
  public void runAfter() {
    // This will ensure I didn't forget to declare any extra interaction the mocks have.
    verifyNoMoreInteractions(mockContainer);
    verifyNoMoreInteractions(mockView);
  }
  
  @Test
  public void testEmptyStateForW() {
    masterMindPresenter.updateUI(createUpdateUI(wId,0,emptyState));
    verify(mockContainer).sendMakeMove(masterMindLogic.getInitialOperations(playerIds));
  }
  
  @Test
  public void testEmptyStateForB() {
    masterMindPresenter.updateUI(createUpdateUI(bId,0,emptyState));
  }
  
  @Test
  public void testEmptyStateForViewer() {
    masterMindPresenter.updateUI(createUpdateUI(viewerId,0,emptyState));
  }
  
  @Test
  public void testCodeStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,initialState));
    verify(mockView).setCoderStateCode(initialState);
    verify(mockView).startCode("    ");
    masterMindPresenter.sendCodeMove("1598");
    verify(mockContainer).sendMakeMove(masterMindLogic.getCodeOperations(wId, bId, "1598"));
  }
  
  @Test
  public void testCodeStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,wId,initialState));
    verify(mockView).setGuesserState(initialState);
  }
  
  @Test
  public void testCodeStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,wId,initialState));
    verify(mockView).setState(initialState);
  }
  
  @Test
  public void testSwitchedCodeStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,bId,initialSwitchedState));
    verify(mockView).setGuesserState(initialSwitchedState);
  }
  
  @Test
  public void testSwitchedCodeStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,bId,initialSwitchedState));
    verify(mockView).setCoderStateCode(initialSwitchedState);
    verify(mockView).startCode("    ");
    masterMindPresenter.sendCodeMove("1598");
    verify(mockContainer).sendMakeMove(masterMindLogic.getCodeOperations(bId, wId, "1598"));
  }
  
  @Test
  public void testSwitchedCodeStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,wId,initialSwitchedState));
    verify(mockView).setState(initialSwitchedState);
  }
  
  @Test
  public void testGuessStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,bId,codedState));
    verify(mockView).setCoderStateFeedback(codedState);
  }
  
  @Test
  public void testGuessStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,bId,codedState));
    verify(mockView).setGuesserState(codedState);
    verify(mockView).startGuess("    ");
    masterMindPresenter.sendGuessMove("1234");
    verify(mockContainer).sendMakeMove(masterMindLogic.getGuessOperation(wId, bId, "1234", codedState));
  }
  
  @Test
  public void testGuessStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,bId,codedState));
    verify(mockView).setState(codedState);
  }
  
  @Test
  public void testSwitchedGuessStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,codedSwitchedState));
    verify(mockView).setGuesserState(codedSwitchedState);
    verify(mockView).startGuess("    ");
    masterMindPresenter.sendGuessMove("1234");
    verify(mockContainer).sendMakeMove(masterMindLogic.getGuessOperation(bId, wId, "1234", codedSwitchedState));
  }
  
  @Test
  public void testSwitchedGuessStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,wId,codedSwitchedState));
    verify(mockView).setCoderStateFeedback(codedSwitchedState);
  }
  
  @Test
  public void testSwitchedGuessStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,wId,codedSwitchedState));
    verify(mockView).setState(codedSwitchedState);
  }
  
  @Test
  public void testMoreGuessStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,bId,returnedState));
    verify(mockView).setCoderStateFeedback(returnedState);
  }
  
  @Test
  public void testMoreGuessStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,bId,returnedState));
    verify(mockView).setGuesserState(returnedState);
    verify(mockView).startGuess("    ");
    masterMindPresenter.sendGuessMove("1234");
    verify(mockContainer).sendMakeMove(masterMindLogic.getGuessOperation(wId, bId, "1234", returnedState));
  }
  
  @Test
  public void testMoreGuessStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,bId,returnedState));
    verify(mockView).setState(returnedState);
  }
  
  @Test
  public void testMoreSwitchedGuessStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,returnedSwitchedState));
    verify(mockView).setGuesserState(returnedSwitchedState);
    verify(mockView).startGuess("    ");
    masterMindPresenter.sendGuessMove("1234");
    verify(mockContainer).sendMakeMove(masterMindLogic.getGuessOperation(bId, wId, "1234", returnedSwitchedState));
  }
  
  @Test
  public void testMoreSwitchedGuessStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,wId,returnedSwitchedState));
    verify(mockView).setCoderStateFeedback(returnedSwitchedState);
  }
  
  @Test
  public void testMoreSwitchedGuessStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,wId,returnedSwitchedState));
    verify(mockView).setState(returnedSwitchedState);
  }
  
  @Test
  public void testFeedbackStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,guessedState));
    verify(mockView).setCoderStateFeedback(guessedState);
    verify(mockView).startFeedback("    ");
    masterMindPresenter.sendFeedbackMove("1b0w");
    verify(mockContainer).sendMakeMove(masterMindLogic.getFeedbackOperationContinue(wId, bId, "1b0w", guessedState));
  }
  
  @Test
  public void testFeedbackStateWithCorrectGuessForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,guessedCorrectState));
    verify(mockView).setCoderStateFeedback(guessedCorrectState);
    verify(mockView).startFeedback("    ");
    masterMindPresenter.sendFeedbackMove("4b0w");
    verify(mockContainer).sendMakeMove(masterMindLogic.getFeedbackOperationVerify(wId, bId, "4b0w", guessedCorrectState));
  }
  
  @Test
  public void testFeedbackStateWithIncorrectGuessForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,guessedOutOfMaxturnState));
    verify(mockView).setCoderStateFeedback(guessedOutOfMaxturnState);
    verify(mockView).startFeedback("    ");
    masterMindPresenter.sendFeedbackMove("1b0w");
    verify(mockContainer).sendMakeMove(masterMindLogic.getFeedbackOperationVerify(wId, bId, "1b0w", guessedOutOfMaxturnState));
  }
  
  @Test
  public void testFeedbackStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,wId,guessedState));
    verify(mockView).setGuesserState(guessedState);
  }
  
  @Test
  public void testFeedbackStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,wId,guessedState));
    verify(mockView).setState(guessedState);
  }
  
  @Test
  public void testSwitchedFeedbackStateForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,bId,guessedSwitchedState));
    verify(mockView).setGuesserState(guessedSwitchedState);
  }
  
  @Test
  public void testSwitchedFeedbackStateForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,bId,guessedSwitchedState));
    verify(mockView).setCoderStateFeedback(guessedSwitchedState);
    verify(mockView).startFeedback("    ");
    masterMindPresenter.sendFeedbackMove("1b0w");
    verify(mockContainer).sendMakeMove(masterMindLogic.getFeedbackOperationContinue(bId, wId, "1b0w", guessedState));
  }
  
  @Test
  public void testSwitchedFeedbackStateForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,bId,guessedSwitchedState));
    verify(mockView).setState(guessedSwitchedState);
  }
  
  @Test
  public void testVerifyStateSwitchCorrectForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,SwitchStateWithCorrectGuess));
    verify(mockView).setCoderStateFeedback(SwitchStateWithCorrectGuess);
    verify(mockContainer).sendMakeMove(masterMindLogic.getSwitchcoderOperation(
        wId, bId, SwitchStateWithCorrectGuess));
  }
  
  @Test
  public void testVerifyStateSwitchCorrectForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,wId,SwitchStateWithCorrectGuess));
    verify(mockView).setGuesserState(SwitchStateWithCorrectGuess);
  }
  
  @Test
  public void testVerifyStateSwitchCorrectForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,wId,SwitchStateWithCorrectGuess));
    verify(mockView).setState(SwitchStateWithCorrectGuess);
  }
  
  @Test
  public void testVerifyStateSwitchIncorrectForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,wId,SwitchStateWithIncorrectGuess));
    verify(mockView).setCoderStateFeedback(SwitchStateWithIncorrectGuess);
    verify(mockContainer).sendMakeMove(masterMindLogic.getSwitchcoderOperation(
        wId, bId, SwitchStateWithIncorrectGuess));
  }
  
  @Test
  public void testVerifyStateSwitchIncorrectForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,wId,SwitchStateWithIncorrectGuess));
    verify(mockView).setGuesserState(SwitchStateWithIncorrectGuess);
  }
  
  @Test
  public void testVerifyStateSwitchIncorrectForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,wId,SwitchStateWithIncorrectGuess));
    verify(mockView).setState(SwitchStateWithIncorrectGuess);
  }
  
  @Test
  public void testVerifyStateEndCorrectForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,bId,EndGameStateWithCorrectGuess));
    verify(mockView).setGuesserState(EndGameStateWithCorrectGuess);
  }
  
  @Test
  public void testVerifyStateEndCorrectForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,bId,EndGameStateWithCorrectGuess));
    verify(mockView).setCoderStateFeedback(EndGameStateWithCorrectGuess);
    verify(mockContainer).sendMakeMove(masterMindLogic.getEndGameOperation(
        bId, wId, wId));
  }
  
  @Test
  public void testVerifyStateEndCorrectForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,bId,EndGameStateWithCorrectGuess));
    verify(mockView).setState(EndGameStateWithCorrectGuess);
  }
  
  @Test
  public void testVerifyStateEndIncorrectForW(){
    masterMindPresenter.updateUI(createUpdateUI(wId,bId,EndGameStateWithWrongGuess));
    verify(mockView).setGuesserState(EndGameStateWithWrongGuess);
  }
  
  @Test
  public void testVerifyStateEndIncorrectForB(){
    masterMindPresenter.updateUI(createUpdateUI(bId,bId,EndGameStateWithWrongGuess));
    verify(mockView).setCoderStateFeedback(EndGameStateWithWrongGuess);
    verify(mockContainer).sendMakeMove(masterMindLogic.getEndGameOperation(
        bId, wId, bId));
  }
  
  @Test
  public void testVerifyStateEndIncorrectForViewer(){
    masterMindPresenter.updateUI(createUpdateUI(viewerId,bId,EndGameStateWithWrongGuess));
    verify(mockView).setState(EndGameStateWithWrongGuess);
  }
  
  private UpdateUI createUpdateUI(
      int yourPlayerId, int turnOfPlayerId, Map<String, Object> state) {
    // Our UI only looks at the current state
    // (we ignore: lastState, lastMovePlayerId, playerIdToNumberOfTokensInPot)
    return new UpdateUI(yourPlayerId, playersInfo, state,
        emptyState, // we ignore lastState
        ImmutableList.<Operation>of(new SetTurn(turnOfPlayerId)),
        0,
        ImmutableMap.<Integer, Integer>of());
  }
  
  
}
