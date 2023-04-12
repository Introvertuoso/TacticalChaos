package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChampionClassFilter extends StoreFilter implements Serializable {

  private static final long serialVersionUID = 010;

  private String[] accepted;
  private ArrayList<Champion> source;
  private ArrayList<Champion> result;

  public ChampionClassFilter(String[] accepted) {
    this.accepted = accepted;
  }

  @Override
  public void getChampionList() {
    result = new ArrayList<>();
    List<String> acceptedList = Arrays.asList(accepted);
    for (Champion c : source) {
      if (acceptedList.contains(c.getBaseAttributes().getClass1()) ||
          acceptedList.contains(c.getBaseAttributes().getClass2()) ||
          acceptedList.contains(c.getBaseAttributes().getClass3())) {

        result.add(c);
      }
    }
  }

  public String[] getAccepted() {
    return accepted;
  }

  public void setAccepted(String[] accepted) {
    this.accepted = accepted;
  }

  public ArrayList<Champion> getSource() {
    return source;
  }

  public void setSource(ArrayList<Champion> source) {
    this.source = source;
  }

  public ArrayList<Champion> getResult() {
    return result;
  }

  public void setResult(ArrayList<Champion> result) {
    this.result = result;
  }
}
