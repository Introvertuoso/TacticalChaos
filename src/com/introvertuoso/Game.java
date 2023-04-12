package com.introvertuoso;

import java.io.*;
import java.util.*;

public class Game implements Serializable {

   private static final long serialVersionUID = 014;
   private transient Object lockSaver = new Object();
   private transient Object lockRecorder = new Object();

   private String[] acceptedClasses;
   private String[] itemAttributes = {
           "PHYSICALDAMAGE",
           "HEALTH",
           "ABILITYDAMAGE",
           "ARMOR",
           "MAGICRESIST",
           "CRITICALSTRIKECHANCE"
   };

   private GameLoader gameLoader;
   private RecordingLoader recordingLoader;
   private Saver gameSaver;
   private Recorder gameRecorder;

   private Arena arena;
   private GameAttributes attributes;

   private ArrayList<Champion> inGameStore = new ArrayList<>();
   private ArrayList<String> accepted = new ArrayList<>();

   private ArrayList<Player> players;
   private ArrayList<Player> remainingPlayers;
   private ArrayList<Item> obtainedItems;

   private RoundManager manager;
   private Recording recording = new Recording();

   public class Recording implements Serializable {

      ArrayList<String> logs;

      public ArrayList<String> getLogs() {
         return logs;
      }

      public void setLogs(ArrayList<String> logs) {
         this.logs = logs;
      }
   }

   private class Saver implements Runnable, Serializable {

      Game game;
      String path;

      @Override
      public void run() {
         synchronized (lockSaver) {
            File f1 = new File(path);
            try {
               FileOutputStream fos = new FileOutputStream(f1);
               ObjectOutputStream oos = new ObjectOutputStream(fos);
               oos.writeObject(game);
               oos.close();
               fos.close();
            } catch (FileNotFoundException e1) {
               e1.printStackTrace();
            } catch (IOException e2) {
               e2.printStackTrace();
            }
         }
      }

      public Game getGame() {
         return game;
      }

      public void setGame(Game game) {
         this.game = game;
      }

      public String getPath() {
         return path;
      }

      public void setPath(String path) {
         this.path = path;
      }
   }

   private class Recorder implements Runnable, Serializable {

      Recording recording;
      String path;

      @Override
      public void run() {
         synchronized (lockRecorder) {
            File f1 = new File(path);
            try {
               FileOutputStream fos = new FileOutputStream(f1);
               ObjectOutputStream oos = new ObjectOutputStream(fos);
               oos.writeObject(recording);
               oos.close();
               fos.close();
            } catch (FileNotFoundException e1) {
               e1.printStackTrace();
            } catch (IOException e2) {
               e2.printStackTrace();
            }
         }
      }

      public Recording getRecording() {
         return recording;
      }

      public void setRecording(Recording recording) {
         this.recording = recording;
      }

      public String getPath() {
         return path;
      }

      public void setPath(String path) {
         this.path = path;
      }
   }

   private class GameLoader implements Runnable, Serializable {

      Game game;
      String path;

      @Override
      public synchronized void run() {
         File f1 = new File(path);
         try {
            FileInputStream fis = new FileInputStream(f1);
            ObjectInputStream ois = new ObjectInputStream(fis);
            game = (Game) ois.readObject();
            game.setLockSaver(new Object());
            game.setLockRecorder(new Object());
            InGameStore.accepted = game.getAccepted();
            InGameStore.inGameStore = game.getInGameStore();
            if (InGameStore.accepted == null | InGameStore.inGameStore == null) {
               System.out.println("Game file corrupted");
            }
            ois.close();
            fis.close();
         } catch (FileNotFoundException e1) {
            e1.printStackTrace();
         } catch (IOException e2) {
            e2.printStackTrace();
         } catch (ClassNotFoundException e3) {
            e3.printStackTrace();
         }
      }

      public Game getGame() {
         return game;
      }

      public void setGame(Game game) {
         this.game = game;
      }

      public String getPath() {
         return path;
      }

      public void setPath(String path) {
         this.path = path;
      }
   }

   private class RecordingLoader implements Runnable, Serializable {

      Recording recording;
      String path;

