package com.introvertuoso;

import com.introvertuoso.Game.GameAttributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import static com.introvertuoso.Utility.direction;
import static com.introvertuoso.Utility.movementDirection;

public class MoveFactory implements Serializable {

   private static final long serialVersionUID = 023;

   private Player owner;
   private ArrayList<Move> moves;


   public boolean GetMovesFromConsole(ArrayList<Move> movesAtTheMoment,
                                      GamePhase phase, Game.GameAttributes attributes,
                                      TemporalStoreFilter filter, ArrayList<Player> remainingPlayers,
                                      Arena arena) {

      moves = new ArrayList<>();
      ArrayList<Move> tempListOfMoves = new ArrayList();

      String[] warmupMenu = {
              "(1) - Buy a champion from the store.",
              "(2) - Place a champion on the arena.",
              "(0) - End turn.",
              "(-1) - Exit."};

      String[] executionMenu = {
              "(1) - Buy a champion from the store.",
              "(2) - Place a champion on the arena.",
              "(3) - Sell a champion.",
              "(4) - Walk a champion in the arena.",
              "(5) - Attack an enemy champion in the arena.",
              "(6) - Swap 2 champions.",
              "(7) - Equip Item.",
              "(0) - End turn.",
              "(-1) - Exit."};

      while (true) {

         System.out.print("Player Name: " + owner.getName() + " - " +
                 "Balance: " + owner.getBalance() + " - ");
         System.out.print("Active Classes: ");
         owner.getDamageCalculatorFactory().printActiveClasses();
         System.out.println();

         if (!owner.getInventory().isEmpty()) {
            System.out.println(ConsoleColors.GREEN
                    + owner.getName()
                    + "'s Inventory: "
                    + ConsoleColors.RESET);
            for (Champion c : owner.getInventory()) {
               System.out.println(ConsoleColors.GREEN_BOLD
                       + c.getCurrentAttributes().getName()
                       + " - "
                       + "Health:"
                       + " "
                       + c.getCurrentAttributes().getHealth()
                       + " - "
                       + "Level:"
                       + " "
                       + c.getLevel()
                       + ConsoleColors.RESET);
            }
         }
         if (!owner.getDroppedItems().isEmpty()) {
            System.out.println(ConsoleColors.YELLOW_BOLD
                    + "Items Inventory: "
                    + ConsoleColors.RESET);
            for (Item i : owner.getDroppedItems()) {
               System.out.println(ConsoleColors.YELLOW_BOLD
                       + "Item Name: "
                       + i.getName()
                       + " - "
                       + "Buff:"
                       + " +"
                       + i.getAmount() * 100 + "% " + i.getCorrAttribute()
                       + " - "
                       + "Class:"
                       + " "
                       + i.getNewClass()
                       + ConsoleColors.RESET);
            }
            System.out.println();
         }

         if (phase == GamePhase.WARMUP) {
            arena.printArena(phase, arena, owner);

            for (String menu : warmupMenu) {
               System.out.println(menu);
            }

            int input = getInputFromUser(warmupMenu.length, true, false);

            if (input == 0) {
               break;
            }

            if (input == -1) {
               return false;
            }
            if (input == 1) {
               BuyMove bm = getBuyMove(attributes, filter);
               if (bm != null) {
                  bm.PerformMove();
                  movesAtTheMoment.add(bm);
               }
            }
            if (input == 2) {
               PlaceMove pm = getPlaceMove(attributes, arena);
               if (pm != null) {
                  pm.PerformMove();
                  movesAtTheMoment.add(pm);
               }
            }
         } else if (phase == GamePhase.BATTLE) {

            arena.printArena(phase, arena, owner);

            for (String menu : executionMenu) {
               System.out.println(menu);
            }

            int input = getInputFromUser(executionMenu.length, true, false);

            if (input == 0) {
               moves = tempListOfMoves;
               break;
            }
            if (input == -1) {
               return false;
            }
            if (input == 1) {
               BuyMove buyMove = getBuyMove(attributes, filter);
               if (buyMove != null) {
                  buyMove.PerformMove();
                  movesAtTheMoment.add(buyMove);
               }
            }
            if (input == 2) {
               PlaceMove placeMove = getPlaceMove(attributes, arena);
               if (placeMove != null) {
                  placeMove.PerformMove();
                  movesAtTheMoment.add(placeMove);
               }
            }
            if (input == 3) {
               SellMove sellMove = getSellMove(arena);
               if (sellMove != null) {
                  sellMove.PerformMove();
                  movesAtTheMoment.add(sellMove);
               }
            }
            if (input == 4) {
               WalkMove walkMove = getWalkMove(attributes, arena);
               if (walkMove != null) {
                  tempListOfMoves.add(walkMove);
                  movesAtTheMoment.add(walkMove);
                  walkMove.sourceChampion.getMoves().add(walkMove);
               }
            }
            if (input == 5) {
               BasicAttackMove basicAttackMove = getBasicAttackMove(remainingPlayers);
               if (basicAttackMove != null) {
                  tempListOfMoves.add(basicAttackMove);
                  movesAtTheMoment.add(basicAttackMove);
                  basicAttackMove.sourceChampion.getMoves().add(basicAttackMove);
               }
            }
            if (input == 6) {
               SwapMove swapMove = getSwapMove(arena);
               if (swapMove != null) {
                  swapMove.PerformMove();
                  movesAtTheMoment.add(swapMove);
               }
            }
            if (input == 7) {
               Utility.Pair itemChampionPair = getEquipItem();
               if (itemChampionPair != null) {
                  ((Champion) itemChampionPair.getValue())
                          .equipItem((Item) itemChampionPair.getKey());
                  owner.getDroppedItems().remove(itemChampionPair.getKey());
               }
            }
         }
      }
      return true;
   }

