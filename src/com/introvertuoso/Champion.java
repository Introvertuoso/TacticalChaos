package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public class Champion implements Serializable {

  private static final long serialVersionUID = 007;

  private int level = 1;
  private int posX, posY;

  private ArrayList<Move> Moves;
  private ArrayList<Item> items = new ArrayList<Item>();
  private ArrayList<DamageCalculator> currentDamageCalculators;
  private ChampionAttributes currentAttributes;
  private ChampionAttributes baseAttributes;

  private Player owner;
  private ArrayList<ChampionState> states;

  private int itemLimit = 3;

  Champion(String[] row) {
    states = new ArrayList<>();
    currentDamageCalculators = new ArrayList<>();
    Moves = new ArrayList<>();
    this.baseAttributes = new ChampionAttributes(row);
    this.currentAttributes = new ChampionAttributes(row);
  }

  public Champion(ChampionAttributes currentAttributes, ChampionAttributes baseAttributes) {
    this.currentAttributes = currentAttributes;
    this.baseAttributes = baseAttributes;
    states = new ArrayList<>();
    currentDamageCalculators = new ArrayList<>();
    Moves = new ArrayList<>();
  }

  @Override
  protected Champion clone() {
    return new Champion(currentAttributes.clone(), baseAttributes.clone());
  }

  public void equipItem(Item item) {
    if (items.size() < itemLimit) {
      items.add(item);

    }
  }

  public void restoreItems() {
    ArrayList<Integer> removed = new ArrayList<>();
    for (int i = 0; i < items.size(); i++) {
      owner.getDroppedItems().add(items.get(i));
      removed.add(i);
    }
    for (int i : removed) {
      items.remove(i);
    }
  }

  public void levelUpAttributes() {
    if (level == 1) {
      level = 2;

      baseAttributes.attackDamage *= 0.1;
      baseAttributes.health *= 0.2;
      baseAttributes.armor *= 0.2;
      baseAttributes.magicResist *= 0.2;

      currentAttributes = baseAttributes.clone();
    } else if (level == 2) {
      level = 3;

      baseAttributes.attackDamage *= 0.15;
      baseAttributes.health *= 0.25;
      baseAttributes.armor *= 0.25;
      baseAttributes.magicResist *= 0.25;

      currentAttributes = baseAttributes.clone();
    }
  }

  public void AcceptDamage(DamageCalculator damage) {
    if (states.contains(ChampionState.IMMUNE)) {
      return;
    } else {
      for (DamageCalculator dc : currentDamageCalculators) {
        if (dc.getType() == DamageCalculatorType.INCOMING) {
          if (Math.random() < dc.getChance()) {
            return;
          }
        }
      }
      if (damage instanceof BasicAttackDamageCalculator) {
        if (((BasicAttackDamageCalculator) damage).getDamageType() == DamageType.TRUE) {
          currentAttributes.health -= damage.CalculateIntendedDamage();
        } else if (((BasicAttackDamageCalculator) damage).getDamageType() == DamageType.BASIC) {
          currentAttributes.health -= damage.CalculateIntendedDamage() *
              (1 - currentAttributes.armor);
        }
        if (currentAttributes.health < 0) {
          currentAttributes.health = 0;
          owner.cleanUp();
        }
      } else if (damage instanceof ManaBurnerDamageCalculator) {
        currentAttributes.manaStart -= ((ManaBurnerDamageCalculator) damage).getAmount();
        if (currentAttributes.health < 0) {
          currentAttributes.health = 0;
          owner.cleanUp();
        }
        currentAttributes.manaStart -= ((ManaBurnerDamageCalculator) damage).getAmount();
        if (currentAttributes.manaStart < 0) {
          currentAttributes.manaStart = 0;
        }
      }
    }
  }

  public void GetIntendedDamage(Champion target) {
    for (DamageCalculator dc : currentDamageCalculators) {
      if (dc.getType() == DamageCalculatorType.OUTGOING) {
        if (dc instanceof BasicAttackDamageCalculator) {
          ((BasicAttackDamageCalculator) dc).setCurrentAttrs(currentAttributes);
        }
        target.AcceptDamage(dc);
      }
    }
    ChampionAttributes attrs = target.getCurrentAttributes();
    attrs.setManaStart(attrs.getManaStart() + 1);
  }

  // Getters and Setters

  public ArrayList<ChampionState> getStates() {
    return states;
  }

  public void setStates(ArrayList<ChampionState> states) {
    this.states = states;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getPosX() {
    return posX;
  }

  public void setPosX(int posX) {
    this.posX = posX;
  }

  public int getPosY() {
    return posY;
  }

  public void setPosY(int posY) {
    this.posY = posY;
  }

  public ArrayList<Move> getMoves() {
    return Moves;
  }

  public void setMoves(ArrayList<Move> moves) {
    Moves = moves;
  }

  public ArrayList<DamageCalculator> getCurrentDamageCalculators() {
    return currentDamageCalculators;
  }

  public void setCurrentDamageCalculators(
      ArrayList<DamageCalculator> currentDamageCalculators) {
    this.currentDamageCalculators = currentDamageCalculators;
  }

  public ChampionAttributes getCurrentAttributes() {
    return currentAttributes;
  }

  public void setCurrentAttributes(ChampionAttributes currentAttributes) {
    this.currentAttributes = currentAttributes;
  }

  public ChampionAttributes getBaseAttributes() {
    return baseAttributes;
  }

  public void setBaseAttributes(ChampionAttributes baseAttributes) {
    this.baseAttributes = baseAttributes;
  }

  public Player getOwner() {
    return owner;
  }

  public void setOwner(Player owner) {
    this.owner = owner;
  }

  public ArrayList<Item> getItems() {
    return items;
  }

  public void setItems(ArrayList<Item> items) {
    this.items = items;
  }

  public int getItemLimit() {
    return itemLimit;
  }

  public void setItemLimit(int itemLimit) {
    this.itemLimit = itemLimit;
  }

  public class ChampionAttributes implements Serializable {

    private String
        name,
        abilityDescription;

    private String
        class1,
        class2,
        class3;

    private double
        health,
        visionRange,
        attackRange,
        attackDamage,
        manaCost,
        movementSpeed,
        manaStart,
        value;

    private double
        criticalStrikeChance,
        criticalStrikeDamage,
        armor,
        magicResist;

    public ChampionAttributes(String[] row) {
      this.name = row[0].toUpperCase();
      this.class1 = row[1].toUpperCase();
      this.class2 = row[2].toUpperCase();
      this.class3 = row[3].toUpperCase();
      this.value = Double.parseDouble(row[4]);
      this.health = Double.parseDouble(row[5]);
      this.armor = Double.parseDouble(row[6]);
      this.magicResist = Double.parseDouble(row[7]);
      this.visionRange = Double.parseDouble(row[8]);
      this.attackRange = Double.parseDouble(row[9]);
      this.attackDamage = Double.parseDouble(row[10]);
      this.movementSpeed = Double.parseDouble(row[11]);
      this.criticalStrikeChance = Double.parseDouble(row[12]);
      this.criticalStrikeDamage = Double.parseDouble(row[13]);
      this.manaStart = Double.parseDouble(row[14]);
      this.manaCost = Double.parseDouble(row[15]);
      this.abilityDescription = row[16];
    }

    public ChampionAttributes(String name, String abilityDescription, double health,
        double visionRange, double attackRange, double attackDamage, double manaCost,
        double movementSpeed, double manaStart, double value, String class1, String class2,
        String class3, double criticalStrikeChance, double criticalStrikeDamage, double armor,
        double magicResist) {
      this.name = name;
      this.abilityDescription = abilityDescription;
      this.health = health;
      this.visionRange = visionRange;
      this.attackRange = attackRange;
      this.attackDamage = attackDamage;
      this.manaCost = manaCost;
      this.movementSpeed = movementSpeed;
      this.manaStart = manaStart;
      this.value = value;
      this.class1 = class1;
      this.class2 = class2;
      this.class3 = class3;
      this.criticalStrikeChance = criticalStrikeChance;
      this.criticalStrikeDamage = criticalStrikeDamage;
      this.armor = armor;
      this.magicResist = magicResist;
    }

    @Override
    protected ChampionAttributes clone() {
      return new ChampionAttributes(name, abilityDescription, health, visionRange, attackRange,
          attackDamage, manaCost, movementSpeed, manaStart, value, class1, class2, class3,
          criticalStrikeChance, criticalStrikeDamage, armor, magicResist);
    }

    public String getAbilityDescription() {
      return abilityDescription;
    }

    public void setAbilityDescription(String abilityDescription) {
      this.abilityDescription = abilityDescription;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public double getHealth() {
      return health;
    }

    public void setHealth(double health) {
      this.health = health;
    }

    public double getArmor() {
      return armor;
    }

    public void setArmor(double armor) {
      this.armor = armor;
    }

    public double getMagicResist() {
      return magicResist;
    }

    public void setMagicResist(double magicResist) {
      this.magicResist = magicResist;
    }

    public double getVisionRange() {
      return visionRange;
    }

    public void setVisionRange(double visionRange) {
      this.visionRange = visionRange;
    }

    public double getAttackRange() {
      return attackRange;
    }

    public void setAttackRange(double attackRange) {
      this.attackRange = attackRange;
    }

    public double getAttackDamage() {
      return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
      this.attackDamage = attackDamage;
    }

    public double getManaCost() {
      return manaCost;
    }

    public void setManaCost(double manaCost) {
      this.manaCost = manaCost;
    }

    public double getMovementSpeed() {
      return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed) {
      this.movementSpeed = movementSpeed;
    }

    public double getManaStart() {
      return manaStart;
    }

    public void setManaStart(double manaStart) {
      this.manaStart = manaStart;
    }

    public double getValue() {
      return value;
    }

    public void setValue(double value) {
      this.value = value;
    }

    public String getClass1() {
      return class1;
    }

    public void setClass1(String class1) {
      this.class1 = class1;
    }

    public String getClass2() {
      return class2;
    }

    public void setClass2(String class2) {
      this.class2 = class2;
    }

    public String getClass3() {
      return class3;
    }

    public void setClass3(String class3) {
      this.class3 = class3;
    }

    public double getCriticalStrikeChance() {
      return criticalStrikeChance;
    }

    public void setCriticalStrikeChance(double criticalStrikeChance) {
      this.criticalStrikeChance = criticalStrikeChance;
    }

    public double getCriticalStrikeDamage() {
      return criticalStrikeDamage;
    }

    public void setCriticalStrikeDamage(double criticalStrikeDamage) {
      this.criticalStrikeDamage = criticalStrikeDamage;
    }
  }
}
