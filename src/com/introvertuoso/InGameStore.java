package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class InGameStore implements Serializable {

  private static final long serialVersionUID = 017;

  private static ArrayList<Champion> vanillaPool;
  private static ArrayList<Champion> filteredPool;
  public static ArrayList<Champion> inGameStore;
  public static ArrayList<String> accepted;

  public static void initInGameStore(String path, int rowStart, int rowEnd, int colStart,
      int colEnd, ChampionClassFilter filter) {

    inGameStore = new ArrayList<>();
    vanillaPool = new ArrayList<>();
    accepted = new ArrayList<>();
    filteredPool = new ArrayList<>();
    String[] acc = filter.getAccepted();
    Collections.addAll(accepted, acc);
    obtainVanillaPool(path, rowStart, rowEnd, colStart, colEnd);
    filterVanillaPool(filter);
    duplicateFiltered();
    if (inGameStore != null && !inGameStore.isEmpty()) {
      Collections.shuffle(inGameStore);
    }
  }

  private static void obtainVanillaPool(String path, int rowStart, int rowEnd, int colStart,
      int colEnd) {
    File f1 = new File(path);
    int nRows = rowEnd - rowStart, nCols = colEnd - colStart;
    String[][] vanillaPoolArray = new String[nRows][nCols];
    for (int k = 0; k < nRows; k++) {
      for (int l = 0; l < nCols; l++) {
        vanillaPoolArray[k][l] = "";
      }
    }
    try {
      FileInputStream fis = new FileInputStream(f1);
      XSSFWorkbook wb = new XSSFWorkbook(fis);
      XSSFSheet sheet = wb.getSheetAt(0);
      Iterator<Row> itr = sheet.iterator();
      int x = 0, y;
      while (itr.hasNext()) {
        Row r = itr.next();
        if (r.getRowNum() >= rowStart &&
            r.getRowNum() + 1 <= rowEnd) {

          Iterator<Cell> cellIterator = r.cellIterator();
          y = 0;
          while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getColumnIndex() >= colStart &&
                cell.getColumnIndex() + 1 <= colEnd) {

              String s = cell.toString();
              if (!s.equals("")) {
                vanillaPoolArray[x][y] = s;
                y++;
              }
            }
          }
          x++;
        }
      }
      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < nRows; i++) {
      Champion c = new Champion(vanillaPoolArray[i]);
      vanillaPool.add(c);
    }
  }

  private static void filterVanillaPool(ChampionClassFilter filter) {
    filter.setSource(vanillaPool);
    filter.getChampionList();
    filteredPool = filter.getResult();
  }

  private static void duplicateFiltered() {
    for (Champion c : filteredPool) {
      Random r = new Random();
      int rand = r.nextInt(5) + 5;
      for (int i = 0; i < rand; i++) {
        inGameStore.add(c.clone());
      }
    }
  }


  public static ArrayList<Champion> getVanillaPool() {
    return vanillaPool;
  }

  public static void setVanillaPool(ArrayList<Champion> vanillaPool) {
    InGameStore.vanillaPool = vanillaPool;
  }

  public static ArrayList<Champion> getFilteredPool() {
    return filteredPool;
  }

  public static void setFilteredPool(ArrayList<Champion> filteredPool) {
    InGameStore.filteredPool = filteredPool;
  }
}