package com.introvertuoso;

public abstract class Move {

  protected Champion sourceChampion;
  protected int duration = 0;
  protected AbilityState abilityState = AbilityState.INACTIVE;

  public abstract String log();

  public abstract void removeEffects();

  public Champion getSourceChampion() {
    return sourceChampion;
  }

  public void setSourceChampion(Champion sourceChampion) {
    this.sourceChampion = sourceChampion;
  }

  public abstract void PerformMove();

  public AbilityState getAbilityState() {
    return abilityState;
  }

  public void setAbilityState(AbilityState abilityState) {
    this.abilityState = abilityState;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }
}
