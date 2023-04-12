package com.introvertuoso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Arena implements Serializable {

  private static final long serialVersionUID = 001;

  private int size;
  private Square[][] board;

  private ArrayList<Champion> champsOnBoard;
  private ArrayList<Item> itemsPool;

  public Arena(int size, ArrayList<Item> items) {
    this.size = size;
    board = new Square[size][size];
    itemsPool = items;
    champsOnBoard = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        board[i][j] = new Square(i, j);
      }
    }
    initArena(board);
    distributeItems();
    printBoard(board);
  }

  public void distributeItems() {
    for (Item i : itemsPool) {
      int x, y;
      do {
        Random rx = new Random();
        Random ry = new Random();

        x = rx.nextInt(size);
        y = ry.nextInt(size);
      } while (board[x][y].getSquareType() != SquareType.TERRAIN &&
          board[x][y].getItem() == null);

      board[x][y].setItem(i);
    }
  }

  private void initArena(Square[][] board) {
    int length = board.length;
    int tenPer = (int) (0.1 * length);
    Random rand = new Random();
    int swap = rand.nextInt(2);
    grassGenerator(board, length, tenPer, tenPer + 1, 1, tenPer);
    waterGenerator(board, length, (int) (tenPer * 0.5), tenPer * 2, tenPer, swap);
    landscapeGenerator(board, length, (int) (tenPer * 3.5), (int) (tenPer * 3.5), 0.5,
        tenPer, tenPer * 3, -(int) (tenPer * 0.5), tenPer * 2, SquareType.TERRAIN, swap);
  }

  private void waterGenerator(
      Square[][] board, int max, int pad, int radius, int noise, int swap) {

    int length = board.length;
    int tenPer = (int) (0.1 * length);
    Random rand = new Random();
    int r = rand.nextInt(2);
    Random randx = new Random();
    int x = randx.nextInt(max / 2);
    x = x + pad;
    if (r == 1) {
      x += max / 2;
    }
    Random randy = new Random();
    int y = randy.nextInt(max / 2);
    y = y + pad;
    if (r == 1) {
      y += max / 2;
    }

    if (x > max - 1) {
      x = max - 1;
    } else if (x < 0) {
      x = 0;
    }

    if (y > max - 1) {
      y = max - 1;
    } else if (y < 0) {
      y = 0;
    }
    landscapeGenerator(board, length, (int) (tenPer * 3.5), (int) (tenPer * 3.5), 1.0,
        tenPer, tenPer * 2, -(int) (tenPer * 0.5), tenPer * 2, SquareType.WATER, swap);
  }

  public void printArena(GamePhase phase, Arena arena, Player owner) {
    if (phase == GamePhase.WARMUP) {
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          if (!board[i][j].getContents().isEmpty()) {
            for (Champion c : board[i][j].getContents()) {
              if (c.getOwner() == owner) {
                System.out.print(c.getCurrentAttributes().getName().charAt(0) + "  ");
              }
            }
          } else {
            System.out.print("__ ");
          }
        }
        System.out.println();
      }
    } else if (phase == GamePhase.BATTLE) {
      for (int k = 0; k < size; k++) {
        for (int l = 0; l < size; l++) {
          if (board[k][l].getItem() != null) {
            System.out.print(board[k][l].getItem().getName());
          }
          String str;
          if (!board[k][l].getContents().isEmpty()) {
            Champion c = board[k][l].getContents().get(0);
            str = c.getCurrentAttributes().getName().charAt(0) + ""
                + c.getCurrentAttributes().getName().charAt(1) + " "
                + c.getOwner().getName().charAt(0) + ""
                + c.getOwner().getName().charAt(c.getOwner().getName().length() - 1)
                + " ";
          } else {
            str = "_____ ";
          }
          if (board[k][l].getSquareType() == SquareType.WATER) {
            System.out.print(ConsoleColors.CYAN + str + ConsoleColors.RESET);
          } else if (board[k][l].getSquareType() == SquareType.TERRAIN) {
            System.out.print(ConsoleColors.YELLOW + str + ConsoleColors.RESET);
          } else if (board[k][l].getSquareType() == SquareType.GRASS) {
            System.out.print(ConsoleColors.GREEN + str + ConsoleColors.RESET);
          } else {
            System.out.print(str);
          }
        }
        System.out.println();
      }
    }

    arena.refreshChampsOnBoard();
    System.out.println();
    for (Champion c : arena.getChampsOnBoard()) {

      Utility.Pair temp = coorToRaw(c.getPosX(), c.getPosY());

      System.out.println(ConsoleColors.CYAN_BOLD
          + c.getCurrentAttributes().getName()
          + " - "
          + c.getOwner().getName()
          + " - "
          + "Health:"
          + " "
          + c.getCurrentAttributes().getHealth()
          + " - "
          + "Damage:"
          + " "
          + c.getCurrentAttributes().getAttackDamage()
          + " - "
          + "Armor:"
          + " "
          + c.getCurrentAttributes().getArmor() * 100 + "%"
          + " - "
          + "Position: "
          + "("
          + temp.getKey()
          + ","
          + temp.getValue()
          + ")"
          + ConsoleColors.RESET);
      if (!c.getItems().isEmpty()) {
        System.out.println(c.getCurrentAttributes().getName());
        for (Item i : c.getItems()) {
          System.out.println(ConsoleColors.CYAN_BOLD
              + "Item Name: "
              + i.getName()
              + " , "
              + "Class:"
              + " "
              + i.getNewClass()
              + ConsoleColors.RESET);
        }
      }
    }
    System.out.println();
  }

  private void printBoard(Square[][] board) {
    for (Square[] i : board) {
      for (Square s : i) {
        if (s.getSquareType() == SquareType.TERRAIN) {
          System.out.print(ConsoleColors.YELLOW_BACKGROUND +
              "  " + ConsoleColors.RESET);
        } else if (s.getSquareType() == SquareType.WATER) {
          System.out.print(ConsoleColors.CYAN_BACKGROUND +
              "  " + ConsoleColors.RESET);
        } else if (s.getSquareType() == SquareType.GRASS) {
          System.out.print(ConsoleColors.GREEN_BACKGROUND +
              "  " + ConsoleColors.RESET);
        } else {
          System.out.print(ConsoleColors.WHITE_BACKGROUND +
              "  " + ConsoleColors.RESET);
        }
      }
      System.out.println();
    }
  }

  private void landscapeGenerator(Square[][] board, int max, int centers,
      double radius,
      double rotationalPercentage,
      int rotations,
      int noise,
      int safety,
      int jitter, SquareType type, int swap) {

    radius -= 1;
    double con = radius;
    Random random = new Random();
    int jit = random.nextInt(jitter);
    if (jit % 2 == 0) {
      jit = -jit;
    }
    double step = rotationalPercentage * ((3.14159265 * 2) / centers);
    double x, y, current = 0;
    for (int i = 0; i < rotations; i++) {
      for (int j = 0; j < centers; j++) {
        Random rand = new Random();
        int n = rand.nextInt(noise);
        x = (Math.cos(current) * radius) + con + n + safety + jit;
        y = (Math.sin(current) * radius) + con + n + +safety + jit;
        if (x > max - 1) {
          x = max - 1;
        } else if (x < 0) {
          x = 0;
        }
        if (y > max - 1) {
          y = max - 1;
        } else if (y < 0) {
          y = 0;
        }
        if (swap == 1) {
          int temp = (int) x;
          x = y;
          y = temp;
        }
        board[(int) x][(int) y].setSquareType(type);
        current += step;
        radius -= (radius / centers);
      }
    }
  }

  private void grassGenerator(Square[][] board, int max, int vskip,
      int hskip, int pad, int noise) {

    if (noise >= ((Math.min(vskip, hskip) - 1) / 2) + 1) {
      noise = ((Math.min(vskip, hskip) - 1) / 2) + 1;
    }
    if (vskip < 2) {
      vskip = 2;
    }
    if (hskip < 2) {
      hskip = 2;
    }

    int counter = 0, trueJ, max_pad = max - pad;
    for (int i = 0; i < max; i++) {
      if (i > pad && i < max_pad && i % vskip == 0) {
        for (int j = 0; j < max; j++) {
          if (j > pad && j < max_pad && j % hskip == 0) {
            trueJ = j;
            if (counter % 2 == 1 && i + 1 != max) {
              trueJ = j + 1;
            }
            Random rand = new Random();
            int n = rand.nextInt(noise);
            if (n % 2 == 0) {
              n = -n - 1;
            }
            int x = i + n, y = trueJ + n;
            if (x > max - 1) {
              x = max - 1;
            } else if (x < 0) {
              x = 0;
            }
            if (y > max - 1) {
              y = max - 1;
            } else if (y < 0) {
              y = 0;
            }
            board[x][y].setSquareType(SquareType.GRASS);
          }
        }
        counter++;
      }
    }
  }

  private Utility.Pair coorToRaw(int actualX, int actualY) {
    int rawX, rawY;
    int lastIndex = size - 1;
    rawX = actualY;
    rawY = lastIndex - actualX;
    return new Utility.Pair(rawX, rawY);
  }

  public void refreshChampsOnBoard() {
    champsOnBoard = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (!board[i][j].getContents().isEmpty()) {
          champsOnBoard.addAll(board[i][j].getContents());
        }
      }
    }
  }

  public Square[][] getBoard() {
    return board;
  }

  public void setBoard(Square[][] board) {
    this.board = board;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public ArrayList<Champion> getChampsOnBoard() {
    return champsOnBoard;
  }

  public void setChampsOnBoard(ArrayList<Champion> champsOnBoard) {
    this.champsOnBoard = champsOnBoard;
  }

  public ArrayList<Item> getItemsPool() {
    return itemsPool;
  }

  public void setItemsPool(ArrayList<Item> itemsPool) {
    this.itemsPool = itemsPool;
  }
}
