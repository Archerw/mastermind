package org.mastermind.graphics;

import java.util.List;
import java.util.Map;

import org.mastermind.client.MasterMindPresenter;
import org.mastermind.client.MasterMindPresenter.View;
import org.mastermind.i18n.MMConstants;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
  private MMConstants constants = GWT.create(MMConstants.class);
  
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
  }
  
  /**
   * private methods updates panels
   * 
   */
  private void showHistoryArea(Map<String, Object> state) {
    @SuppressWarnings("unchecked")
    List<String> guessHistory = (List<String>)state.get(GUESSHISTORY);
    this.historyArea.clear();
    if (guessHistory != null){
      this.historyArea.add(this.createGuessPanel(guessHistory));
    }
  }
  
  private void showFeedbackArea(Map<String, Object> state) {
    @SuppressWarnings("unchecked")
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
      html = "<div id = 'inputArea'> "+constants.Input()+": "
          + b + "b"
          + w + "w"
          + "</div>";
    } else {
      html = "<div id = 'inputArea'> "+constants.Input()+": "
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
  
  private void buildButtonArea() {
    this.buttonArea.clear();
    for (Integer i = 0; i < 10; i ++){
      final String optionF= i.toString();
      final DragButton btn = new DragButton(optionF);
      this.buttonArea.add(btn);
    }
    //Delete button
    Button btn;
    btn = new Button(constants.DELETE());
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
    btn = new Button(constants.CLEAR());
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
    btn = new Button(constants.SUBMIT());
    btn.addClickHandler(new ClickHandler(){
       @Override
       public void onClick(ClickEvent event) {
         if (enableClicks) {
          if (currentState == FEEDBACK) {
            if (checkValidFeedbackInput(input)) {
              updateMessageArea(constants.FeedbackSend());
              presenter.sendFeedbackMove(getFormatFeedback(input));
              enableClicks = false;
            } else {
              updateMessageArea(constants.IllegalFeedback());
            }
          } else if (currentState == CODE) {
            if (checkValidCodeInput(input)) {
              updateMessageArea(constants.CodeSend());
              presenter.sendCodeMove(input);
              enableClicks = false;
            } else {
              updateMessageArea(constants.IllegalCode());
            }
          } else {
            if (checkValidCodeInput(input)) {
              updateMessageArea(constants.GuessSend());
              presenter.sendGuessMove(input);
              enableClicks = false;
            } else {
              updateMessageArea(constants.IllegalGuess());
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
        + "<div class='panelTitle'>"+constants.Guess()+"</div>");
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
        + "<div class='panelTitle'>"+constants.Feedback()+"</div>");
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
    switch (currentMove) {
      case "CODE": currentMove = constants.CODE(); break;
      case "GUESS": currentMove = constants.GUESS();break;
      case "FEEDBACK": currentMove = constants.FEEDBACK(); break;
      case "VERIFY": currentMove = constants.VERIFY(); break;
      default: break;
    }
    String html = "<div id = 'infoPanel'>"
        + "<div class='panelTitle'>"+constants.InfoPanel()+"</div>"
        + "<div><span class = 'InfoEntryKey'>"+constants.MaxDigit()+": </span>"
        + "<span class = 'InfoEntryValue'>"+ maxdigit + "</span> </div>"
        + "<div><span class = 'InfoEntryKey'>"+constants.CodeLength()+": </span>"
        + "<span class = 'InfoEntryValue'>"+ codeLength + " </div>"
        + "<div><span class = 'InfoEntryKey'>"+constants.Guess()+": </span>"
        + "<span class = 'InfoEntryValue'>"+ currentTurn + "/"+ maxTurn +" </div>"
        + "<div><span class = 'InfoEntryKey'>"+constants.Stage()+": </span>"
        + "<span class = 'InfoEntryValue'>"+ currentMove + " </div>"
        + "<div><span class = 'InfoEntryKey'>"+constants.Code()+": </span>"
        + "<span class = 'InfoEntryValue'>"+ code + " </div>"
        + "</div>";
    HTMLPanel panel = new HTMLPanel(html);
    return panel;
  }
  
  void inputAppend(String s){
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
    this.updateMessageArea(constants.StartCode());
    this.enableClicks = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startGuess(String guess) {
    this.updateMessageArea(constants.StartGuess());
    this.enableClicks = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startFeedback(String feedback) {
    this.updateMessageArea(constants.StartFeedback());
    this.input = ""+feedback.charAt(0)+feedback.charAt(2);
    updateInputArea();
    this.enableClicks = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendEndGameInfo(boolean isWin) {
    if (isWin) {
      this.updateMessageArea(constants.GameWon());
    } else {
      this.updateMessageArea(constants.GameLost());
    }
    this.enableClicks = false;
  }
  
  final Label dropLabel = new Label("");
    
  private void buildDragArea() {
    this.dragArea.add(dropLabel);
  }
  //Animation
  
  public void startAnimation(Widget start, Widget end,Widget move) {
    WidgetAnimation wd = new WidgetAnimation(start,end,move);
    wd.run(1000);
  }
  
  //DragButtonPanel
  
  class DragButtonPanel extends AbsolutePanel {
    private PickupDragController dragController;
    
    public DragButtonPanel(PickupDragController dragController) {
      this.dragController = dragController;
    }
    
    public void setDragController(PickupDragController dragController) {
      this.dragController = dragController;
    }
    
    public void add(DragButton w) {
      dragController.makeDraggable(w);
      super.add(w);
    }
    
    @Override
    public boolean remove(Widget w) {
      int index = getWidgetIndex(w);
      if (index != -1 && w instanceof DragButton) {
        DragButton clone = ((DragButton) w).cloneButton();
        dragController.makeDraggable(clone);
        insert(clone, index);
      }
      return super.remove(w);
    }
  }
  
  //DragButton
  class DragButton extends Button {
    private final String text;
    
    public DragButton(final String optionF) {
      super(optionF);
      text = optionF;
      this.addClickHandler(new ClickHandler(){
         @Override
          public void onClick(ClickEvent event) {
            if (enableClicks) {
              inputAppend(optionF);
              updateInputArea();
              sound.play();
              dropLabel.setText(constants.SELECTFIRST() +text+
                  constants.SELETESECOND());
              
            }
          }
      });
    }
    
    public DragButton cloneButton() {
      String text = this.text;
      return new DragButton(text);
    }
  }
}



