package org.mastermind.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mastermind.client.GameApi.Delete;
import org.mastermind.client.GameApi.EndGame;
import org.mastermind.client.GameApi.Operation;
import org.mastermind.client.GameApi.Set;
import org.mastermind.client.GameApi.SetTurn;
import org.mastermind.client.GameApi.SetVisibility;
import org.mastermind.client.GameApi.VerifyMove;
import org.mastermind.client.GameApi.VerifyMoveDone;
import org.mastermind.client.Color;
import org.mastermind.client.CodeFeedback;

import com.google.common.collect.ImmutableList;

public class MasterMindLogic {
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
  
  /**
   * VerifyMove validation
   * If not exception thrown then the move is legal
   * @param verifyMove
   * @return
   */
  public VerifyMoveDone verify(VerifyMove verifyMove) {
    try {
      checkMoveIsLegal(verifyMove);
      return new VerifyMoveDone();
    } catch (Exception e) {
      return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
    }
  }
  
  /**
   * Check move if it is legal.
   * Checking is based on condition of lastState
   * @param verifyMove
   */
  private void checkMoveIsLegal(VerifyMove verifyMove){
    Map<String, Object> lastState = verifyMove.getLastState();
    // We use SetTurn, so we don't need to check that the correct player did the move.
    // However, we do need to check the first move is done by the white player (and then in the
    // first MakeMove we'll send SetTurn which will guarantee the correct player send MakeMove).
    if (lastState.isEmpty()){
      //Test Initial Move Here
      check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
      checkValidInitialMove(verifyMove);
    } else if (lastState.get(CURRENTMOVE) == CODE){
      //Test Code phase
      checkValidCodeMove(verifyMove);
    } else if (lastState.get(CURRENTMOVE) == GUESS){
      //Test Guess phase
      checkValidGuessMove(verifyMove);
    } else if (lastState.get(CURRENTMOVE) == FEEDBACK){
      //Test Feedback phase
      checkValidFeedBackMove(verifyMove);
    } else if (lastState.get(CURRENTMOVE) == VERIFY){
      //Test Validate phase
      checkValidVerifyMove(verifyMove);
    } else {
      check(false,"UNKNOWN CURRENTMOVE");
    }
  }
  
  private void checkValidInitialMove(VerifyMove verifyMove){
    List<Operation> lastMove = verifyMove.getLastMove();
    List<Operation> initMove = getInitialOperations(verifyMove.getPlayerIds());
    check(initMove.equals(lastMove), initMove, lastMove);
  }
  
  private void checkValidCodeMove(VerifyMove verifyMove){
    /**
     * Legal Move should be (assume lastMoveId is wId)
     * 1 add(new SetTurn(bId))
     * 2 add(new Set(CURRENTMOVE,GUESS))
     * 3 add(new Set(CODE,"1598")) code length should match CODELENGTH IN LAST STATE, CODE must use
     *   legal Digit specified by MAXDIGIT
     * 4 add(new SetVisibility(CODE,ImmutableList.of(wId)))
    */
    List<Operation> lastMove = verifyMove.getLastMove();
    Map<String, Object> lastState = verifyMove.getLastState();
    int coderId = verifyMove.getLastMovePlayerId();
    List<Integer> ids = verifyMove.getPlayerIds();
    int guesserId = getOtherPlayerId(ids,coderId);
    String code = (String)((Set)lastMove.get(2)).getValue();
    List<Operation> codeMove = getCodeOperations(coderId, guesserId, code);
    check(lastMove.equals(codeMove),codeMove,lastMove);
    check(code.length() == (Integer)lastState.get(CODELENGTH));
    int maxDigit = (Integer)lastState.get(MAXDIGIT);
    check(checkValidCode(code,maxDigit));
  }
  
