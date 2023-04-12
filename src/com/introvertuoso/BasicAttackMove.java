package com.introvertuoso;

import java.io.Serializable;

public class BasicAttackMove extends Move implements Serializable {

  private static final long serialVersionUID = 004;

  private Champion targetChampion;

  @Override
  public void PerformMove() {
    if (sourceChampion != null && targetChampion != null) {
      if (Utility.inRange(sourceChampion, targetChampion,
          sourceChampion.getCurrentAttributes().getAttackRange())) {

        sourceChampion.GetIntendedDamage(targetChampion);
        Champion.ChampionAttributes attrs = sourceChampion.getCurrentAttributes();
        attrs.setManaStart(attrs.getManaStart() + 1);
      }
    }
  }

  @Override
  public String log() {
    String temp1 = "";
    String temp2 = "";
    String temp3 = "";

    temp1 += ConsoleColors.GREEN;
    if (sourceChampion.getOwner() != null) {
      temp1 += String.valueOf(getSourceChampion().getOwner().getName().charAt(0))
              + String.valueOf(getSourceChampion().getOwner().getName().charAt(
              getSourceChampion().getOwner().getName().length() - 1))
              + "" + ":" + "";
    }
    temp1 += getSourceChampion().getCurrentAttributes().getName()
            + ConsoleColors.RESET;

    temp2 += " ordered to attack "
            + ConsoleColors.RED;

    if (sourceChampion.getOwner() != null) {
      temp2 += String.valueOf(getTargetChampion().getOwner().getName().charAt(0))
              + String.valueOf(getTargetChampion().getOwner().getName().charAt(
              getTargetChampion().getOwner().getName().length() - 1))
              + "" + ":" + "";
    }

    temp2 += getTargetChampion().getCurrentAttributes().getName()
            + ConsoleColors.RESET;
    
    return temp1 + temp2 + temp3;

//    return ConsoleColors.GREEN
//        + getSourceChampion().getOwner().getName().charAt(0)
//        + getSourceChampion().getOwner().getName().charAt(
//        getSourceChampion().getOwner().getName().length() - 1)
//        + "" + ":" + ""
//        + getSourceChampion().getCurrentAttributes().getName()
//        + ConsoleColors.RESET
//        + " ordered to attack "
//        + ConsoleColors.RED
//        + getTargetChampion().getOwner().getName().charAt(0)
//        + getTargetChampion().getOwner().getName().charAt(
//        getTargetChampion().getOwner().getName().length() - 1)
//        + "" + ":" + ""
//        + getTargetChampion().getCurrentAttributes().getName()
//        + ConsoleColors.RESET;
  }

  @Override
  public void removeEffects() {
  }

  public Champion getTargetChampion() {
    return targetChampion;
  }

  public void setTargetChampion(Champion targetChampion) {
    this.targetChampion = targetChampion;
  }
}
