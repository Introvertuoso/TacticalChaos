package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public class Square implements Serializable {

  private static final long serialVersionUID = 031;

  private int x, y;
  private ArrayList<Champion> contents;

  private SquareType squareType;
  private Item item = null;

  public Square(int x, int y) {
    this.x = x;
    this.y = y;
        this.contents = new ArrayList<>();
        this.squareType = SquareType.STANDARD;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<Champion> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Champion> contents) {
        this.contents = contents;
    }

    public SquareType getSquareType() {
        return squareType;
    }

    public void setSquareType(SquareType squareType) {
        this.squareType = squareType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