  boolean checkValidCode(String code, int maxDigit){
    try {
      for (char c : code.toCharArray()){
        check(c >= '0');
        check(c <= '0'+maxDigit);
      };
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  
  @SuppressWarnings("unchecked")
  private void checkValidGuessMove(VerifyMove verifyMove){
    /**
     * Legal Move should be (assume lastMoveId is wId)
     * 1 add(new SetTurn(bId)) 
     * 2 add(new Set(CURRENTMOVE,FEEDBACK))
     * 3 add(new Set(CURRENTTURN,x+1)), where x is CURRENTTURN in last state
     * 4 add(new Set(GUESSHISTORY,y+guess))), where y is the GUESSHISTORY in last state
     * and guess should be a valid guess
     */
    List<Operation> lastMove = verifyMove.getLastMove();
    Map<String, Object> lastState = verifyMove.getLastState();
    int guesserId = verifyMove.getLastMovePlayerId();
    List<Integer> ids = verifyMove.getPlayerIds();
    int coderId = getOtherPlayerId(ids,guesserId);
    List<String> guessHistory = (List<String>)(((Set)lastMove.get(3)).getValue());
    String guess = guessHistory.get(guessHistory.size()-1);
    List<Operation> guessMove = getGuessOperation(coderId, guesserId, guess, lastState);
    check(lastMove.equals(guessMove),guessMove,lastMove);
    check(guess.length() == (Integer)lastState.get(CODELENGTH));
    int maxDigit = (Integer)lastState.get(MAXDIGIT);
    check(checkValidCode(guess,maxDigit));
  }
  
  @SuppressWarnings("unchecked")
  private void checkValidFeedBackMove(VerifyMove verifyMove){
    /**
     * Legal Move should be (assume lastMoveId is wId)
     * Case A: the game is not finished.
     * 1 add(new SetTurn(bId))
     * 2 add(new Set(CURRENTMOVE,GUESS))
     * 3 add(new Set(FEEDBACKHISTORY,x+feedback)), where x is FEEDBACKHISTORY and feedback is not 
     *   checked now. However the format should be "nbmw", where n+m <= 4
     * Case B: current game is end and verification should be done. Coder uncover the code and let
     * guesser do verification next move.
     * 1 add (new SetTurn(wId))
     * 2 add(new Set(CURRENTMOVE,VERIFY))
     * 3 add (new Set(FEEDBACKHISTORY, x+feedback), where x is FEEDBACKHISTORY and entire feedback 
     *   is checked now.
     * 4 add (new SetVisibility(CODE,ImmutableList.of(bId))
     * 
     */
    List<Operation> lastMove = verifyMove.getLastMove();
    Map<String, Object> lastState = verifyMove.getLastState();
    int coderId = verifyMove.getLastMovePlayerId();
    List<Integer> ids = verifyMove.getPlayerIds();
    int guesserId = getOtherPlayerId(ids,coderId);
    int currentTurn = (Integer)lastState.get(CURRENTTURN);
    int maxTurn = (Integer)lastState.get(MAXTURN);
    
    
    List<String> feedbackHistory = (List<String>)(((Set)lastMove.get(2)).getValue());
    String feedback = feedbackHistory.get(feedbackHistory.size()-1);
    check(feedback.length() == (Integer)lastState.get(CODELENGTH));
    check(checkValidFeedback(feedback));
    
    List<Operation> feedbackMove = getFeedbackOperationContinue(coderId, guesserId, feedback,
        lastState);
    if (currentTurn < maxTurn && !feedback.equals("4b0w")){
      //Nothing change for defaultfeedbackMove
    } else {
      feedbackMove = getFeedbackOperationVerify(coderId, guesserId, feedback,
          lastState);
      //Verify previous feedback
    } 
    check(lastMove.equals(feedbackMove),feedbackMove,lastMove);
  }
  
  @SuppressWarnings("unchecked")
  private void checkValidVerifyMove(VerifyMove verifyMove){
    /**
     * Legal Move should be (assume lastMoveId is wId)
     * Case A: current game is end and role should switch between players
     * 1 add(new SetTurn(bId))
     * 2 add(new Set(GUESSHISTORY, ImmutableList.<String>of()))
     * 3 add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()))
     * 4 add(new Set(CURRENTGAME,x+1)) where x is the CURRENTGAME in lastState
     * 5 add(new Set(MAXTURN,y)) where y is the CURRENTTURN is lastState
     * 6 add(new Set(CURRENTMOVE,CODE))
     * 7 add(new Set(CURRENTTURN,0))
     * 8 add(new Delete(CODE))
     * Case B: current game is end and both player challenged. Entire game end. (bId feedback)
     * 1 add(new SetTurn(bId)) 
     * 2 add(new EndGame(winnerId))
     * 
     */
    List<Operation> lastMove = verifyMove.getLastMove();
    Map<String, Object> lastState = verifyMove.getLastState();
    int coderId = verifyMove.getLastMovePlayerId();
    List<Integer> ids = verifyMove.getPlayerIds();
    int guesserId = getOtherPlayerId(ids,coderId);
    String code = (String)lastState.get("CODE");
    List<String> guessHistory = (List<String>) lastState.get(GUESSHISTORY); 
    List<String> feedbackHistory = (List<String>) lastState.get(FEEDBACKHISTORY);
    //Verify Feedback is correct
    checkGuessAndFeedbackMatch(guessHistory,feedbackHistory,code);
    
    List<Operation> validateMove = getSwitchcoderOperation(coderId, guesserId, lastState);
    int lastCurrentGame = (Integer)lastState.get(CURRENTGAME);
    
    if (lastCurrentGame == 2){
      String lastFeedback = feedbackHistory.get(feedbackHistory.size()-1);
      int winnerId = coderId;
      if (lastFeedback.equals("4b0w")) {
        winnerId = guesserId;
      }
      validateMove = getEndGameOperation(coderId, guesserId, winnerId);
    }
    
    check(lastMove.equals(validateMove),validateMove,lastMove);
    
  }
  
  /**
   * Helper function to get the other player id
   */
  
  private int getOtherPlayerId(List<Integer> ids, int cId) {
    int result = 0;
    for (Integer id: ids){
      if (id != cId){
        result = id;
        break;
      }
    }
    return result;
  }
  /**
   * Helper function for validating feedback
   * @param feedback
   */
  boolean checkValidFeedback(String feedback){
    try {
	    check(feedback.length() == 4);
	    check(feedback.charAt(1) == 'b');
	    check(feedback.charAt(3) == 'w');
	    int b = feedback.charAt(0) - '0';
	    int w = feedback.charAt(2) -'0';
	    check(b <= CL && b >= 0);
	    check(w <= CL && w >= 0);
	    check(b+w <=CL && b+w >= 0);
    } catch (Exception e) {
	    return false;
    }
    return true;
    
  }
  
  /**
   * Helper function for checking all the feedback is valid
   * @param guessHistory
   * @param feedbackHistory
   * @param code
   */
  private void checkGuessAndFeedbackMatch(List<String> guessHistory, List<String> feedbackHistory,
      String code){
    check(guessHistory.size() == feedbackHistory.size(),"hisotry size mismatch",guessHistory,
        feedbackHistory);
    Iterator<String> git = guessHistory.iterator();
    Iterator<String> fit = feedbackHistory.iterator();
    while (git.hasNext() || fit.hasNext()){
      String g = git.next();
      String f = fit.next();
      String gf = CodeFeedback.getValidFeedback(code,g);
      check(gf.equals(f),"hacked feedback",g,f,gf);
    }
  }
  
  /**
   * Helper function to get initial operation
   * @param playerIds
   * @return
   */
  List<Operation> getInitialOperations(List<Integer> playerIds) {
    int wId = playerIds.get(0);
    List<Operation> init = new ArrayList<Operation>();
    init.add(new SetTurn(wId));
    init.add(new Set(CODELENGTH, CL));
    init.add(new Set(MAXTURN,MT));
    init.add(new Set(MAXDIGIT,MD));
    init.add(new Set(GUESSHISTORY, ImmutableList.<String>of()));
    init.add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()));
    init.add(new Set(CURRENTGAME,1));
    init.add(new Set(CURRENTMOVE,CODE));
    init.add(new Set(CURRENTTURN,0));
    return init;
  }
  