   public ArrayList<Move> GetMovesFromGUI() {
      return new ArrayList<>();
   }

   private BuyMove getBuyMove(GameAttributes attributes, TemporalStoreFilter filter) {
      if (owner.getInventory().size() < attributes.getMaxBenchSize()) {

         BuyMove move = new BuyMove();
         ArrayList<Champion> list = filter.getChosen();

         for (int i = 0; i < list.size(); i++) {
            Champion.ChampionAttributes cAtrrs = list.get(i).getCurrentAttributes();
            System.out.println(
                    ConsoleColors.BLUE
                            + i + " - "
                            + cAtrrs.getName()
                            + " ("
                            + cAtrrs.getClass1()
                            + " " + cAtrrs.getClass2()
                            + " " + cAtrrs.getClass3()
                            + ")"
                            + ConsoleColors.CYAN
                            + " Value : " + cAtrrs.getValue()
                            + ConsoleColors.RESET

            );
         }
         System.out.println("(Press -1 to go back)");

         int input = getInputFromUser(list.size(), true, false);

         if (input == -1) {
            return null;
         }

         if (owner.getBalance() -
                 list.get(input).getCurrentAttributes().getValue() >= 0) {

            move.setSourceChampion(filter.getOnly(input));
            move.getSourceChampion().setOwner(owner);

            System.out.println(move.log());

            return move;
         } else {
            System.out.println("Insufficient funds.");
            return null;
         }
      }
      System.out.println("Bench is full");
      return null;
   }

   private PlaceMove getPlaceMove(GameAttributes attributes, Arena arena) {
      if (!owner.getInventory().isEmpty() &&
              owner.getChampsOnBoard().size() < attributes.getMaxTeamSize()) {

         PlaceMove move = new PlaceMove();

         move.setArena(arena);

         ArrayList<Champion> inventory = owner.getInventory();

         int arenaSize = attributes.getArenaSize();
         for (int i = 0; i < inventory.size(); i++) {
            System.out.println(
                    ConsoleColors.YELLOW
                            + i + "- " + inventory.get(i).getCurrentAttributes().getName()
                            + "( " + inventory.get(i).getLevel() + " )"
                            + ConsoleColors.RESET);
         }
         System.out.println("(Press -1 to go back)");

         int input = getInputFromUser(inventory.size(), true, false);
         if (input == -1) {
            return null;
         }
         move.setSourceChampion(
                 inventory.get(input));

         System.out.println(ConsoleColors.YELLOW +
                 "Enter x Position of destination tile: "
                 + ConsoleColors.RESET);

         int x = getInputFromUser(arenaSize, true, false);

         if (x == -1) {
            return null;
         }

         System.out.println(ConsoleColors.YELLOW
                 + "Enter y Position of destination tile: "
                 + ConsoleColors.RESET);

         int y = getInputFromUser(arenaSize, true, false);

         if (y == -1) {
            return null;
         }

         Utility.Pair temp = coorProjection(x, y, arenaSize);
         Square destination = arena.getBoard()[(int)temp.getKey()][(int)temp.getValue()];

         move.setDestination(destination);

         System.out.println(move.log());

         return move;
      } else {
         System.out.println("Inventory is empty or maxTeamSize reached.");
         return null;
      }
   }

