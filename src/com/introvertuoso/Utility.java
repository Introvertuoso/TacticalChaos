package com.introvertuoso;

import java.util.ArrayList;
import java.util.List;

public class Utility {

  public static ArrayList<Square> movementDirection = new ArrayList<>(List.of(
      new Square(0, 1),
      new Square(1, 1),
      new Square(1, 0),
      new Square(1, -1),
      new Square(0, -1),
      new Square(-1, -1),
      new Square(-1, 0),
      new Square(-1, 1)
  ));



  public static String[] direction = {
      "Right", "Down/Right",
      "Down", "Down/Left",
      "Left", "Up/Left",
      "Up", "Up/Right"};

  public static class Pair {

    private Object key;
    private Object value;

    public Pair(Object key, Object value) {
      this.key = key;
      this.value = value;
    }

    public Object getKey() {
      return key;
    }

    public void setKey(Object key) {
      this.key = key;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }
  }

  public static boolean inRange(Champion source, Champion target, double maxRange) {
    return Math.sqrt(
        Math.pow(
            (target.getPosX() - source.getPosX()), 2) +
            Math.pow(target.getPosY() - source.getPosY(), 2))
        < maxRange;
  }

  public static boolean inRange(int input, int rangeStart, int rangeEnd) {
    return input >= rangeStart && input < rangeEnd;
  }

  public static boolean withinArena(int x, int y, int limit) {
    return (x >= 0 && x < limit) && (y >= 0 && y < limit);
  }
}
