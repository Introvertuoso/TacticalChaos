package com.introvertuoso;

import java.io.Serializable;

public class BotPlayer extends Player implements Serializable {

  private static final long serialVersionUID = 005;

  // 1 = Easy, 2 = Intermediate
  private int difficulty = 1;

  // Getters and Setters

  public int getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

}