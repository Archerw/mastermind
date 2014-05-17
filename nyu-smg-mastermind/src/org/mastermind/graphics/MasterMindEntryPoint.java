package org.mastermind.graphics;

import org.game_api.GameApi.Container;
import org.game_api.GameApi.ContainerConnector;
import org.game_api.GameApi.Game;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;
import org.mastermind.client.MasterMindLogic;
import org.mastermind.client.MasterMindPresenter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class MasterMindEntryPoint implements EntryPoint {
	Container container;
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
    MasterMindGraphic MasterMindGraphics = new MasterMindGraphic();
    masterMindPresenter =
        new MasterMindPresenter(MasterMindGraphics, container);
    RootPanel.get("mainDiv").add(MasterMindGraphics);
    container.sendGameReady();
  }
}
