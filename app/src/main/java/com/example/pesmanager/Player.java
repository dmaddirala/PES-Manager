package com.example.pesmanager;

public class Player {
    private String name;
    private float value;
//    private String cardType;

    public Player(String name, float value){
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }

    public float getValue() { return value; }

//    public String getCardType() { return cardType; }
//
//    public void setCardType(String cardType) { this.cardType = cardType; }

    public void setName(String name) { this.name = name; }

    public void setValue(int value) { this.value = value; }
}