  /**
   * helper function to get code operation
   * @param coderId
   * @param guesserId
   * @param code
   * @return
   */
  List<Operation> getCodeOperations(int coderId, int guesserId, String code) {
    List<Operation> codeMove = new ArrayList<Operation>();
    codeMove.add(new SetTurn(guesserId));
    codeMove.add(new Set(CURRENTMOVE,GUESS));
    codeMove.add(new Set(CODE,code));
    codeMove.add(new SetVisibility(CODE,ImmutableList.of(coderId)));
    return codeMove;
  }
  
  /**
   * Helper function to get guess operation
   * @param coderId
   * @param guesserId
   * @param guess
   * @param lastState
   * @return
   */
  @SuppressWarnings("unchecked")
  List<Operation> getGuessOperation(int coderId, int guesserId, String guess,
       Map<String, Object> lastState) {
    List<Operation> guessMove = new ArrayList<Operation>();
    guessMove.add(new SetTurn(coderId));
    guessMove.add(new Set(CURRENTMOVE,FEEDBACK));
    guessMove.add(new Set(CURRENTTURN,(Integer)lastState.get(CURRENTTURN)+1));
    List<String> guessHistory = new ArrayList<String>();
    List<String> lastGuessHistory = (List<String>)(lastState.get(GUESSHISTORY));
    for (Iterator<String> it = lastGuessHistory.iterator(); it.hasNext();){
      guessHistory.add(it.next());
    }
    guessHistory.add(guess);
    guessMove.add(new Set(GUESSHISTORY,guessHistory));
    return guessMove;
  }
  
