package org.mastermind.graphics;

import org.game_api.GameApi;
import org.game_api.GameApi.ContainerConnector;
import org.game_api.GameApi.Game;
import org.game_api.GameApi.IteratingPlayerContainer;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;
import org.mastermind.client.MasterMindLogic;
import org.mastermind.client.MasterMindPresenter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class MasterMindEntryPoint implements EntryPoint {
	ContainerConnector container;
  MasterMindPresenter masterMindPresenter;
  
  @Override
  public void onModuleLoad() {
    Game game = new Game() {
      @Override
      public void sendVerifyMove(VerifyMove verifyMove) {
        container.sendVerifyMoveDone(new MasterMindLogic().verify(verifyMove));
      }

      @Override
      public void sendUpdateUI(UpdateUI updateUI) {
        masterMindPresenter.updateUI(updateUI);
      }
    };
    container = new ContainerConnector(game);
    container.sendGameReady();
    MasterMindGraphic MasterMindGraphics = new MasterMindGraphic();
    masterMindPresenter =
        new MasterMindPresenter(MasterMindGraphics, container);
//    final ListBox playerSelect = new ListBox();
//    playerSelect.addItem("WhitePlayer");
//    playerSelect.addItem("BlackPlayer");
//    playerSelect.addItem("Viewer");
//    playerSelect.addChangeHandler(new ChangeHandler() {
//      @Override
//      public void onChange(ChangeEvent event) {
//        int selectedIndex = playerSelect.getSelectedIndex();
//        String playerId = selectedIndex == 2 ? GameApi.VIEWER_ID
//            : container.getPlayerIds().get(selectedIndex);
//        container.updateUi(playerId);
//      }
//    });
    FlowPanel flowPanel = new FlowPanel();
    flowPanel.add(MasterMindGraphics);
    RootPanel.get("mainDiv").add(flowPanel);
//    container.sendGameReady();
//    container.updateUi(container.getPlayerIds().get(0));
  }

}