   private SellMove getSellMove(Arena arena) {
      if (!owner.getInventory().isEmpty() || !owner.getChampsOnBoard().isEmpty()) {
         SellMove move = new SellMove();
         move.setArena(arena);
         ArrayList<Champion> inventory = owner.getInventory();
         ArrayList<Champion> onBoard = owner.getChampsOnBoard();
         System.out.println("Inventory Champions (-2 to skip)");
         for (int i = 0; i < inventory.size(); i++) {
            System.out.println(i + "- " + inventory.get(i).getCurrentAttributes().getName());
         }
         System.out.println("(Press -1 to go back)");

         int input = getInputFromUser(inventory.size(), true, true);

         if (input == -1) {
            return null;
         } else if (input == -2) {
            for (int i = 0; i < onBoard.size(); i++) {
               System.out.println(i + "- " + onBoard.get(i).getCurrentAttributes().getName());
            }
            System.out.println("(Press -1 to go back (main menu))");
            int input1 = getInputFromUser(onBoard.size(), true, false);
            if (input1 == -1) {
               return null;
            }
            move.setFromWhere(1);
            move.setSourceChampion(onBoard.get(input1));
         } else {
            move.setFromWhere(0);
            move.setSourceChampion(inventory.get(input));
         }

         System.out.println(move.log());

         return move;

      } else {
         System.out.println("There's nothing to sell.");
         return null;
      }

   }

   private WalkMove getWalkMove(Game.GameAttributes attributes, Arena arena) {

      WalkMove move = new WalkMove();

      move.setArena(arena);
      move.setLimit(attributes.getArenaSize());

      ArrayList<Champion> onBoard = owner.getChampsOnBoard();

      for (int i = 0; i < onBoard.size(); i++) {

         if (allowMove(onBoard.get(i).getMoves(), 3)) {
            System.out.println(i + "- " + onBoard.get(i).getCurrentAttributes().getName()
                    + "( " + onBoard.get(i).getLevel() + " )");
         }
      }
      System.out.println("(Press -1 to go back)");

      int input = getInputFromUser(onBoard.size(), true, false);

      if (input == -1) {
         return null;
      }
      move.setSourceChampion(onBoard.get(input));

      System.out.println("Please select which direction you want to move your selected champion.. ");

      for (Square k : movementDirection) {
         System.out.print(movementDirection.indexOf(k));
         System.out.println("- " + direction[movementDirection.indexOf(k)]);
      }
      System.out.println("(Press -1 to go back (main menu))");

      int directionInput = getInputFromUser(direction.length, true, false);

      if (input == -1) {
         return null;
      }

      move.setDirection(movementDirection.get(directionInput));

      move.setDirectionString(direction[directionInput]);

      System.out.println(move.log());

      return move;
   }

