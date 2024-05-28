package models;

public class Guest extends User {
    public Guest(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean getIsGuest() {
        return true;
    }
}