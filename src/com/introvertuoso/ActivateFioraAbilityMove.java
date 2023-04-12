package com.introvertuoso;

import java.io.Serializable;

public class ActivateFioraAbilityMove extends Move implements Serializable {

  private static final long serialVersionUID = 000;

  private Champion targetChampion;
  private Arena arena;

  @Override
  public void PerformMove() {
    Champion.ChampionAttributes attrs = sourceChampion.getCurrentAttributes();
    attrs.setManaStart(attrs.getManaStart() - attrs.getManaCost());
    int range = (int) sourceChampion.getCurrentAttributes().getVisionRange();
    if (sourceChampion != null && targetChampion != null) {
      if (arena.getBoard()[sourceChampion.getPosX()][sourceChampion.getPosY()].getSquareType()
          == SquareType.GRASS) {
        range /= 2;
      }
      if (Utility.inRange(sourceChampion, targetChampion, range)) {
        duration--;
        abilityState = AbilityState.ACTIVE;
        firstEffect();
        secondEffect();
      }
    }
  }

  private void firstEffect() {
    targetChampion.getStates().add(ChampionState.STUNNED);
  }

  private void secondEffect() {
    sourceChampion.getStates().add(ChampionState.IMMUNE);
  }

  @Override
  public String log() {
    String temp = "";
    if (sourceChampion.getOwner() != null) {
      sourceChampion.getOwner().getName();
    }
    temp += " has ordered " +
            sourceChampion.getCurrentAttributes().getName() +
            " to use ability on " +
            targetChampion.getCurrentAttributes().getName();
    return temp;

//    return sourceChampion.getOwner().getName() +
//        " has ordered " +
//        sourceChampion.getCurrentAttributes().getName() +
//        " to use ability on " +
//        targetChampion.getCurrentAttributes().getName();
  }

  @Override
  public void removeEffects() {
    removeFirstEffect();
    removeSecondEffect();
  }

  private void removeFirstEffect() {
    if (targetChampion != null) {
      targetChampion.getStates().remove(ChampionState.STUNNED);
    }
  }

  private void removeSecondEffect() {
    sourceChampion.getStates().remove(ChampionState.IMMUNE);
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public Champion getTargetChampion() {
    return targetChampion;
  }

  public void setTargetChampion(Champion targetChampion) {
    this.targetChampion = targetChampion;
  }

  public AbilityState getAbilityState() {
    return abilityState;
  }

  public void setAbilityState(AbilityState abilityState) {
    this.abilityState = abilityState;
  }
}
