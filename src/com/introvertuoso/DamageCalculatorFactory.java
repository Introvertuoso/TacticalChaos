package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class DamageCalculatorFactory implements Serializable {

   private static final long serialVersionUID = 012;

   private ArrayList<Champion> current;
   private int[] buffs = new int[InGameStore.accepted.size()];

   private ArrayList<DamageCalculator> damageCalculators;

   public void CalculateClasses() {
      Arrays.fill(buffs, 0);
      ArrayList<String> accepted = InGameStore.accepted;
      for (Champion i : current) {
         int j = accepted.indexOf(i.getCurrentAttributes().getClass1());
         if (j != -1) {
            buffs[j] += 1;
         }
         j = accepted.indexOf(i.getCurrentAttributes().getClass2());
         if (j != -1) {
            buffs[j] += 1;
         }

         if (!i.getCurrentAttributes().getClass3().equals(ChampionClass.NONE)) {
            j = accepted.indexOf(i.getCurrentAttributes().getClass3());
            if (j != -1) {
               buffs[j] += 1;
            }
         }
         CalculateItemClasses(i);
      }
   }

   private void CalculateItemClasses(Champion c) {
//      ArrayList<String> accepted = InGameStore.accepted;
      if (!c.getItems().isEmpty()) {
         for (Item i : c.getItems()) {
            int j = InGameStore.accepted.indexOf(i.getNewClass());
            if (j != -1) {
               buffs[j] += 1;
            }
         }
      }
   }

   private void GenerateItemBuffs(Champion c) {
      if (!c.getItems().isEmpty()) {
         for (Item i : c.getItems()) {
            if (i.getCorrAttribute().equals("PHYSICALDAMAGE")) {
               c.getBaseAttributes()
                       .setAttackDamage(c.getBaseAttributes().getAttackDamage() + i.getAmount());
            } else if (i.getCorrAttribute().equals("ARMOR")) {
               c.getBaseAttributes()
                       .setArmor(c.getBaseAttributes().getArmor() + i.getAmount());
            } else if (i.getCorrAttribute().equals("CRITICALSTRIKECHANCE")) {
               c.getBaseAttributes()
                       .setCriticalStrikeChance(
                               c.getBaseAttributes().getCriticalStrikeChance() + i.getAmount());
            } else if (i.getCorrAttribute().equals("HEALTH")) {
               c.getBaseAttributes()
                       .setHealth(c.getBaseAttributes().getHealth() + i.getAmount());
            } else if (i.getCorrAttribute().equals("MAGICRESIST")) {
               c.getBaseAttributes()
                       .setMagicResist(c.getBaseAttributes().getMagicResist() + i.getAmount());
            }
         }
      }
   }

   public void GenerateDamageCalculators() {
      ArrayList<String> accepted = InGameStore.accepted;

      for (Champion c : current) {
         GenerateItemBuffs(c);
         c.setCurrentDamageCalculators(new ArrayList<>());
         BasicAttackDamageCalculator dc = new BasicAttackDamageCalculator();
         addGeneratedDamageCalculatorToChampion(c, dc);
      }

      double chance;
      if (accepted.contains(ChampionClass.YORDLE) &&
              buffs[accepted.indexOf(ChampionClass.YORDLE)] >= 2) {
         if (buffs[accepted.indexOf(ChampionClass.YORDLE)] >= 4) {
            if (buffs[accepted.indexOf(ChampionClass.YORDLE)] >= 6) {
               chance = 0.5;
            } else {
               chance = 0.3;
            }
         } else {
            chance = 0.15;
         }
         for (Champion c : current) {
            if (findClassInChampion(c, ChampionClass.YORDLE)) {
               BasicAttackDodgeDamageCalculator basicAttackDodgeDamageCalculator =
                       new BasicAttackDodgeDamageCalculator();
               basicAttackDodgeDamageCalculator.setChance(chance);
               addGeneratedDamageCalculatorToChampion(c, basicAttackDodgeDamageCalculator);
            }
         }
      }

      int amount;
      if (accepted.contains(ChampionClass.DEMON) &&
              buffs[accepted.indexOf(ChampionClass.DEMON)] >= 2) {
         if (buffs[accepted.indexOf(ChampionClass.DEMON)] >= 4) {
            amount = 40;
         } else {
            amount = 20;
         }
         for (Champion c : current) {
            if (findClassInChampion(c, ChampionClass.DEMON)) {
               ManaBurnerDamageCalculator manaBurnerDamageCalculator =
                       new ManaBurnerDamageCalculator(amount);
               addGeneratedDamageCalculatorToChampion(c, manaBurnerDamageCalculator);
            }
         }
      }

      int level = 0;
      if (accepted.contains(ChampionClass.VOID) &&
              buffs[accepted.indexOf(ChampionClass.VOID)] >= 2) {
         if (buffs[accepted.indexOf(ChampionClass.VOID)] >= 4) {
            level += 2;
         } else {
            level += 1;
         }
         for (Champion c : current) {
            if (findClassInChampion(c, ChampionClass.VOID)) {
               BasicAttackDamageCalculator dc =
                       (BasicAttackDamageCalculator) c.getCurrentDamageCalculators().get(0);
               if (level == 1) {
                  dc.setDamageType(DamageType.TRUE);
                  level = 0;
               } else if (level == 2) {
                  dc.setDamageType(DamageType.TRUE);
               }
            }
         }
      }
   }

   private void addGeneratedDamageCalculatorToChampion(Champion c, DamageCalculator dc) {
      c.getCurrentDamageCalculators().add(dc);
   }

   private boolean findClassInChampion(Champion c, String s) {
      if (c.getCurrentAttributes().getClass1().equals(s)) {
         return true;
      } else if (c.getCurrentAttributes().getClass2().equals(s)) {
         return true;
      } else {
         return c.getCurrentAttributes().getClass3().equals(s);
      }
   }

   public void printActiveClasses() {
      for (int i = 0; i < buffs.length; i++) {
         if (buffs[i] >= 1) {
            System.out.print(InGameStore.accepted.get(i) + " ");
         }
      }
      System.out.println();
   }

   public ArrayList<DamageCalculator> getDamageCalculators() {
      return damageCalculators;
   }

   public void setDamageCalculators(ArrayList<DamageCalculator> damageCalculators) {
      this.damageCalculators = damageCalculators;
   }

   public ArrayList<Champion> getCurrent() {
      return current;
   }

   public void setCurrent(ArrayList<Champion> current) {
      this.current = current;
   }

   public int[] getBuffs() {
      return buffs;
   }

   public void setBuffs(int[] buffs) {
      this.buffs = buffs;
   }
}