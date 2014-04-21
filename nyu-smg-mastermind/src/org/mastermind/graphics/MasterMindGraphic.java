package org.mastermind.graphics;

import java.util.List;
import java.util.Map;

import org.mastermind.client.MasterMindPresenter;
import org.mastermind.client.MasterMindPresenter.View;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Graphic of the master mind game.
 * Since it is mostly digit game, we use HTML to present the game. Content are inserted as 
 * HTMLPanel
 * 
 * @author Archer
 *
 */
public class MasterMindGraphic extends Composite implements View {
  public interface MasterMindGraphicsUiBinder extends UiBinder<Widget, MasterMindGraphic> {
  }
  
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
  private final String MAXTURN= "MaxTurn";
  private final String MAXDIGIT= "MaxDigit";
  private static GameSounds gameSounds = GWT.create(GameSounds.class);
  
  
  @UiField
  VerticalPanel historyArea;
  @UiField
  VerticalPanel feedbackArea;
  @UiField
  VerticalPanel infoArea;
  @UiField
  HorizontalPanel inputInfoArea;
  @UiField
  HorizontalPanel messageArea;
  @UiField
  AbsolutePanel buttonArea = new AbsolutePanel();
  @UiField
  HorizontalPanel dragArea = new HorizontalPanel();
  private boolean enableClicks = false;
  private MasterMindPresenter presenter;
  private String input = "";
  private Map<String, Object> state;
  private String currentState = ""; 
  private Audio gameAudio;
  private SoundController soundController;
  private Sound sound;
  
  public MasterMindGraphic() {
    MasterMindGraphicsUiBinder uiBinder = GWT.create(MasterMindGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    buildButtonArea();
    buildDragArea();
    this.soundController = new SoundController();
    this.sound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG_MP3,
        "sounds/pieceDown.mp3", false, false);
    if (Audio.isSupported()) {
      gameAudio = Audio.createIfSupported();
      gameAudio.addSource(gameSounds.pieceDownMp3().getSafeUri().asString(), AudioElement.TYPE_MP3);
    }
  }
  
  /**
   * private methods updates panels
   * 
   */
  private void showHistoryArea(Map<String, Object> state) {
    List<String> guessHistory = (List<String>)state.get(GUESSHISTORY);
    this.historyArea.clear();
    if (guessHistory != null){
      this.historyArea.add(this.createGuessPanel(guessHistory));
    }
  }
  
  private void showFeedbackArea(Map<String, Object> state) {
    List<String> feedbackHistory = (List<String>)state.get(FEEDBACKHISTORY);
    this.feedbackArea.clear();
    if (feedbackHistory != null){
      this.feedbackArea.add(this.createFeedbackPanel(feedbackHistory));
    }
  }
  
  private void showInfoArea(Map<String, Object> state) {
    this.infoArea.clear();
    this.infoArea.add(createInfoPanel(state));
  }
  
  private void updateInputArea() {
    String html;
    if (currentState == MasterMindGraphic.FEEDBACK){
      String b = this.input.length() > 0 ? this.input.substring(0, 1):" " ;
      String w = this.input.length() > 1 ? this.input.substring(1, 2):" ";
      html = "<div id = 'inputArea'> Input: "
          + b + "b"
          + w + "w"
          + "</div>";
    } else {
      html = "<div id = 'inputArea'> Input: "
          + this.input
          + "</div>";
    }
    this.inputInfoArea.clear();
    this.inputInfoArea.add(new HTMLPanel(html));
  }
  
  private void updateMessageArea(String s){
    this.messageArea.clear();
    this.messageArea.add(new HTMLPanel(
        "<div id = messagePanel>"+ s +" </div>"));
  }
  private static Button [] aniBtn;
  private static Button toMove = null;
  
