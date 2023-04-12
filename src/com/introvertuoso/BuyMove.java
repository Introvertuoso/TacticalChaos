package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public class BuyMove extends Move implements Serializable {

  private static final long serialVersionUID = 006;

  @Override
  public void PerformMove() {
    Player owner = sourceChampion.getOwner();
    ArrayList<Champion> inv = owner.getInventory();
    owner.setBalance(owner.getBalance() - (int) sourceChampion.getCurrentAttributes().getValue());
    int c = 0;
    int[] indices = new int[2];
    for (int i = 0; i < inv.size(); i++) {
      if (inv.get(i).getCurrentAttributes().getName().equals(
          sourceChampion.getCurrentAttributes().getName())) {
        indices[c] = i;
        c++;
      }
    }
    if (c == 2) {
      inv.remove(indices[0]);
      inv.remove(indices[1] - 1);
      sourceChampion.levelUpAttributes();
    }
    inv.add(sourceChampion);
  }

  @Override
  public String log() {
    String temp = "";
    temp += ConsoleColors.BLUE;
    if (sourceChampion.getOwner() != null) {
      temp += getSourceChampion().getOwner().getName();
    }
    temp += " bought "
            + getSourceChampion().getCurrentAttributes().getName() +
            ConsoleColors.RESET;
    return temp;
//    return ConsoleColors.BLUE +
//        getSourceChampion().getOwner().getName() +
//        " bought "
//        + getSourceChampion().getCurrentAttributes().getName() +
//        ConsoleColors.RESET;
  }

  @Override
  public void removeEffects() {
  }
}
