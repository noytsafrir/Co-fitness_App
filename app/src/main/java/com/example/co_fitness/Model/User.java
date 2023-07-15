package com.example.co_fitness.Model;


//import com.example.co_fitness.model.group.Group;
import static com.example.co_fitness.Constants.PROFILE_IMAGE_DEFAULT;

import java.util.ArrayList;

public class User {
    private boolean isRegistered;
    private String uid;
    private String name;
    private String gender;
    private String dateOfBirth;
    private String image;
    private String phoneNumber;
    private String address;
    private ArrayList<String> offers = new ArrayList<String>();


    public User() {
        this.image = PROFILE_IMAGE_DEFAULT;
    }

    public User(String uid, String name, String email) {
        this.isRegistered = false;
        this.uid = uid;
        this.name = name;
        this.phoneNumber = "";
        this.address = "";
        this.gender = "";
        this.dateOfBirth = "";
        this.image = PROFILE_IMAGE_DEFAULT;
    }


    public void addOffer(String id){
        offers.add(id);
    }

    public void removeOffer(String id) {
        offers.remove(id);
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }

    public User setIsRegistered(boolean registered) {
        isRegistered = registered;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public User setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public User setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getImage() {
        return image;
    }

    public User setImage(String image) {
        this.image = image;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public ArrayList<String> getOffers() {
        return offers;
    }

    public User setOffers(ArrayList<String> offers) {
        this.offers = offers;
        return this;
    }


    //    public void setUser(User currentUser) {
//        this.name = currentUser.getName();
//        this.uid = currentUser.getUid();
//        this.isRegistered = currentUser.isRegistered();
//        this.gender = currentUser.getGender();
//        this.age = currentUser.getAge();
//        this.image = currentUser.getImage();
//        this.email = currentUser.getEmail();
//        this.phoneNumber = currentUser.getPhoneNumber();
//        this.address = currentUser.getAddress();
//        this.level = currentUser.getLevel();
//        this.displayName = currentUser.getDisplayName();
//        this.offers = currentUser.getOffers();
//        if(this.offers == null)
//            this.offers = new HashMap<>();
//    }


    @Override
    public String toString() {
        return "User{" +
                "isRegistered=" + isRegistered +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", image='" + image + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", offers=" + offers +
                '}';
    }
}
