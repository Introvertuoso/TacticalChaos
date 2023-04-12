package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public class SellMove extends Move implements Serializable {

   private static final long serialVersionUID = 027;

   private int fromWhere;
   private Arena arena;

   @Override
   public void PerformMove() {
      Player owner = getSourceChampion().getOwner();
      // From inventory
      if (this.fromWhere == 0) {
         ArrayList<Champion> inv = owner.getInventory();
         inv.remove(sourceChampion);
      }
      // From board
      if (this.fromWhere == 1) {
         if (sourceChampion != null) {
            ArrayList<Champion> onBoard = owner.getChampsOnBoard();
            onBoard.remove(sourceChampion);
            arena.getBoard()[sourceChampion.getPosX()][sourceChampion.getPosY()]
                    .getContents().remove(sourceChampion);
         }
      }
      owner.setBalance(owner.getBalance() + (int) sourceChampion.getCurrentAttributes().getValue());
      InGameStore.inGameStore.add(sourceChampion);
      sourceChampion.restoreItems();
   }

   @Override
   public String log() {
      String temp = "";
      temp += ConsoleColors.WHITE_BRIGHT;
      if (sourceChampion.getOwner() != null) {
         temp += sourceChampion.getOwner().getName();
      }
      temp +=  " sold "
              + sourceChampion.getCurrentAttributes().getName()
              + ConsoleColors.RESET;
      return temp;
//      return ConsoleColors.WHITE_BRIGHT
//              + sourceChampion.getOwner().getName()
//              + " sold "
//              + sourceChampion.getCurrentAttributes().getName()
//              + ConsoleColors.RESET;
   }

   @Override
   public void removeEffects() {
   }

   public int getFromWhere() {
      return fromWhere;
   }

   public void setFromWhere(int fromWhere) {
      this.fromWhere = fromWhere;
   }

   public Arena getArena() {
      return arena;
   }

   public void setArena(Arena arena) {
      this.arena = arena;
   }
}
