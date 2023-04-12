package com.introvertuoso;

import java.util.ArrayList;

public abstract class Round {

    protected GamePhase phase;
    protected int number = -1;

    protected ArrayList<Champion> champsOnBoardAtTheMoment = new ArrayList<>();

    public abstract boolean run(ArrayList<Player> players, Game.GameAttributes attributes, Arena arena);

    public abstract void record(Game.Recording recording, Game.GameAttributes attributes);

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<Champion> getChampsOnBoardAtTheMoment() {
        return champsOnBoardAtTheMoment;
    }

    public void setChampsOnBoardAtTheMoment(ArrayList<Champion> champsOnBoardAtTheMoment) {
        this.champsOnBoardAtTheMoment = champsOnBoardAtTheMoment;
    }
}