      @Override
      public synchronized void run() {
         File f1 = new File(path);
         try {
            FileInputStream fis = new FileInputStream(f1);
            ObjectInputStream ois = new ObjectInputStream(fis);
            recording = (Recording) ois.readObject();
            ois.close();
            fis.close();
         } catch (FileNotFoundException e1) {
            e1.printStackTrace();
         } catch (IOException e2) {
            e2.printStackTrace();
         } catch (ClassNotFoundException e3) {
            e3.printStackTrace();
         }
      }

      public Recording getRecording() {
         return recording;
      }

      public void setRecording(Recording recording) {
         this.recording = recording;
      }

      public String getPath() {
         return path;
      }

      public void setPath(String path) {
         this.path = path;
      }
   }

   private class RoundManager implements Serializable {

      private int warmupEnd = 2;                  // 9
      private int cr = 0;

      private ArrayList<Round> rounds;
      private Round currentRound;

      public RoundManager() {
         rounds = new ArrayList<>();
         if (attributes.getMaxRounds() % 2 != 1) {
            attributes.setMaxRounds(attributes.getMaxRounds() + 1);
         }
         for (int i = 0; i < attributes.getMaxRounds(); i++) {
            if (i < warmupEnd) {
               rounds.add(new PlanningRound());
            } else {
               if (i % 2 == 1) {
                  rounds.add(new PlanningRound());
               } else {
                  rounds.add(new ExecutionRound());
               }
            }
         }
      }

      public void runRounds() {
         recording.setLogs(new ArrayList<>());
         while (!gameOver(cr)) {
            currentRound = rounds.get(cr);
            for (Champion c : arena.getChampsOnBoard()) {
               currentRound.getChampsOnBoardAtTheMoment().add(c.clone());
            }
            if (cr < warmupEnd) {
               currentRound.setPhase(GamePhase.WARMUP);
            } else {
               currentRound.setPhase(GamePhase.BATTLE);
            }
            if (currentRound.getPhase() == GamePhase.WARMUP) {
               currentRound.setNumber(cr + 1);
               System.out.println("Round: " + (cr + 1));
            } else if (cr % 2 == 1) {
               currentRound.setNumber(((cr - warmupEnd) / 2 + warmupEnd) + 1);
               System.out.println("Round: " + (((cr - warmupEnd) / 2 + warmupEnd) + 1));
            }
            if (currentRound instanceof PlanningRound &&
                    attributes.getAutoSave() == 1) {
               saveGame();
            }
            if (!currentRound.run(remainingPlayers, attributes, arena)) {
               System.out.println("Some progress may be lost.\n" +
                       "Do you wish to save before exiting? (y/n)");
               Scanner s = new Scanner(System.in);
               String input = s.nextLine();
               if (input.equals("y")) {
                  saveGame();
                  return;
               } else if (input.equals("n")) {
                  return;
               }
            }
            currentRound.record(recording, attributes);
            saveRecording();
            cr++;
         }
         System.out.println("Game Over");
         int index = -1, highest = 0;
         int indexTie = -1;
         for (int i = 0; i < remainingPlayers.size(); i++) {
            Player current = remainingPlayers.get(i);
            if (current.getChampsOnBoard().size() > highest) {
               highest = current.getChampsOnBoard().size();
               index = i;
            } else if (current.getChampsOnBoard().size() == highest
                    && i != index) {
               indexTie = i;
            }
         }
         if (index >= 0 && indexTie == -1) {
            System.out.println(remainingPlayers.get(index).getName() + " Wins!" +
                    " With " + highest + " Champion(s) left.");
         } else if (index >= 0) {
            System.out.println("It's a tie! " + remainingPlayers.get(index).getName() + " and " +
                    remainingPlayers.get(indexTie).getName() + " Win! " +
                    "With " + highest + " Champion(s) left.");
         }
      }

      public int getCr() {
         return cr;
      }

      public void setCr(int cr) {
         this.cr = cr;
      }

      public ArrayList<Round> getRounds() {
         return rounds;
      }

      public void setRounds(ArrayList<Round> rounds) {
         this.rounds = rounds;
      }

      public int getWarmupEnd() {
         return warmupEnd;
      }

