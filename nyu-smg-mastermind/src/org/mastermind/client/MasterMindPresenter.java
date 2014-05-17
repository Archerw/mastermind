package org.mastermind.client;

import java.util.List;
import java.util.Map;

import org.game_api.GameApi.Container;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.SetTurn;
import org.game_api.GameApi.UpdateUI;

import com.google.common.base.Optional;

/**
 * The presenter of MasterMind graphics.
 * Use MVP pattern
 * @author Archer
 *
 */
public class MasterMindPresenter {
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
  private final int aiLevel = 5;
  private MasterMindAI ai = new MasterMindAI(aiLevel);
  
  public interface View {
    /**
     * Set the presenter. The view will call certain methods on the presenter.
     * 1) {@link #sendCodeMove} to set code 
     * 2) {@link #sendGuessMove} to guess result
     * 3) {@link #sendFeedbackMove} to give feedback
     * @param masterMindPresenter
     */
    void setPresenter(MasterMindPresenter masterMindPresenter);
    
    /**
     * Set default display for viewer
     * @param state
     */
    void setState(Map<String, Object> state);
    /**
     * Set display for coder in coding state
     * @param state
     */
    void setCoderStateCode(Map<String, Object> state);
    
    /**
     * Set display for coder in feedback state
     * @param state
     */
    void setCoderStateFeedback(Map<String, Object> state);
    
    /**
     * Set display for guesser in guess state
     * @param state
     */
    void setGuesserState(Map<String, Object> state);
    
    /**
     * UI/presenter interaction functions
     * 
     * Presenter first send a signal to the view. The view will collect information from the user,
     * e.g. code, guess or feedback and return to the presenter via related methods.
     * 1) {@link #sendCodeMove} to set code 
     * 2) {@link #sendGuessMove} to guess result
     * 3) {@link #sendFeedbackMove} to give feedback
     * 
     * Since collecting and giving the code is relatively simple, it purely works in viewer's
     * domain. 
     */
    
    /**
     * Send start coding signal. Waiting for a {@link #sendCodeMove} feedback afterward
     * @param code is initial display of code area
     */
    void startCode(String code);
    
    /**
     * Send start guessing signal. Waiting for a {@link #sendGuessMove}feedback afterward
     * @param guess is initial display of guess area
     */
    void startGuess(String guess);
    
    /**
     * Send start feedback signal. Waiting for a {@link #sendFeedbackMove}feedback afterward
     * @param feedback is initial display of guess area
     */
    void startFeedback(String feedback);
    
    /**
     * Send end game info, tell the presenter if the player is the winner
     */
    void sendEndGameInfo(boolean isWin);
  }
  
  private final MasterMindLogic masterMindLogic = new MasterMindLogic();
  private final View view;
  private final Container container;
  /** A viewer doesn't have a color. */
  private Optional<Color> myColor;
  private Color turnOfColor;
  Map<String, Object> state;
  private String guesserId;
  private String coderId;
  private String code = "    ";
  private String guess = "    ";
  private String feedback = "    ";
  private String currentMove = "";
  
  public MasterMindPresenter(View view, Container container) {
    this.view = view;
    this.container = container;
    view.setPresenter(this);
  }
  