   private BasicAttackMove getBasicAttackMove(ArrayList<Player> remainingPlayers) {

      BasicAttackMove move = new BasicAttackMove();

      if (!owner.getChampsOnBoard().isEmpty()) {

         ArrayList<Champion> onBoard = owner.getChampsOnBoard();

         System.out.println("Pick attacker Champion from arena: ");
         ArrayList<Champion> temp = new ArrayList<>();
         for (int i = 0; i < onBoard.size(); i++) {

            if (allowMove(onBoard.get(i).getMoves(), 3)) {
//               System.out.println(i + "- " + onBoard.get(i).getCurrentAttributes().getName());
               temp.add(onBoard.get(i));
            }
         }
         System.out.println("(Press -1 to go back)");

         int input = getInputFromUser(onBoard.size(), true, false);

         if (input == -1) {
            return null;
         }
         move.setSourceChampion(onBoard.get(input));
      } else {
         System.out.println("You do not own any champions");
         return null;
      }

      System.out.println("Pick the player that you wanna attack: ");

      ArrayList<Player> tempPlayers = remainingPlayers;
      tempPlayers.remove(owner);

      for (int i = 0; i < tempPlayers.size(); i++) {
         System.out.println(i + "-" + tempPlayers.get(i).getName());
      }
      System.out.println("(Press -1 to go back)");

      int input1 = getInputFromUser(tempPlayers.size(), true, false);

      if (input1 == -1) {
         return null;
      }

      if (!remainingPlayers.get(input1).getChampsOnBoard().isEmpty()) {

         ArrayList<Champion> temp = remainingPlayers.get(input1).getChampsOnBoard();

         for (int i = 0; i < temp.size(); i++) {
            if (Utility.inRange(move.getSourceChampion(), temp.get(i),
                    move.getSourceChampion().getCurrentAttributes().getAttackRange())) {
               System.out.println(i + "- " +
                       remainingPlayers.get(input1).getChampsOnBoard().get(i).getCurrentAttributes()
                               .getName());
            }
         }
         System.out.println("(Press -1 to go back)");

         int input2 = getInputFromUser(tempPlayers.size(), true, false);

         if (input2 == -1) {
            return null;
         }

         move.setTargetChampion(remainingPlayers.get(input1)
                 .getChampsOnBoard().get(input2));

         System.out.println(move.log());
         return move;
      } else {
         System.out.println(remainingPlayers.get(input1).getName() +
                 " owns no champions. Pick another player..");
         return null;
      }
   }

   private SwapMove getSwapMove(Arena arena) {
      if (!owner.getInventory().isEmpty() && !owner.getChampsOnBoard().isEmpty()) {
         SwapMove move = new SwapMove();
         move.setArena(arena);
         ArrayList<Champion> inventory = owner.getInventory();
         ArrayList<Champion> onBoard = owner.getChampsOnBoard();
         System.out.println("Pick source Champions from inventory");
         for (int i = 0; i < inventory.size(); i++) {
            System.out.println(i + "- " + inventory.get(i).getCurrentAttributes().getName());
         }
         System.out.println("(Press -1 to go back)");

         int input1 = getInputFromUser(inventory.size(), true, false);

         if (input1 == -1) {
            return null;
         }

         move.setSourceChampion(inventory.get(input1));

         System.out.println("Pick target Champions from arena");

         for (int i = 0; i < onBoard.size(); i++) {
            System.out.println(i + "- " + onBoard.get(i).getCurrentAttributes().getName());
         }

         System.out.println("(Press -1 to go back (main menu))");

         int input2 = getInputFromUser(onBoard.size(), true, false);

         if (input2 == -1) {
            return null;
         }

         move.setTarget(onBoard.get(input2));

         System.out.println(move.log());

         return move;
      } else {
         System.out.println("You own no champions.");
         return null;
      }
   }

   private Utility.Pair getEquipItem() {
      if (!owner.getDroppedItems().isEmpty() && !owner.getChampsOnBoard().isEmpty()) {

         ArrayList<Item> items = owner.getDroppedItems();

         System.out.println("Select the item you want to equip: ");

         for (int i = 0; i < items.size(); i++) {
            System.out.println(i + "- " + items.get(i).getName()
                    + "( " + items.get(i).getCorrAttribute() + " )");
         }
         System.out.println("(Press -1 to go back)");

         int input = getInputFromUser(items.size(), true, false);
         if (input == -1) {
            return null;
         }
         Item item = items.get(input);

         ArrayList<Champion> champions = owner.getChampsOnBoard();

         System.out.println("Select the champion that you want equip onto: ");

         for (int i = 0; i < champions.size(); i++) {
            System.out.println(i + "- " + champions.get(i).getCurrentAttributes().getName());
         }
         System.out.println("(Press -1 to go back (main menu))");

         int input1 = getInputFromUser(champions.size(), true, false);
         if (input1 == -1) {
            return null;
         }
         Champion champion = champions.get(input1);

         return new Utility.Pair(item, champion);
      } else {
         System.out.println("You don't own any champions/items.");
         return null;
      }
   }


