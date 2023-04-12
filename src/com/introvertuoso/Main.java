package com.introvertuoso;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    System.out.println(ConsoleColors.PURPLE_BOLD
        + "### Welcome to Tactical Chaos ###"
        + ConsoleColors.RESET);

    int input = new Main().getInputMainMenu();

    if (input == 1) {
      Game game = new Game();
      // Appendix is an Excel sheet containing all the necessary values to create the champion pool.
      game.configureGame(
          "files\\Appendix1.xlsx",
          "files\\Game Attributes.txt",
          "files\\Accepted Classes.txt",
          "files\\Items List.txt",
          1, 47, 0, 17);
      game.initGame();
      game.runGame();
    } else if (input == 2) {
      Game game = new Game();
      game = game.loadGame("files\\game.save");
      if (game != null) {
        game.runGame();
      } else {
        System.err.println("Game not found!");
      }

    } else if (input == 3) {
      Game game = new Game();
      game.setRecording(game.loadRecording("files\\game.recording"));
      if (game.getRecording() != null) {
        game.replayRecording(0);
      } else {
        System.err.println("Recording not found!");
      }
    }

  }

  private int getInputMainMenu() {
    String[] mainMenu = {"(1) - Start A New Game.", "(2) - Load Game.", "(3) - Replay Game"};
    for (String s : mainMenu) {
      System.out.println(s);
    }
    System.out.println("Please select an option: ");
    int input = 0;
    boolean inputOK = false;
    do {
      Scanner scanner = new Scanner(System.in);
      try {
        input = scanner.nextInt();
        if (input > 0 && input <= mainMenu.length) {
          inputOK = true;
        } else {
          throw new InputMismatchException();
        }
      } catch (InputMismatchException e) {
        System.err.println("Error! Incorrect input");
        scanner.reset();
      }

    } while (!inputOK);
    return input;
  }
}