  private void buildButtonArea() {
    aniBtn = new Button[10];
    for (int i = 0; i<10; i ++) {
      final String optionF= String.valueOf(i);
      aniBtn[i] = new Button(optionF);
    }
    this.buttonArea.clear();
    final Button btn1;
    btn1 = new Button("DROP HERE");
    for (Integer i = 0; i < 10; i ++){
      final String optionF= i.toString();
      final Button btn = new Button(optionF);
      btn.getElement().setDraggable(Element.DRAGGABLE_TRUE);
      btn.addClickHandler(new ClickHandler(){
         @Override
          public void onClick(ClickEvent event) {
            if (enableClicks) {
              toMove = btn;
              //inputAppend(optionF);
              //updateInputArea();
              sound.play();
              dropLabel.setText("DIGIT "+optionF+
                  " RECEIVED, CLICK DROP HERE BUTTON TO ENTER DIGIT");
              
            }
          }
      });
      btn.addDragStartHandler(new DragStartHandler() {
        @Override
        public void onDragStart(DragStartEvent event) {
            event.setData("text", optionF);
            dropLabel.setText("DIGIT "+optionF+
                " RECEIVED, DROP THE DIGIT TO DROP HERE BUTTON");
        }
      });
      this.buttonArea.add(btn);
    }
    //Drop button
    btn1.addClickHandler(new ClickHandler(){
       @Override
       public void onClick(ClickEvent event) {
         if (enableClicks && toMove != null) {
           String s = toMove.getText();
           startAnimation(toMove,btn1,aniBtn[Integer.parseInt(s)]);
           inputAppend(s);
           updateInputArea();
           sound.play();
           toMove = null;
           dropLabel.setText("INPUT RECEIVED");
         }
       }
    });
    btn1.addDragOverHandler(new DragOverHandler() {
      @Override
      public void onDragOver(DragOverEvent event) {
        dropLabel.setText("DROP TO ENTER DIGIT");
      }
    });
   
    btn1.addDropHandler(new DropHandler() {
      @Override
      public void onDrop(DropEvent event) {
          // prevent the native text drop
          event.preventDefault();
   
          // get the data out of the event
          String data = event.getData("text");
          if (enableClicks) {
            dropLabel.setText("INPUT RECEIVED");
            inputAppend(data);
            updateInputArea();
            gameAudio.play();
          } else {
            dropLabel.setText("INPUT DISABLE:NOT YOUR TURN");
          }
      }
    });
    
    btn1.addDomHandler(new DragLeaveHandler()
    {
        @Override
        public void onDragLeave(DragLeaveEvent event)
        {
          dropLabel.setText("DROP TO RELEASE");
        }
    }, DragLeaveEvent.getType());
    
    this.buttonArea.add(btn1);
    //Delete button
    Button btn;
    btn = new Button("DELETE");
    btn.addClickHandler(new ClickHandler(){
       @Override
       public void onClick(ClickEvent event) {
         if (enableClicks && input.length() > 0) {
           input = input.substring(0, input.length()-1);
           updateInputArea();
         }
       }
    });
    this.buttonArea.add(btn);
    //Clear button
    btn = new Button("CLEAR");
    btn.addClickHandler(new ClickHandler(){
       @Override
       public void onClick(ClickEvent event) {
         if (enableClicks && input.length() > 0) {
           input = "";
           updateInputArea();
         }
       }
    });
    this.buttonArea.add(btn);
    btn = new Button("SUBMIT");
    btn.addClickHandler(new ClickHandler(){
       @Override
       public void onClick(ClickEvent event) {
         if (enableClicks) {
          if (currentState == FEEDBACK) {
            if (checkValidFeedbackInput(input)) {
              updateMessageArea("Feedback Send, it is opponent turn");
              presenter.sendFeedbackMove(getFormatFeedback(input));
              enableClicks = false;
            } else {
              updateMessageArea("Illegal Feedback, please try again");
            }
          } else if (currentState == CODE) {
            if (checkValidCodeInput(input)) {
              updateMessageArea("Code Send, it is opponent turn");
              presenter.sendCodeMove(input);
              enableClicks = false;
            } else {
              updateMessageArea("Illegal Code, please try again");
            }
          } else {
            if (checkValidCodeInput(input)) {
              updateMessageArea("Guess Send, it is opponent turn");
              presenter.sendGuessMove(input);
              enableClicks = false;
            } else {
              updateMessageArea("Illegal Guess, please try again");
            }
          }
        }
       }
    });
    this.buttonArea.add(btn);
  }
  
  /**
   * generate guess panel with guess history
   * @param history
   * @return
   */
  private HTMLPanel createGuessPanel(List<String> history) {
    StringBuilder htmlBuilder = new StringBuilder();
    htmlBuilder.append("<div id = 'guessHistoryPanel'>"
        + "<div class='panelTitle'>Guess</div>");
    for (String h : history) {
      htmlBuilder.append("<div class='GuessEntry'>"+h+"</div>");
    }
    htmlBuilder.append("</div>");
    String html = htmlBuilder.toString();
    HTMLPanel panel = new HTMLPanel(html);
    return panel;
  }
  
  /**
   * generate feedback panel with feedback history
   * @param history
   * @return
   */
  private HTMLPanel createFeedbackPanel(List<String> history) {
    StringBuilder htmlBuilder = new StringBuilder();
    htmlBuilder.append("<div id = 'feedbackHistoryPanel'>"
        + "<div class='panelTitle'>Feedback</div>");
    for (String h : history) {
      htmlBuilder.append("<div class='FeedbackEntry'>"+h+"</div>");
    }
    htmlBuilder.append("</div>");
    String html = htmlBuilder.toString();
    HTMLPanel panel = new HTMLPanel(html);
    return panel;
  }
  
