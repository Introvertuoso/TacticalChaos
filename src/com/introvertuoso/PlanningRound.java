package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanningRound extends Round implements Serializable {

  private static final long serialVersionUID = 025;

  private int currentPlayer = 0;
  private ArrayList<Move> movesAtTheMoment = new ArrayList<>();

  @Override
  public boolean run(ArrayList<Player> players, Game.GameAttributes attributes,
      Arena arena) {
    while (currentPlayer < players.size()) {
      Player p = players.get(currentPlayer);
      TemporalStoreFilter filter = new TemporalStoreFilter(attributes.getMaxStoreListings());
      p.setBalance(p.getBalance() + 2);
      if (p instanceof ConsolePlayer) {
        p.setOrder(new MoveFactory());
        p.getOrder().setOwner(p);
        if (!p.getOrder()
            .GetMovesFromConsole(movesAtTheMoment, phase, attributes, filter, players, arena)) {
          return false;
        }
      } else if (p instanceof GUIPlayer) {
        p.getOrder().GetMovesFromGUI();
      } else if (p instanceof BotPlayer) {
        p.setOrder(new MoveFactory());
        p.getOrder().setOwner(p);
        if (!p.getOrder().GetMovesFromBot(
            movesAtTheMoment,
            ((BotPlayer) p).getDifficulty(),
            phase, attributes, filter, players, arena)) {

          return false;
        }
      }
      currentPlayer++;
    }
    return true;
  }

  @Override
  public void record(Game.Recording recording, Game.GameAttributes attributes) {
    String temp = "";
    for (Move m : movesAtTheMoment) {
      temp += (m.log() + "\n");
    }
    recording.getLogs().add(temp);
  }

  public void getTemporalList() {

  }
}
