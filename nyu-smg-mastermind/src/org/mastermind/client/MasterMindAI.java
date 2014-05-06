package org.mastermind.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MasterMindAI {
  private List<String> possibleAnswer = new ArrayList<String>();
  private Random random = new Random();
  private int level;
  
  /**
   * Generate an AI
   * @param level a parameter indicate how good is the ai, should be from 0 to 5, parameter will
   * be adjusted if out of range
   */
  public MasterMindAI(int level) {
    int i = level;
    if (i > 5) {
      i = 5;
    }
    if (i < 0 ) {
      i = 0;
    }
    this.level = (level+5)*500;
    init();
  }
  
  /**
   * Initial AI
   */
  public void init() {
    //TODO generate general init
    //String.format() cannot be used in GWT
    for (int i = 0; i < 10000; i ++) {
      String digits = String.valueOf(i);
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 4-digits.length(); j ++) {
        sb.append('0');
      }
      sb.append(digits);
      possibleAnswer.add(sb.toString());
    }
  }
  
  /**
   * filter out impossible answers with guess and feedback history
   * @param guessHistory
   * @param feedbackHistory
   */
  public void filter(List<String> guessHistory, List<String> feedbackHistory) {
    Iterator<String> it = possibleAnswer.iterator();
    int i = feedbackHistory.size() - 1;
    if (i >= guessHistory.size()){
      return;
    }
    String gs = guessHistory.get(i);
    String fb = feedbackHistory.get(i);
    for (int count = 0; count < level && it.hasNext(); count++) {
      String code = it.next();
      String temp = CodeFeedback.getValidFeedback(code, gs);
      if (!temp.equals(fb)){
        it.remove();
        continue;
      }
    }
  }
  
  /**
   * generate a random code
   * @param length
   * @param maxInt
   * @return
   */
  public String generateCode(int length, int maxInt){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; ++ i) {
      sb.append(random.nextInt(maxInt+1));
    }
    return sb.toString();
  }
  
  /**
   * generate a guess
   * @return
   */
  public String generateGuess(){
    return possibleAnswer.get(random.nextInt(possibleAnswer.size()));
  }
  
  public static void main(String[] args){
    MasterMindAI ai = new MasterMindAI(3);
    String code = "1357";
    List<String> guessHistory = new ArrayList<String>();
    List<String> feedbackHistory = new ArrayList<String>();
    for (int i = 0; i < 10; i ++){
      String guess = ai.generateGuess();
      guessHistory.add(guess);
      feedbackHistory.add(CodeFeedback.getValidFeedback(code, guess));
      ai.filter(guessHistory, feedbackHistory);
    }
    System.out.println("===");
    System.out.println(guessHistory);
    System.out.println(feedbackHistory);
    System.out.println(ai.generateGuess());
    
  }
}
