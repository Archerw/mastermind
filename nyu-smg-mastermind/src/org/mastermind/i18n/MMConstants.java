package org.mastermind.i18n;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public interface MMConstants extends Constants {
  @DefaultStringValue("Guess")
  public String Guess();
  
  @DefaultStringValue("Feedback")
  public String Feedback();
  
  @DefaultStringValue("Info Panel")
  public String InfoPanel();
  
  @DefaultStringValue("Max Digit")
  public String MaxDigit();
  
  @DefaultStringValue("Code length")
  public String CodeLength();
  
  @DefaultStringValue("Stage")
  public String Stage();
  
  @DefaultStringValue("Code")
  public String Code();
  
  //BUTTONS
  @DefaultStringValue("DROP HERE")
  public String DROPHERE();
  
  @DefaultStringValue("DELETE")
  public String DELETE();
  
  @DefaultStringValue("CLEAR")
  public String CLEAR();
  
  @DefaultStringValue("SUBMIT")
  public String SUBMIT();
  
  //Input Area
  
  @DefaultStringValue("Input")
  public String Input();
  
  //Message Area
  @DefaultStringValue("Start input code")
  public String StartCode();
  
  @DefaultStringValue("Start guessing")
  public String StartGuess();
  
  @DefaultStringValue("Start giving feedback")
  public String StartFeedback();
  
  @DefaultStringValue("Game End. You are the winner!")
  public String GameWon();
  
  @DefaultStringValue("Game End. The opponent win.")
  public String GameLost();
  
  @DefaultStringValue("Feedback Sent, it is opponent turn")
  public String FeedbackSend();
  
  @DefaultStringValue("Illegal Feedback, please try again")
  public String IllegalFeedback();
  
  @DefaultStringValue("Code Sent, it is opponent turn")
  public String CodeSend();
  
  @DefaultStringValue("Illegal Code, please try again")
  public String IllegalCode();
  
  @DefaultStringValue("Guess Sent, it is opponent turn")
  public String GuessSend();
  
  @DefaultStringValue("Illegal Guess, please try again")
  public String IllegalGuess();
  
  //Drop Panel
  @DefaultStringValue("INPUT RECEIVED")
  public String InputReceived();
  
  @DefaultStringValue("INPUT DISABLED:NOT YOUR TURN")
  public String InputDisable();
  
  @DefaultStringValue("DROP TO ENTER DIGIT")
  public String DropToEnter();
  
  @DefaultStringValue("DIGIT ")
  public String SELECTFIRST();
  
  @DefaultStringValue(" RECEIVED, DROP THE DIGIT TO DROP HERE BUTTON")
  public String SELETESECOND();

  
  
  
  
  
  @DefaultStringValue("CODE")
  public String CODE();
  
  @DefaultStringValue("GUESS")
  public String GUESS();
  
  @DefaultStringValue("FEEDBACK")
  public String FEEDBACK();
  
  @DefaultStringValue("VERIFY")
  public String VERIFY();
  
  
  
}
