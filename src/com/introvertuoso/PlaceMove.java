package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaceMove extends Move implements Serializable {

  private static final long serialVersionUID = 024;

  private Arena arena;
  private Square destination;

  public Square getDestination() {
    return destination;
  }

  public void setDestination(Square destination) {
    this.destination = destination;
  }

  @Override
  public void PerformMove() {
    Player owner = sourceChampion.getOwner();
    ArrayList<Champion> champsOnBoard = owner.getChampsOnBoard();
    sourceChampion.setPosX(destination.getX());
    sourceChampion.setPosY(destination.getY());
    Item item = destination.getItem();
    if (item != null) {
      sourceChampion.getOwner().getDroppedItems().add(item);
      arena.getItemsPool().remove(item);
      destination.setItem(null);
    }
    destination.getContents().add(sourceChampion);
    sourceChampion.getOwner().getInventory().remove(sourceChampion);
    int c = 0;
    int[] indices = new int[2];
    for (int i = 0; i < champsOnBoard.size(); i++) {
      if (champsOnBoard.get(i).getCurrentAttributes().getName().equals(
          sourceChampion.getCurrentAttributes().getName())) {
        indices[c] = i;
        c++;
      }
    }
    if (c == 2) {
      champsOnBoard.remove(indices[0]);
      champsOnBoard.remove(indices[1] - 1);
      sourceChampion.levelUpAttributes();
    }
    champsOnBoard.add(sourceChampion);
  }

  @Override
  public String log() {

    String temp = "";
    temp += ConsoleColors.YELLOW;
    if (sourceChampion.getOwner() != null) {
      temp += sourceChampion.getOwner().getName();
    }
    temp += " placed "
            + sourceChampion.getCurrentAttributes().getName()
            + ConsoleColors.RESET;
    return temp;

//    return ConsoleColors.YELLOW
//        + sourceChampion.getOwner().getName()
//        + " placed "
//        + sourceChampion.getCurrentAttributes().getName()
//        + ConsoleColors.RESET;
  }

  @Override
  public void removeEffects() {
  }

  public Arena getArena() {
    return arena;
  }

  public void setArena(Arena arena) {
    this.arena = arena;
  }
}
