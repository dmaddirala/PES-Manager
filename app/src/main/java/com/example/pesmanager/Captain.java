package com.example.pesmanager;

import java.util.ArrayList;

public class Captain {

    private String name;
    private float balanceMoney;
    private ArrayList<Player> players;

    public Captain(String name, float balanceMoney){
        this.name = name;
        this.balanceMoney = balanceMoney;
    }

    public String getName() {
        return name;
    }

    public float getBalanceMoney() {
        return balanceMoney;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setName(String name) { this.name = name; }

    public void setBalanceMoney(float balanceMoney) { this.balanceMoney = balanceMoney; }

    public void setPlayers(ArrayList<Player> players) { this.players = players; }
}
