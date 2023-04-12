package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class TemporalStoreFilter extends StoreFilter implements Serializable {

  private static final long serialVersionUID = 033;

  private int size;
  private ArrayList<Champion> chosen = new ArrayList<>();

  TemporalStoreFilter(int size) {
    this.size = size;
    getChampionList();
  }

  @Override
    public void getChampionList() {
        chosen = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Random r = new Random();
            chosen.add(InGameStore.inGameStore.get(r.nextInt(InGameStore.inGameStore.size())));
        }
    }

    public Champion getOnly(int index) {
        Champion temp = chosen.get(index);
        InGameStore.inGameStore.remove(temp);
        chosen.remove(temp);
        return temp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<Champion> getChosen() {
        return chosen;
    }

    public void setChosen(ArrayList<Champion> chosen) {
        this.chosen = chosen;
    }
}
