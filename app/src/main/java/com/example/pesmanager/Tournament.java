package com.example.pesmanager;

import java.util.ArrayList;

public class Tournament {

    private String name;
    private String date;
    private String time;
    private float startingMoney;
    private ArrayList<Captain> captains;

    public Tournament(String name){
        this.name = name;
    }


    public float getStartingMoney() { return startingMoney; }

    public String getName(){
        return name;
    }

    public String getDate() { return date; }

    public String getTime() {
        return time;
    }

    public ArrayList<Captain> getCaptains() { return captains; }

    public void setCaptains(ArrayList<Captain> captains) { this.captains = captains; }

    public void setName(String name) { this.name = name; }

    public void setStartingMoney(float startingMoney) { this.startingMoney = startingMoney; }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