  /**
   * Update the presenter and the view
   * @param updateUI
   */
  public void updateUI(UpdateUI updateUI){
    List<String> playerIds = updateUI.getPlayerIds();
    String yourPlayerId = updateUI.getYourPlayerId();
    int yourPlayerIndex = updateUI.getPlayerIndex(yourPlayerId);
    String otherPlayerId = yourPlayerIndex == 0 ? playerIds.get(1) : playerIds.get(0);
    myColor = yourPlayerIndex == 0 ? Optional.of(Color.W)
        : yourPlayerIndex == 1 ? Optional.of(Color.B) : Optional.<Color>absent();
    
    this.state = updateUI.getState();
    
    if (state.isEmpty()){
      if (myColor.isPresent() && myColor.get().isWhite()){
        sendInitialMove(playerIds);
      }
      return;
    }
    
    for (Operation operation : updateUI.getLastMove()) {
      if (operation instanceof SetTurn) {
        turnOfColor = Color.values()[playerIds.indexOf(((SetTurn) operation).getPlayerId())];
      }
    }
    
    if (updateUI.isAiPlayer()) {
      // TODO: implement AI in a later HW!
      
      //Determine role
      int currentGame = (Integer)state.get(CURRENTGAME);
      if ((currentGame+yourPlayerIndex) % 2 == 0){
        this.guesserId = yourPlayerId;
        this.coderId = otherPlayerId;
      } else {
        this.coderId = yourPlayerId;
        this.guesserId = otherPlayerId;
      }
      //Player Display
      
      if (this.coderId == yourPlayerId){
        //Coder
        if (CODE == state.get(CURRENTMOVE)) {
          // Code state
          this.code = "    ";
          view.setCoderStateCode(state);
          if (isMyTurn()){
            currentMove = CODE;
            //AI CODE
            this.sendCodeMove(ai.generateCode((Integer)state.get(CODELENGTH),
                (Integer)state.get(MAXDIGIT)));
          }
        } else if (VERIFY == state.get(CURRENTMOVE)){
          //Subgame end and verify is done
          currentMove = VERIFY;
          view.setCoderStateFeedback(state);
          if (isMyTurn()){
            if ((Integer)state.get(CURRENTGAME) == 1){
              this.sendSwitchcoderMove();
            } else {
              this.sendEndGameMove();
            }
          }
        } else if (FEEDBACK == state.get(CURRENTMOVE)){
          //TODO calculate feedback Directly
          currentMove = FEEDBACK;
          this.feedback = "    ";
          view.setCoderStateFeedback(state);
          if (isMyTurn()){
            //AI FEEDBACK
            String code = (String) state.get(CODE);
            List<String> guessHistoryList = (List<String>) state.get(GUESSHISTORY);
            String guess = guessHistoryList.get(guessHistoryList.size()-1);
            String feedback = CodeFeedback.getValidFeedback(code, guess);
            this.sendFeedbackMove(feedback);
          }
        } else {
          view.setCoderStateFeedback(state);
        }
      } else {
      //Guesser
        this.guess = "    ";
        currentMove = GUESS;
        view.setGuesserState(state);
        if (isMyTurn()){
          //AI Guess
          List<String> guessHistory = (List<String>)state.get(GUESSHISTORY);
          List<String> feedbackHistory = (List<String>)state.get(FEEDBACKHISTORY);
          if (guessHistory.size() == 0) {
            ai.init();
          } else {
            ai.filter(guessHistory, feedbackHistory);
          }
          this.sendGuessMove(ai.generateGuess());
        }
      }
      return;
    }
    
    if (updateUI.isViewer()) {
      view.setState(state);
      return;
    }
    //Determine role
    int currentGame = (Integer)state.get(CURRENTGAME);
    if ((currentGame+yourPlayerIndex) % 2 == 0){
      this.guesserId = yourPlayerId;
      this.coderId = otherPlayerId;
    } else {
      this.coderId = yourPlayerId;
      this.guesserId = otherPlayerId;
    }
    //Player Display
    
    if (this.coderId == yourPlayerId){
      //Coder
      if (CODE == state.get(CURRENTMOVE)) {
        // Code state
        this.code = "    ";
        view.setCoderStateCode(state);
        if (isMyTurn()){
          currentMove = CODE;
          view.startCode(code);
        }
      } else if (VERIFY == state.get(CURRENTMOVE)){
        //Subgame end and verify is done
        currentMove = VERIFY;
        view.setCoderStateFeedback(state);
        if (isMyTurn()){
          if ((Integer)state.get(CURRENTGAME) == 1){
            this.sendSwitchcoderMove();
          } else {
            this.sendEndGameMove();
          }
        }
      } else if (FEEDBACK == state.get(CURRENTMOVE)){
        currentMove = FEEDBACK;
        view.setCoderStateFeedback(state);
        if (isMyTurn()){
          String code = (String) state.get(CODE);
          List<String> guessHistoryList = (List<String>) state.get(GUESSHISTORY);
          String guess = guessHistoryList.get(guessHistoryList.size()-1);
          this.feedback = CodeFeedback.getValidFeedback(code, guess);
          view.startFeedback(feedback);
        }
      } else {
        view.setCoderStateFeedback(state);
        
      }
      if (END == state.get(CURRENTMOVE)){
        view.sendEndGameInfo(isWin(yourPlayerId));
      }
    } else {
    //Guesser
      this.guess = "    ";
      currentMove = GUESS;
      view.setGuesserState(state);
      if (isMyTurn()){
        view.startGuess(guess);
      }
      if (END == state.get(CURRENTMOVE)){
        view.sendEndGameInfo(isWin(yourPlayerId));
      }
    }
  }
  

  private boolean isMyTurn() {
    return myColor.isPresent() && myColor.get() == turnOfColor;
  }
  
