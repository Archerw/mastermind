package org.mastermind.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.gwt.i18n.client.NumberFormat;

public class MasterMindAI {
  private List<String> possibleAnswer = new ArrayList<String>();
  private Random random = new Random();
  
  public MasterMindAI() {
    init();
  }
  
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
  
  public void filter(List<String> guessHistory, List<String> feedbackHistory) {
    Iterator<String> it = possibleAnswer.iterator();
    while (it.hasNext()){
      String code = it.next();
      for (int i = 0; i < feedbackHistory.size(); i ++) {
        if (i >= guessHistory.size()){
          break;
        }
        String temp = CodeFeedback.getValidFeedback(code, guessHistory.get(i));
        if (!temp.equals(feedbackHistory.get(i))){
          it.remove();
          break;
        }
      }
      System.out.println(code);
    }
  }
  
  public String generateCode(int length, int maxInt){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; ++ i) {
      sb.append(random.nextInt(maxInt+1));
    }
    return sb.toString();
  }
  
  public String generateGuess(){
    return possibleAnswer.get(random.nextInt(possibleAnswer.size()));
  }

  public static void main(String[] args){
    MasterMindAI ai = new MasterMindAI();
//    System.out.println(ai.generateCode(4, 9));
//    System.out.println(ai.generateCode(2, 3));
//    System.out.println(ai.generateCode(2, 3));
//    String code = "8888";
//    List<String> guessHistory = new ArrayList<String>();
//    List<String> feedbackHistory = new ArrayList<String>();
//    for (int i = 0; i < 6; i ++){
//      String guess = ai.generateGuess();
//      guessHistory.add(guess);
//      feedbackHistory.add(CodeFeedback.getValidFeedback(code, guess));
//      ai.filter(guessHistory, feedbackHistory);
//    }
//    System.out.println("===");
//    ai.filter(guessHistory, feedbackHistory);
//    
  }
}
