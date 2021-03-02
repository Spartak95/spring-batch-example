package com.polixis.entity;

public class User {

    private String firstName;
    private String lastName;
    private String date;

    public User() {
    }

    public User(String firstName, String lastName, String date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