      public void setWarmupEnd(int warmupEnd) {
         this.warmupEnd = warmupEnd;
      }

      public Round getCurrentRound() {
         return currentRound;
      }

      public void setCurrentRound(Round currentRound) {
         this.currentRound = currentRound;
      }
   }

   public synchronized void saveGame() {
      gameSaver = new Saver();
      gameSaver.setPath(attributes.getSavePath());
      gameSaver.setGame(this);
      gameSaver.run();
   }

   public synchronized void saveRecording() {
      gameRecorder = new Recorder();
      gameRecorder.setPath(attributes.recordingPath);
      gameRecorder.setRecording(this.recording);
      gameRecorder.run();
   }

   public Game loadGame(String path) {
      gameLoader = new GameLoader();
      gameLoader.setPath(path);
      gameLoader.run();
      return gameLoader.getGame();
   }

   public Recording loadRecording(String path) {
      recordingLoader = new RecordingLoader();
      recordingLoader.setPath(path);
      recordingLoader.run();
      return recordingLoader.getRecording();
   }

   public void replayRecording(int start) {
      while (start < recording.getLogs().size()) {
//         System.out.println("Round: " + currentRound.getNumber());
         System.out.println(recording.logs.get(start));
         System.out.println();
         start++;
      }
   }

   public void runGame() {
      manager.runRounds();
   }

   public void initGame() {
      arena = new Arena(attributes.arenaSize, obtainedItems);
      initPlayers();
      remainingPlayers = players;
      manager = new RoundManager();
   }

   private void configureStore(String poolPath, int rowS, int rowE, int colS, int colE) {
      if (acceptedClasses.length > 0) {
         ChampionClassFilter filter = new ChampionClassFilter(acceptedClasses);
         InGameStore.initInGameStore(poolPath, rowS, rowE, colS, colE, filter);
      }
   }

   private void initPlayers() {
      players = new ArrayList<>();
      System.out.println("Maximum number of players: " + attributes.maxPlayers +
              " (If you enter less than that the remaining players will be bots)\n");
      System.out.println("Enter the number of Human players:   ");
      int numberOfHumans = 0, numberOfBots;
      boolean inputHumansOk = false;
      do {
         Scanner sc = new Scanner(System.in);
         try {
            numberOfHumans = sc.nextInt();
            inputHumansOk = true;
            if (numberOfHumans < 0 || numberOfHumans > attributes.maxPlayers) {
               inputHumansOk = false;
               throw new InputMismatchException();
            }
         } catch (InputMismatchException e) {
            System.err.println("Error! Incorrect input");
            sc.reset();
         }
      } while (!inputHumansOk);

      if (numberOfHumans == attributes.maxTeamSize) {
         return;
      } else {
         if (numberOfHumans == 0) {
            numberOfBots = attributes.maxPlayers;
         } else {
            numberOfBots = attributes.maxPlayers - numberOfHumans;
         }

      }
      for (int i = 0; i < numberOfHumans; i++) {
         Player player = new ConsolePlayer();
         player.setName(getPlayerNameFromUser());
         players.add(player);
      }
      for (int i = 0; i < numberOfBots; i++) {
         Player player = new BotPlayer();
         player.setName("Bot " + (i + 1));
         players.add(player);
      }
   }

   public void configureGame(String poolPath, String attrsConfigPath,
                             String acptdConfigPath, String itemListPath,
                             int rowS, int rowE, int colS, int colE) {

      configureAttributes(attrsConfigPath);
      configureAcceptedClasses(acptdConfigPath);
      configureItems(itemListPath);
      configureStore(poolPath, rowS, rowE, colS, colE);
      inGameStore = InGameStore.inGameStore;
      accepted = InGameStore.accepted;
   }

   private boolean gameOver(int cr) {
      if (cr + 1 == this.attributes.getMaxRounds()) {
         return true;
      }
      return remainingPlayers.size() == 1;
   }

