package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public class SwapMove extends Move implements Serializable {

  private static final long serialVersionUID = 032;

  private Champion target;
  private Arena arena;

  @Override
  public void PerformMove() {
    if (sourceChampion != null && target != null) {
      int xT = target.getPosX();
      int yT = target.getPosY();

      Player owner = sourceChampion.getOwner();
      ArrayList<Champion> inv = owner.getInventory();
      ArrayList<Champion> onBoard = owner.getChampsOnBoard();

      int c1 = 0;
      int[] indices1 = new int[2];

      for (int i = 0; i < onBoard.size(); i++) {
        if (onBoard.get(i).getCurrentAttributes().getName().equals(
            sourceChampion.getCurrentAttributes().getName())) {
          indices1[c1] = i;
          c1++;
        }
      }

      if (c1 == 2) {
        Champion temp1 = onBoard.get(indices1[0]);
        Champion temp2 = onBoard.get(indices1[1]);
        onBoard.remove(indices1[0]);
        onBoard.remove(indices1[1] - 1);
        arena.getBoard()[temp1.getPosX()][temp1.getPosY()].getContents().remove(temp1);
        arena.getBoard()[temp2.getPosX()][temp2.getPosY()].getContents().remove(temp2);
        sourceChampion.levelUpAttributes();
      }
      inv.remove(sourceChampion);
      sourceChampion.setPosX(xT);
      sourceChampion.setPosY(yT);
      onBoard.add(sourceChampion);
      arena.getBoard()[xT][yT].getContents().add(sourceChampion);

      int c2 = 0;
      int[] indices2 = new int[2];
      for (int i = 0; i < inv.size(); i++) {
        if (inv.get(i).getCurrentAttributes().getName().equals(
            target.getCurrentAttributes().getName())) {
          indices2[c2] = i;
          c2++;
        }
      }
      if (c2 == 2) {
        inv.remove(indices2[0]);
        inv.remove(indices2[1] - 1);
        target.levelUpAttributes();
      }
      onBoard.remove(target);
      inv.add(target);
      arena.getBoard()[xT][yT].getContents().remove(target);
    }
  }

  @Override
  public String log() {
    String temp = "";
    temp += ConsoleColors.GREEN;
    if (sourceChampion.getOwner() != null) {
      temp += sourceChampion.getOwner().getName();
    }
    temp += " swapped between "
            + sourceChampion.getCurrentAttributes().getName()
            + " " + getTarget().getCurrentAttributes().getName()
            + ConsoleColors.RESET;
    return temp;
//    return ConsoleColors.GREEN
//            + sourceChampion.getOwner().getName()
//            + " swapped between "
//            + sourceChampion.getCurrentAttributes().getName()
//            + " " + getTarget().getCurrentAttributes().getName()
//            + ConsoleColors.RESET;
  }

  @Override
  public void removeEffects() {
  }

  public Champion getTarget() {
    return target;
  }

  public void setTarget(Champion target) {
    this.target = target;
  }

  public Arena getArena() {
    return arena;
  }

  public void setArena(Arena arena) {
    this.arena = arena;
  }
}
