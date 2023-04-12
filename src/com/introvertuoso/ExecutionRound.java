package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ExecutionRound extends Round implements Serializable {

  private static final long serialVersionUID = 013;

  @Override
  public boolean run(ArrayList<Player> players, Game.GameAttributes attributes, Arena arena) {

    ArrayList<Player> temp = new ArrayList<>();
    temp.addAll(players);
    Collections.shuffle(temp);
    for (Player player : temp) {
      Thread thread = new Thread() {
        @Override
        public synchronized void run() {
          player.getDamageCalculatorFactory().setCurrent(player.getChampsOnBoard());
          player.getDamageCalculatorFactory().CalculateClasses();
          player.getDamageCalculatorFactory().GenerateDamageCalculators();
          if (!player.getOrder().getMoves().isEmpty()) {
            ArrayList<Integer> removed = new ArrayList<>();
            for (Move m : player.getOrder().getMoves()) {
              if (!m.getSourceChampion().getStates().contains(ChampionState.STUNNED)) {
                if (m.getAbilityState() == AbilityState.INACTIVE) {
                  m.setAbilityState(AbilityState.ACTIVE);
                  m.setDuration(m.getDuration() - 1);
                  m.PerformMove();
                } else {
                  if (m.getDuration() == 0) {
                    m.removeEffects();
                    removed.add(player.getOrder().getMoves().indexOf(m));
                  } else {
                    m.setDuration(m.getDuration() - 1);
                  }
                }
              }
              if (!removed.isEmpty()) {
                for (int j : removed) {
                  Move move = player.getOrder().getMoves().get(j);
                  move.getSourceChampion().getMoves().remove(move);
                  player.getOrder().getMoves().remove(j);
                }
              }
            }
          }
        }
      };
      thread.start();
    }
    return true;
  }

  @Override
  public void record(Game.Recording recording, Game.GameAttributes attributes) {
    String temp = "";
    for (Champion c : champsOnBoardAtTheMoment) {
      if (c != null) {
        temp += c.getCurrentAttributes().getName() +
//                " - " + c.getOwner().getName() +
            " - health: " +
            c.getCurrentAttributes().getHealth() + " - damage: " +
            c.getCurrentAttributes().getAttackDamage() + " - armor: " +
            c.getCurrentAttributes().getArmor() + "\n";
      }
    }
    recording.getLogs().add(temp);
  }
}