   public boolean GetMovesFromBot(ArrayList<Move> movesAtTheMoment,
                                  int difficulty, GamePhase phase, Game.GameAttributes attributes,
                                  TemporalStoreFilter filter, ArrayList<Player> remainingPlayers,
                                  Arena arena) {
      moves = new ArrayList<>();
      if (difficulty == 1) {
         level1Bot(movesAtTheMoment, phase, attributes, filter, remainingPlayers, arena);
      } else if (difficulty == 2) {
      }
      return true;
   }

   private void level1Bot(ArrayList<Move> movesAtTheMoment,
                          GamePhase phase, Game.GameAttributes attributes,
                          TemporalStoreFilter filter, ArrayList<Player> remainingPlayers,
                          Arena arena) {

      ArrayList tempListOfMoves = new ArrayList();
      if (phase == GamePhase.WARMUP) {
         if (owner.getInventory().isEmpty()
                 && owner.getInventory().size() < attributes.getMaxTeamSize()) {
            ArrayList<Champion> temp = filter.getChosen();
            for (int i = 0; i < temp.size(); i++) {
               Random r1 = new Random();
               double rand1 = r1.nextDouble();
               if (temp.get(i).getCurrentAttributes().getValue() <= owner.getBalance() &&
                       rand1 < 0.8) {
                  BuyMove move = new BuyMove();
                  move.setSourceChampion(filter.getOnly(i));
                  move.getSourceChampion().setOwner(owner);
                  move.PerformMove();
                  movesAtTheMoment.add(move);
                  System.out.println(move.log());
               }
            }
         } else {
            Random r2 = new Random();
            double chance2 = r2.nextDouble();
            if (chance2 < 0.8) {
               Random r3 = new Random();
               int index = r3.nextInt(owner.getInventory().size());
               PlaceMove move = new PlaceMove();
               move.setArena(arena);
               Random rx = new Random();
               Random ry = new Random();
               move.setDestination(arena.getBoard()[
                       rx.nextInt(attributes.getArenaSize())][ry.nextInt(attributes.getArenaSize())]);
               move.setSourceChampion(owner.getInventory().get(index));
               move.getSourceChampion().setOwner(owner);
               move.PerformMove();
               movesAtTheMoment.add(move);
               System.out.println(move.log());
            }
         }
      } else if (phase == GamePhase.BATTLE) {

         arena.printArena(phase, arena, owner);

         // FIXME here is the equipping code for bot (nothing to fix just saying)
         if (!owner.getDroppedItems().isEmpty()) {
            ArrayList<Item> discarded = new ArrayList<>();
            for (Item i : owner.getDroppedItems()) {
               int index234;
               do {
                  Random rand213 = new Random();
                  index234 = rand213.nextInt(owner.getChampsOnBoard().size());
               } while (!owner.getChampsOnBoard().get(index234).getItems().isEmpty());
               owner.getChampsOnBoard().get(index234).equipItem(i);
               discarded.add(i);
            }
            for (Item i : discarded) {
               owner.getDroppedItems().remove(i);
            }
         }

         if (owner.getInventory().size() > 0 && owner.getInventory().size() < attributes
                 .getMaxBenchSize()) {
            if (owner.getChampsOnBoard().size() + 2 < attributes.getMaxTeamSize()) {
               Random r6 = new Random();
               double chance6 = r6.nextDouble();
               if (chance6 < 0.8) {
                  for (int i = 0; i < filter.getChosen().size(); i++) {
                     if (filter.getChosen().get(i).getCurrentAttributes().getValue() <= owner
                             .getBalance()) {
                        BuyMove move = new BuyMove();
                        move.setSourceChampion(filter.getOnly(i));
                        move.getSourceChampion().setOwner(owner);
                        move.PerformMove();
                        movesAtTheMoment.add(move);
                        System.out.println(move.log());
                     }
                  }
               }
            }
            Random r4 = new Random();
            double chance4 = r4.nextDouble();
            if (chance4 < 0.95) {
               Random r5 = new Random();
               int index5 = r5.nextInt(owner.getInventory().size());
               PlaceMove move = new PlaceMove();
               move.setArena(arena);
               Random rx = new Random();
               Random ry = new Random();
               move.setDestination(arena.getBoard()[
                       rx.nextInt(attributes.getArenaSize())][ry.nextInt(attributes.getArenaSize())]);
               move.setSourceChampion(owner.getInventory().get(index5));
               move.getSourceChampion().setOwner(owner);
               move.PerformMove();
               movesAtTheMoment.add(move);
               System.out.println(move.log());
            }
         }
         for (int i = 0; i < owner.getChampsOnBoard().size(); i++) {
            ArrayList<Champion> list = owner.getChampsOnBoard();
            if (allowMove(list.get(i).getMoves(), 3)) {
               int counter=0;
               Random r8 = new Random();
               double chance8 = r8.nextDouble();
               if (chance8 < 0.8) {
                  Random r9 = new Random();
                  int index9 = r9.nextInt(2);
                  if (index9 == 0 ) {
                     BasicAttackMove move = new BasicAttackMove();
                     move.setSourceChampion(owner.getChampsOnBoard().get(i));
                     move.getSourceChampion().setOwner(owner);
                     for (Player p : remainingPlayers) {
                        if (p != owner) {
                           for (Champion c : p.getChampsOnBoard()) {
                              if (counter < 3 &&
                                      Utility.inRange(list.get(i),
                                      c,
                                      list.get(i).getCurrentAttributes().getAttackRange())) {
                                 move.setTargetChampion(c);
                                 tempListOfMoves.add(move);
                                 movesAtTheMoment.add(move);
                                 move.getSourceChampion().getMoves().add(move);
                                 counter++;
                                 System.out.println(move.log());
                              }
                           }
                        }
                     }
                  } else if (index9 == 1) {

                     Random r0 = new Random();
                     int index0 = r0.nextInt(movementDirection.size());
                     WalkMove move = new WalkMove();
                     move.setDirection(movementDirection.get(index0));
                     move.setDirectionString(direction[index0]);
                     move.setLimit(attributes.getArenaSize());
                     move.setArena(arena);
                     move.setSourceChampion(owner.getChampsOnBoard().get(i));
                     move.getSourceChampion().setOwner(owner);
                     tempListOfMoves.add(move);
                     movesAtTheMoment.add(move);
                     move.getSourceChampion().getMoves().add(move);
                     counter++;
                     System.out.println(move.log());
                  }
               }
            }
         }
      }
      moves = tempListOfMoves;
   }