  /**
   * generate info panel with info history
   * @param state
   * @return
   */
  private HTMLPanel createInfoPanel(Map<String, Object> state) {
    String maxdigit = ((Integer)state.get(this.MAXDIGIT)).toString() == null? 
        "" : ((Integer)state.get(this.MAXDIGIT)).toString();
    String currentMove = (String)state.get(this.CURRENTMOVE) == null?
        "" : (String)state.get(this.CURRENTMOVE);
    String codeLength = ((Integer)state.get(this.CODELENGTH)).toString() == null?
        "" : ((Integer)state.get(this.CODELENGTH)).toString();
    String maxTurn = ((Integer)state.get(this.MAXTURN)).toString() == null?
        "" : ((Integer)state.get(this.MAXTURN)).toString();
    String currentTurn = ((Integer)state.get(this.CURRENTTURN)).toString() == null?
        "" : ((Integer)state.get(this.CURRENTTURN)).toString();
    String code = ((String)state.get(MasterMindGraphic.CODE)) == null?
        "" : ((String)state.get(MasterMindGraphic.CODE));
    String html = "<div id = 'infoPanel'>"
        + "<div class='panelTitle'>Info Panel</div>"
        + "<div><span class = 'InfoEntryKey'>Max Digit: </span>"
        + "<span class = 'InfoEntryValue'>"+ maxdigit + "</span> </div>"
        + "<div><span class = 'InfoEntryKey'>Code length: </span>"
        + "<span class = 'InfoEntryValue'>"+ codeLength + " </div>"
        + "<div><span class = 'InfoEntryKey'>Guess: </span>"
        + "<span class = 'InfoEntryValue'>"+ currentTurn + "/"+ maxTurn +" </div>"
        + "<div><span class = 'InfoEntryKey'>Stage: </span>"
        + "<span class = 'InfoEntryValue'>"+ currentMove + " </div>"
        + "<div><span class = 'InfoEntryKey'>Code: </span>"
        + "<span class = 'InfoEntryValue'>"+ code + " </div>"
        + "</div>";
    HTMLPanel panel = new HTMLPanel(html);
    return panel;
  }
  
  private void inputAppend(String s){
    int maxLength = 4;
    if (currentState == MasterMindGraphic.FEEDBACK) {
      maxLength = 2;
    }
    if (input.length() < maxLength) {
      this.input += s;
    }
  }
  
  /**
   * helper functions for validation
   * 
   */
  
  private boolean checkValidFeedbackInput(String feedback){
    if (feedback.length() != 2) {
      return false;
    }
    int CL = (Integer)this.state.get(CODELENGTH);
    int b = feedback.charAt(0) - '0';
    int w = feedback.charAt(1) -'0';
    if (!(b <= CL && b >= 0) || !(w <= CL && w >= 0) || !(b+w <=CL && b+w >= 0)){
      return false;
    }
    return true;
  }
  
  private String getFormatFeedback(String feedbackInput){
    try{
      return feedbackInput.substring(0, 1)+"b"+feedbackInput.substring(1, 2)+"w";
    } catch (Exception e){
      return "0b0w";
    }
    
  }
  
  private boolean checkValidCodeInput(String code){
    int CL = (Integer)this.state.get(CODELENGTH);
    if (code.length() != CL) {
      return false;
    }
    int maxDigit = (Integer)this.state.get(MAXDIGIT);
    for (char c: code.toCharArray()){
      if (c <'0' || c > '0' + maxDigit) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void setPresenter(MasterMindPresenter masterMindPresenter) {
    this.presenter = masterMindPresenter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setState(Map<String, Object> state) {
    this.state = state;
    this.showInfoArea(state);
    this.showHistoryArea(state);
    this.showFeedbackArea(state);
    this.updateMessageArea("");
    this.input = "";
    this.inputInfoArea.clear();
    this.enableClicks = false;
    currentState = (String)state.get(this.CURRENTMOVE) == null?
        "" : (String)state.get(this.CURRENTMOVE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCoderStateCode(Map<String, Object> state) {
    this.setState(state);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCoderStateFeedback(Map<String, Object> state) {
    this.setState(state);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setGuesserState(Map<String, Object> state) {
    this.setState(state);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startCode(String code) {
    this.updateMessageArea("Start input code");
    this.enableClicks = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startGuess(String guess) {
    this.updateMessageArea("Start guessing");
    this.enableClicks = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startFeedback(String feedback) {
    this.updateMessageArea("Start giving feedback");
    this.enableClicks = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendEndGameInfo(boolean isWin) {
    if (isWin) {
      this.updateMessageArea("Game End. You are the winner!");
    } else {
      this.updateMessageArea("Game End. The opponent win.");
    }
    this.enableClicks = false;
  }
  
  final Label dropLabel = new Label("DROP");
    
  private void buildDragArea() {
    this.dragArea.add(dropLabel);
  }
  //Animation
  
  public void startAnimation(Widget start, Widget end,Widget move) {
    WidgetAnimation wd = new WidgetAnimation(start,end,move);
    wd.run(1000);
  }
  
}
