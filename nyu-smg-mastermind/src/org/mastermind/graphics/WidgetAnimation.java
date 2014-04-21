package org.mastermind.graphics;

import com.google.appengine.api.images.Image;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class WidgetAnimation extends Animation{
  AbsolutePanel panel;
  Widget start, end, moving, toMove;
  ImageResource piece, transform;
  int startX, startY, startWidth, startHeight;
  int endX, endY;
  Audio soundAtEnd;
  boolean cancelled;
  private final static int yOffset = 40;

  public WidgetAnimation(Widget startWidget, Widget endWidget, Widget move
                  //ImageResource startRes, ImageResource endRes, ImageResource blankRes, 
                  //Audio sfx
                  ){
          start = startWidget;
          end = endWidget;
          //piece = startRes;
          //transform = endRes == null ? startRes : endRes;
          panel = (AbsolutePanel) start.getParent();
          startX = panel.getWidgetLeft(start);
          startY = panel.getWidgetTop(start);
          endX = panel.getWidgetLeft(end);
          endY = panel.getWidgetTop(end);
          //soundAtEnd = sfx;
          toMove = move;
          cancelled = false;

          moving = toMove;
          //moving.setPixelSize(startWidth, startHeight);
          panel.add(moving, startX, startY);
  }

  @Override
  protected void onUpdate(double progress) {
          int x = (int) (startX + (endX - startX) * progress);
          int y = (int) (startY + (endY - startY) * progress) 
              + (int)(yOffset*Math.sin(progress * Math.PI));
          double scale = 1 + 0.5 * Math.sin(progress * Math.PI);
          //int width = (int) (startWidth * scale);
          //int height = (int) (startHeight * scale);
          //moving.setPixelSize(width, height);
          //x -= (width - startWidth) / 2;
          //y -= (height - startHeight) / 2;

          panel.remove(moving);
          //moving.setPixelSize(width, height);
          panel.add(moving, x, y);
  }

  @Override
  protected void onCancel() {
          cancelled = true;
          panel.remove(moving);
  }

  @Override
  protected void onComplete() {
          if (!cancelled) {
                  if (soundAtEnd != null)
                          soundAtEnd.play();
                  //end.setResource(transform);
                  panel.remove(moving);
          }
  }
}
