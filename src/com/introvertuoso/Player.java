package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Player implements Serializable {

  private static final long serialVersionUID = 026;

  private String name;
  private int score = 0;
  private int balance = 0;

  private ArrayList<Champion> inventory = new ArrayList<>();
  private ArrayList<Champion> champsOnBoard = new ArrayList<>();
  private ArrayList<Item> droppedItems = new ArrayList<Item>();

  private MoveFactory order = new MoveFactory();
  private DamageCalculatorFactory damageCalculatorFactory = new DamageCalculatorFactory();

  public void cleanUp() {
    ArrayList<Integer> indices = new ArrayList<>();
    for (int i = 0; i < champsOnBoard.size(); i++) {
      if (champsOnBoard.get(i).getCurrentAttributes().getHealth() == 0) {
        indices.add(i);
      }
    }
    for (int j : indices) {
      champsOnBoard.get(j).restoreItems();
      champsOnBoard.remove(j);
    }
  }

  public Item getItem(int index) {
    if (!droppedItems.isEmpty()) {
      Item item = droppedItems.get(index);
      droppedItems.remove(index);
      return item;
    }
    return null;
  }

  // Getters and Setters


  public ArrayList<Item> getDroppedItems() {
    return droppedItems;
  }

  public void setDroppedItems(ArrayList<Item> droppedItems) {
    this.droppedItems = droppedItems;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public ArrayList<Champion> getInventory() {
    return inventory;
  }

  public void setInventory(ArrayList<Champion> inventory) {
    this.inventory = inventory;
  }

  public ArrayList<Champion> getChampsOnBoard() {
    return champsOnBoard;
  }

  public void setChampsOnBoard(ArrayList<Champion> champsOnBoard) {
    this.champsOnBoard = champsOnBoard;
  }

  public MoveFactory getOrder() {
    return order;
  }

  public void setOrder(MoveFactory order) {
    this.order = order;
  }

  public DamageCalculatorFactory getDamageCalculatorFactory() {
    return damageCalculatorFactory;
  }

  public void setDamageCalculatorFactory(DamageCalculatorFactory damageCalculatorFactory) {
    this.damageCalculatorFactory = damageCalculatorFactory;
  }
}