   public Player getOwner() {
      return owner;
   }

   public void setOwner(Player owner) {
      this.owner = owner;
   }

   public ArrayList<Move> getMoves() {
      return moves;
   }

   public void setMoves(ArrayList<Move> moves) {
      this.moves = moves;
   }

   private boolean allowMove(ArrayList<Move> moves, int limit) {
      int c = 0;
      for (Move m : moves) {
         if (m.getAbilityState() == AbilityState.INACTIVE) {
            c++;
         }
      }
      return c < limit;
   }

   private int getInputFromUser(int size, boolean backON, boolean skipON) {
      int input = 0;
      boolean inputOk = false;
      do {
         Scanner scanner = new Scanner(System.in);
         try {
            input = scanner.nextInt();
            if (input >= 0 && input < size) {
               inputOk = true;
            } else if (input < 0) {
               if (input == -1 && backON) {
                  inputOk = true;
               } else if (input == -2 && skipON) {
                  inputOk = true;
               }
            } else {
               throw new InputMismatchException();
            }
         } catch (InputMismatchException e) {
            System.err.println("Error! Incorrect input");
            scanner.reset();
         }
      }
      while (!inputOk);
      return input;
   }

   private Utility.Pair coorProjection(int rawX, int rawY, int arenaSize) {
      int actualX, actualY;
      int lastIndex = arenaSize - 1;
      actualX = lastIndex - rawY;
      actualY = rawX;
      return new Utility.Pair(actualX, actualY);
   }


}