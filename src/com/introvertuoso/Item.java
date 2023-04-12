package com.introvertuoso;

import java.io.Serializable;

public class Item implements Serializable {

  private static final long serialVersionUID = 021;

  private String name;
  private double amount;
  private String corrAttribute;
  private String newClass;

  public Item(String name, double amount, String corrAttribute, String newClass) {
    this.name = name;
    this.amount = amount;
    this.corrAttribute = corrAttribute;
        this.newClass = newClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCorrAttribute() {
        return corrAttribute;
    }

    public void setCorrAttribute(String corrAttribute) {
        this.corrAttribute = corrAttribute;
    }

    public String getNewClass() {
        return newClass;
    }

    public void setNewClass(String newClass) {
        this.newClass = newClass;
    }

    @Override
    protected Item clone() {
        return new Item(name, amount, corrAttribute, newClass);
    }
}
