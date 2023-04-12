package com.introvertuoso;

import java.io.Serializable;

public class WalkMove extends Move implements Serializable {

  private static final long serialVersionUID = 034;

  private int limit;
  private String directionString;
  private Square direction;
  private Arena arena;

  @Override
  public void PerformMove() {
    if (sourceChampion != null) {
      int x_old = sourceChampion.getPosX();
      int y_old = sourceChampion.getPosY();
      int current_x = x_old, current_y = y_old;
      int mSpeed = (int) sourceChampion.getCurrentAttributes().getMovementSpeed();
      for (int i = 0; i < mSpeed; i++) {
        Square currentSquare = arena.getBoard()[current_x][current_y];
        Item item = currentSquare.getItem();
        if (item != null) {
          sourceChampion.getOwner().getDroppedItems().add(item);
          arena.getBoard()[current_x][current_y].setItem(null);
          arena.getItemsPool().remove(item);
        }
        int nextX = current_x + direction.getX();
        int nextY = current_y + direction.getY();
        if (Utility.withinArena(nextX, nextY, limit) &&
            arena.getBoard()[nextX][nextY].getSquareType() != SquareType.TERRAIN) {
          if (currentSquare.getSquareType() == SquareType.WATER) {
            mSpeed = (mSpeed - i) / 2;
          }
          current_x += direction.getX();
          current_y += direction.getY();
        }
      }

      sourceChampion.setPosX(current_x);
      sourceChampion.setPosY(current_y);
      arena.getBoard()[current_x][current_y].getContents().add(sourceChampion);
      arena.getBoard()[x_old][y_old].getContents().remove(sourceChampion);
    }
  }

  @Override
  public String log() {
    String temp="";
    temp += ConsoleColors.PURPLE;
    if (sourceChampion.getOwner() != null) {
      temp += String.valueOf(getSourceChampion().getOwner().getName().charAt(0))
              + String.valueOf(getSourceChampion().getOwner().getName().charAt(
              getSourceChampion().getOwner().getName().length() - 1))
              + ":";
    }
    temp += getSourceChampion().getCurrentAttributes().getName()
            + " ordered to walk " + directionString
            + ConsoleColors.RESET;
    return temp;

//    return ConsoleColors.PURPLE
//        + getSourceChampion().getOwner().getName().charAt(0)
//        + getSourceChampion().getOwner().getName().charAt(
//        getSourceChampion().getOwner().getName().length() - 1)
//        + ":"
//        + getSourceChampion().getCurrentAttributes().getName()
//        + " ordered to walk " + directionString
//        + ConsoleColors.RESET;

  }

  @Override
  public void removeEffects() {
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public Square getDirection() {
    return direction;
  }

  public void setDirection(Square direction) {
    this.direction = direction;
  }

  public Arena getArena() {
    return arena;
  }

  public void setArena(Arena arena) {
    this.arena = arena;
  }

  public String getDirectionString() {
    return directionString;
  }

  public void setDirectionString(String directionString) {
    this.directionString = directionString;
  }
}