   private void configureItems(String itemsPath) {

      obtainedItems = new ArrayList<>();
      File file = new File(itemsPath);
      String[] item;
      try {
         BufferedReader br = new BufferedReader(new FileReader(file));
         String st;

         while ((st = br.readLine()) != null) {
            item = st.split("[,]");

            List<String> attrs = Arrays.asList(itemAttributes);
            List<String> classes = Arrays.asList(acceptedClasses);

            if (attrs.contains(item[2].trim().toUpperCase())) {
               if (classes.contains(item[3].trim().toUpperCase())) {
                  try {
                     obtainedItems.add(new Item(item[0].trim().toUpperCase(),  // name
                             Double.parseDouble(item[1]),                      // amount
                             item[2].trim().toUpperCase(),                     // attribute
                             item[3].trim().toUpperCase()));                   // class

                  } catch (NumberFormatException e) {

                     System.err.println("Error in line: \n" + st
                             + "\n Assuming amount value as 0.1");

                     obtainedItems.add(new Item(item[0].trim().toUpperCase(),    // name
                             Double.parseDouble("0.1"),                       // amount
                             item[2].trim().toUpperCase(),                       // attribute
                             item[3].trim().toUpperCase()));
                  }
               }
            }
         }
         br.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      int bound = attributes.getArenaSize() / 20 * 3;
      if (bound == 0) {
         bound = 1;
      }
      duplicateItems(bound);
   }

   private void duplicateItems(int bound) {
      // Duplicate
      ArrayList<Item> temp = new ArrayList<>();
      for (Item it : obtainedItems) {
         Random r = new Random();
         int rand = r.nextInt(bound) + 1;
         for (int i = 0; i < rand; i++) {
            temp.add(it.clone());
         }
      }
      Collections.shuffle(temp);
      obtainedItems = temp;
   }

   private String getPlayerNameFromUser() {
      String playerName;
      System.out.println("Please, enter your name: ");
      Scanner sc = new Scanner(System.in);
      playerName = sc.next();
      return playerName;

   }

   private void configureAttributes(String configFilePath) {
      attributes = new GameAttributes();
      File file = new File(configFilePath);
      try {
         BufferedReader br = new BufferedReader(new FileReader(file));
         String st;
         ArrayList<String[]> gameSettings = new ArrayList<>();
         while ((st = br.readLine()) != null) {
            gameSettings.add(st.split(":"));
         }
         for (String[] arrayOfStrings : gameSettings) {
            switch (arrayOfStrings[0].toUpperCase()) {
               case "MAXPLAYERS":
                  attributes.setMaxPlayers(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "MAXBENCHSIZE":
                  attributes.setMaxBenchSize(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "MAXROUNDS":
                  attributes.setMaxRounds(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "MAXTEAMSIZE":
                  attributes.setMaxTeamSize(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "MAXSWAPS":
                  attributes.setMaxSwaps(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "MAXSTORELISTINGS":
                  attributes.setMaxStoreListings(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "ARENASIZE":
                  attributes.setArenaSize(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "AUTOSAVE":
                  attributes.setAutoSave(Integer.parseInt(arrayOfStrings[1]));
                  break;
               case "SAVEPATH":
                  attributes.setSavePath(arrayOfStrings[1]);
                  break;
               case "RECORDINGPATH":
                  attributes.setRecordingPath(arrayOfStrings[1]);
                  break;
            }
         }

         br.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void configureAcceptedClasses(String acceptedClassesConfigFile) {
      File file = new File(acceptedClassesConfigFile);
      try {
         BufferedReader br = new BufferedReader(new FileReader(file));
         String st;
         String[] acc = new String[20];
         while ((st = br.readLine()) != null) {
            acc = st.split(",");
            for (int i = 0; i < acc.length; i++) {
               acc[i] = acc[i].trim();
               acc[i] = acc[i].toUpperCase();
            }
         }
         br.close();
         if (acc.length > 0) {
            acceptedClasses = acc;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static class GameAttributes implements Serializable {

      private static final long serialVersionUID = 015;

      private String
              recordingPath = "files\\game.recording",        // files\game.recording
              savePath = "files\\game.save";                  // files\game.save

      private int
              autoSave = 0,                                     // 0
              maxPlayers = 8,                                       // 8
              maxBenchSize = 8,                                   // 8
              maxRounds = 89,                                   // ???
              maxTeamSize = 9,                                  // 9
              maxSwaps = 2,                                     // 2
              maxStoreListings = 5,                             // 5
              maxPlanTime,                                        // ???
              arenaSize = 100;                                  // 100

      public String getRecordingPath() {
         return recordingPath;
      }

      public void setRecordingPath(String recordingPath) {
         this.recordingPath = recordingPath;
      }

      public String getSavePath() {
         return savePath;
      }

      public void setSavePath(String savePath) {
         this.savePath = savePath;
      }

      public int getAutoSave() {
         return autoSave;
      }

      public void setAutoSave(int autoSave) {
         this.autoSave = autoSave;
      }

      public int getMaxPlayers() {
         return maxPlayers;
      }

      public void setMaxPlayers(int maxPlayers) {
         this.maxPlayers = maxPlayers;
      }

      public int getMaxBenchSize() {
         return maxBenchSize;
      }

      public void setMaxBenchSize(int maxBenchSize) {
         this.maxBenchSize = maxBenchSize;
      }

      public int getMaxRounds() {
         return maxRounds;
      }

      public void setMaxRounds(int maxRounds) {
         this.maxRounds = maxRounds;
      }

      public int getMaxTeamSize() {
         return maxTeamSize;
      }

      public void setMaxTeamSize(int maxTeamSize) {
         this.maxTeamSize = maxTeamSize;
      }

      public int getMaxSwaps() {
         return maxSwaps;
      }

      public void setMaxSwaps(int maxSwaps) {
         this.maxSwaps = maxSwaps;
      }

      public int getMaxStoreListings() {
         return maxStoreListings;
      }

      public void setMaxStoreListings(int maxStoreListings) {
         this.maxStoreListings = maxStoreListings;
      }

      public int getMaxPlanTime() {
         return maxPlanTime;
      }

      public void setMaxPlanTime(int maxPlanTime) {
         this.maxPlanTime = maxPlanTime;
      }

      public int getArenaSize() {
         return arenaSize;
      }

      public void setArenaSize(int arenaSize) {
         this.arenaSize = arenaSize;
      }
   }

   // Getters and Setters

   public String[] getAcceptedClasses() {
      return acceptedClasses;
   }

   public void setAcceptedClasses(String[] acceptedClasses) {
      this.acceptedClasses = acceptedClasses;
   }

   public ArrayList<Player> getPlayers() {
      return players;
   }

   public void setPlayers(ArrayList<Player> players) {
      this.players = players;
   }

   public GameAttributes getAttributes() {
      return attributes;
   }

   public void setAttributes(GameAttributes attributes) {
      this.attributes = attributes;
   }

   public Arena getArena() {
      return arena;
   }

   public void setArena(Arena arena) {
      this.arena = arena;
   }

   public ArrayList<Player> getRemainingPlayers() {
      return remainingPlayers;
   }

   public void setRemainingPlayers(ArrayList<Player> remainingPlayers) {
      this.remainingPlayers = remainingPlayers;
   }

   public RoundManager getManager() {
      return manager;
   }

   public void setManager(RoundManager manager) {
      this.manager = manager;
   }

   public ArrayList<Item> getObtainedItems() {
      return obtainedItems;
   }

   public void setObtainedItems(ArrayList<Item> obtainedItems) {
      this.obtainedItems = obtainedItems;
   }

   public ArrayList<Champion> getInGameStore() {
      return inGameStore;
   }

   public void setInGameStore(ArrayList<Champion> inGameStore) {
      this.inGameStore = inGameStore;
   }

   public ArrayList<String> getAccepted() {
      return accepted;
   }

   public void setAccepted(ArrayList<String> accepted) {
      this.accepted = accepted;
   }

   public Recording getRecording() {
      return recording;
   }

   public void setRecording(Recording recording) {
      this.recording = recording;
   }

   public Object getLockSaver() {
      return lockSaver;
   }

   public void setLockSaver(Object lockSaver) {
      this.lockSaver = lockSaver;
   }

   public Object getLockRecorder() {
      return lockRecorder;
   }

   public void setLockRecorder(Object lockRecorder) {
      this.lockRecorder = lockRecorder;
   }
}