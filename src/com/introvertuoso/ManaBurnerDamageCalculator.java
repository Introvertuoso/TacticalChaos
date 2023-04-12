package com.introvertuoso;

import java.io.Serializable;

public class ManaBurnerDamageCalculator extends DamageCalculator implements Serializable {

  private static final long serialVersionUID = 022;

  private int amount;

  @Override
  public double CalculateIntendedDamage() {
    return 1.0;
  }

  public ManaBurnerDamageCalculator(int amount) {
    this.amount = amount;
        this.type = DamageCalculatorType.OUTGOING;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
