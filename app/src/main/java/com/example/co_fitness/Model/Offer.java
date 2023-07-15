package com.example.co_fitness.Model;

import java.util.ArrayList;
import java.util.UUID;

public class Offer {
    private String createdUserId;
    private ArrayList<String> usersID;
    private String id;
    private String type;
    private int capacity;
    private int numOfUsers;
    private String description;
    private String date;
    private String time;
    private String place;
    private String level;
    private String cost;

    public Offer() {
        this.id = UUID.randomUUID().toString();
        this.usersID = new ArrayList<>();
    }

    public Offer(String createdUserId, String type, int capacity, String description,
                 String date, String time, String place, String level, String cost) {
        this.createdUserId = createdUserId;
        this.numOfUsers = 1;
        this.type = type;
        this.capacity = capacity;
        this.description = description;
        this.date = date;
        this.time = time;
        this.place = place;
        this.level = level;
        this.cost = cost;
        this.id = UUID.randomUUID().toString();
        this.usersID = new ArrayList<>();
        this.usersID.add(CurrentUser.getInstance().getUserProfile().getUid());
    }


    public void addUser(String uid) {
        usersID.add(uid);
        numOfUsers++;
    }

    public void removeUser(String uid){
        usersID.remove(uid);
        numOfUsers--;
    }

    public boolean isUserInOffer(String uid) {
        return usersID.contains(uid);
    }
    public String getCreatedUserId() {
        return createdUserId;
    }

    public Offer setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
        return this;
    }

    public ArrayList<String> getUsersID() {
        return usersID;
    }

    public Offer setUsersID(ArrayList<String> usersID) {
        this.usersID = usersID;
        return this;
    }

    public String getId() {
        return id;
    }

    public Offer setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Offer setType(String type) {
        this.type = type;
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    public Offer setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int getNumOfUsers() {
        return numOfUsers;
    }

    public Offer setNumOfUsers(int numOfUsers) {
        this.numOfUsers = numOfUsers;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Offer setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Offer setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Offer setTime(String time) {
        this.time = time;
        return this;
    }

    public String getPlace() {
        return place;
    }

    public Offer setPlace(String place) {
        this.place = place;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public Offer setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getCost() {
        return cost;
    }

    public Offer setCost(String cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "user=" + createdUserId +
                ", usersID=" + usersID +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", capacity=" + capacity +
                ", numOfUsers=" + numOfUsers +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", place='" + place + '\'' +
                ", level='" + level + '\'' +
                ", cost='" + cost + '\'' +
                '}';
    }
}