  /**
   * Helper function to get feedback operation when guess is continueing
   * @param coderId
   * @param guesserId
   * @param feedback
   * @param lastState
   * @return
   */
  @SuppressWarnings("unchecked")
  List<Operation> getFeedbackOperationContinue(int coderId, int guesserId, String feedback,
      Map<String, Object> lastState) {
    List<Operation> guessMove = new ArrayList<Operation>();
    guessMove.add(new SetTurn(guesserId));
    guessMove.add(new Set(CURRENTMOVE,GUESS));
    List<String> feedbackHistory = new ArrayList<String>();
    List<String> lastFeedbackHistory = (List<String>)(lastState.get(FEEDBACKHISTORY));
    for (Iterator<String> it = lastFeedbackHistory.iterator(); it.hasNext();){
      feedbackHistory.add(it.next());
    }
    feedbackHistory.add(feedback);
    guessMove.add(new Set(FEEDBACKHISTORY,feedbackHistory));
    return guessMove;
  }
  
  /**
   * Get feedback operation when guess is over
   * @param coderId
   * @param guesserId
   * @param feedback
   * @param lastState
   * @return
   */
  @SuppressWarnings("unchecked")
  List<Operation> getFeedbackOperationVerify(int coderId, int guesserId, String feedback,
      Map<String, Object> lastState) {
    List<Operation> guessMove = new ArrayList<Operation>();
    guessMove.add(new SetTurn(coderId));
    guessMove.add(new Set(CURRENTMOVE,VERIFY));
    List<String> feedbackHistory = new ArrayList<String>();
    List<String> lastFeedbackHistory = (List<String>)(lastState.get(FEEDBACKHISTORY));
    for (Iterator<String> it = lastFeedbackHistory.iterator(); it.hasNext();){
      feedbackHistory.add(it.next());
    }
    feedbackHistory.add(feedback);
    guessMove.add(new Set(FEEDBACKHISTORY,feedbackHistory));
    //SetVisibleToAll
    guessMove.add(new SetVisibility(CODE));
    return guessMove;
  }
  
  /**
   * Switch coder operation
   * @param coderId
   * @param guesserId
   * @param lastState
   * @return
   */
  List<Operation> getSwitchcoderOperation(int coderId, int guesserId, 
      Map<String, Object> lastState) {
    List<Operation> switchcoderMove = new ArrayList<Operation>();
    switchcoderMove.add(new SetTurn(guesserId));
    switchcoderMove.add(new Set(GUESSHISTORY, ImmutableList.<String>of()));
    switchcoderMove.add(new Set(FEEDBACKHISTORY, ImmutableList.<String>of()));
    int lastCurrentGame = (Integer)lastState.get(CURRENTGAME);
    int lastCurrentTurn = (Integer)lastState.get(CURRENTTURN);
    switchcoderMove.add(new Set(CURRENTGAME, lastCurrentGame+1));
    switchcoderMove.add(new Set(MAXTURN, lastCurrentTurn));
    switchcoderMove.add(new Set(CURRENTMOVE,CODE));
    switchcoderMove.add(new Set(CURRENTTURN,0));
    switchcoderMove.add(new Delete(CODE));
    return switchcoderMove;
  }
  
  /**
   * End game operation
   * @param coderId
   * @param guesserId
   * @param winnerId
   * @return
   */
  List<Operation> getEndGameOperation(int coderId, int guesserId, int winnerId) {
    List<Operation> endMove = new ArrayList<Operation>();
    endMove.add(new SetTurn(guesserId));
    endMove.add(new Set(CURRENTMOVE,END));
    endMove.add(new EndGame(winnerId));
    return endMove;
  }
  
  private void check(boolean val, Object... debugArguments) {
    if (!val) {
      throw new RuntimeException("We have a hacker! debugArguments="
          + Arrays.toString(debugArguments));
    }
  }
}
  