  /**
   * Send a code signal to presenter. Must be called only in CODE state
   * @param code
   */
  public void sendCodeMove(String code){
    check(isMyTurn() && currentMove == CODE);
    int maxDigit = (Integer)state.get(MAXDIGIT);
    check(masterMindLogic.checkValidCode(code, maxDigit));
    this.sendCodeOperation(coderId, guesserId, code);
  }
  
  /**
   * Send a guess signal to presenter. Must be called only in GUESS state.
   * @param guess
   */
  public void sendGuessMove(String guess){
    check(isMyTurn() && currentMove == GUESS);
    int maxDigit = (Integer)state.get(MAXDIGIT);
    check(masterMindLogic.checkValidCode(guess, maxDigit));
    this.sendGuessOperation(coderId, guesserId, guess, state);
  }
  
  /**
   * Send a feedback signal to presenter. Must be called only in FEEDBACK state
   * @param feedback
   */
  public void sendFeedbackMove(String feedback){
    check(isMyTurn() && currentMove == FEEDBACK);
    check(masterMindLogic.checkValidFeedback(feedback));
    if (feedback.equals("4b0w") 
        || ((List<String>)state.get(GUESSHISTORY)).size() == (Integer) state.get(MAXTURN)){
      this.sendFeedbackMoveVerify(feedback);
    } else {
      this.sendFeedbackMoveContinue(feedback);
    }
  }
  
  private void sendFeedbackMoveContinue(String feedback){
    this.sendFeedbackOperationContinue(coderId, guesserId, feedback, state);
  }
  
  private void sendFeedbackMoveVerify(String feedback){
    this.sendFeedbackOperationVerify(coderId, guesserId, feedback, state);
  }
  
  private void sendSwitchcoderMove(){
    check(isMyTurn() && currentMove == VERIFY);
    this.sendSwitchcoderOperation(coderId, guesserId, state);
  }
  
  @SuppressWarnings("unchecked")
  private void sendEndGameMove(){
    check(isMyTurn() && currentMove == VERIFY);
    String winnerId = coderId;
    List<String> feedbackHistory = (List<String>) state.get(FEEDBACKHISTORY);
    String lastFeedback = feedbackHistory.get(feedbackHistory.size()-1);
    if (lastFeedback.equals("4b0w")) {
      winnerId = guesserId;
    }
    this.sendEndGameOperation(coderId, guesserId, winnerId);
  }
  
  private boolean isWin(String yourPlayerId) {
  	String winnerId = coderId;
    List<String> feedbackHistory = (List<String>) state.get(FEEDBACKHISTORY);
    String lastFeedback = feedbackHistory.get(feedbackHistory.size()-1);
    if (lastFeedback.equals("4b0w")) {
      winnerId = guesserId;
    }
    return (yourPlayerId == winnerId);
  }
  
  /**
   * private methods that send move to container
   * 
   */
  private void sendInitialMove(List<String> playerIds) {
    container.sendMakeMove(masterMindLogic.getInitialOperations(playerIds));
  }
  
  private void sendCodeOperation(String coderId, String guesserId, String code){
    container.sendMakeMove(masterMindLogic.getCodeOperations(coderId, guesserId, code));
  }
  
  private void sendGuessOperation(String coderId, String guesserId, String guess,
      Map<String, Object> lastState) {
    container.sendMakeMove(masterMindLogic.getGuessOperation(
        coderId, guesserId, guess, lastState));
  }
  
  private void sendFeedbackOperationContinue(String coderId, String guesserId, String feedback,
      Map<String, Object> lastState) {
    container.sendMakeMove(masterMindLogic.getFeedbackOperationContinue(
        coderId, guesserId, feedback, lastState));
  }
  
  private void sendFeedbackOperationVerify(String coderId, String guesserId, String feedback,
      Map<String, Object> lastState) {
    container.sendMakeMove(masterMindLogic.getFeedbackOperationVerify(
        coderId, guesserId, feedback, lastState));
  }
  
  private void sendSwitchcoderOperation(String coderId, String guesserId, 
      Map<String, Object> lastState) {
    container.sendMakeMove(masterMindLogic.getSwitchcoderOperation(
        coderId, guesserId, lastState));
  }
  
  private void sendEndGameOperation(String coderId, String guesserId, String winnerId) {
    container.sendMakeMove(masterMindLogic.getEndGameOperation(
        coderId, guesserId, winnerId));
  }
  
  private void check(boolean val) {
    if (!val) {
      throw new IllegalArgumentException();
    }
  }
}
