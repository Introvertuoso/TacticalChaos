package com.introvertuoso;

import java.io.Serializable;

public class BasicAttackDamageCalculator extends DamageCalculator implements Serializable {

  private static final long serialVersionUID = 002;

  private DamageType damageType = DamageType.BASIC;
  private Champion.ChampionAttributes currentAttrs;
  private double intendedDamage = 1.0;

  @Override
  public double CalculateIntendedDamage() {
    double res;
    double criticalChance = Math.random();
    if (criticalChance <= currentAttrs.getCriticalStrikeChance()) {
          res = currentAttrs.getCriticalStrikeDamage() * currentAttrs.getAttackDamage();
        }
        else {
            res = currentAttrs.getAttackDamage();
        }
        intendedDamage = res;
        return res;
    }

    public BasicAttackDamageCalculator() {
        this.type = DamageCalculatorType.OUTGOING;
    }

    // Getters and Setters

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public double getIntendedDamage() {
        return intendedDamage;
    }

    public void setIntendedDamage(double intendedDamage) {
        this.intendedDamage = intendedDamage;
    }

    public Champion.ChampionAttributes getCurrentAttrs() {
        return currentAttrs;
    }

    public void setCurrentAttrs(Champion.ChampionAttributes currentAttrs) {
        this.currentAttrs = currentAttrs;
    }
}