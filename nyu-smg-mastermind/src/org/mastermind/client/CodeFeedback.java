package org.mastermind.client;

import java.util.HashMap;
import java.util.Map;

public class CodeFeedback {
  /**
   * This is the method to get valid feedback
   * Assume code.length() = guess.length(), otherwise return 0b0w
   * 
   * @param code
   * @param guess
   * @return
   */
  static String getValidFeedback(String code, String guess){
    if (code.length() != guess.length()){
      return "0b0w";
    }
    int b = 0;
    int w = 0;
    Map<String,Integer> codeMap = new HashMap<String,Integer>();
    Map<String,Integer> guessMap = new HashMap<String,Integer>();
    for (int i = 0; i < code.length(); ++ i ){
      char codeChar = code.charAt(i);
      char guessChar = guess.charAt(i);
      if (codeChar == guessChar){
        b ++;
      } else {
        if (codeMap.containsKey(codeChar)){
          codeMap.put(""+codeChar, codeMap.get(codeChar)+1);
        } else {
          codeMap.put(""+codeChar, 1);
        }
        
        if (guessMap.containsKey(guessChar)){
          guessMap.put(""+guessChar, guessMap.get(guessChar)+1);
        } else {
          guessMap.put(""+guessChar, 1);
        }
      }
    }
    
    for (String s: codeMap.keySet()){
      if (guessMap.containsKey(s)){
        w += Math.min(codeMap.get(s), guessMap.get(s));
      }
    }
    
    return ""+b+"b"+w+"w";
  }
  
}
