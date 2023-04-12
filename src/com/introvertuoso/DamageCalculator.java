package com.introvertuoso;

public abstract class DamageCalculator {

  protected double chance = 1.0;
  protected DamageCalculatorType type = DamageCalculatorType.NONE;

  public abstract double CalculateIntendedDamage();

  public double getChance() {
    return chance;
  }

  public void setChance(double chance) {
    this.chance = chance;
  }

  public DamageCalculatorType getType() {
    return type;
  }

  public void setType(DamageCalculatorType type) {
    this.type = type;
  }
}