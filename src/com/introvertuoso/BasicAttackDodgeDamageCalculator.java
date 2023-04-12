package com.introvertuoso;

import java.io.Serializable;

public class BasicAttackDodgeDamageCalculator extends DamageCalculator implements Serializable {

  private static final long serialVersionUID = 003;

  public BasicAttackDodgeDamageCalculator() {
    this.type = DamageCalculatorType.INCOMING;
  }

  @Override
  public double CalculateIntendedDamage() {
    return 1.0;
  }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}