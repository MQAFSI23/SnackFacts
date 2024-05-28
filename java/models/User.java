package models;

import dao.UserDao;

public class User {
    protected String username;
    protected String password;
    protected String nickname;

    public User(String username, String password) { // authenticate
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String nickname) { // register
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public boolean authenticate() {
        UserDao userDao = new UserDao();
        User user = userDao.getUserByUsername(this.username);
        if (user != null && user.getPassword().equals(this.password)) {
            return true;
        }
        return false;
    }

    public boolean isUsernameAvailable() {
        UserDao userDao = new UserDao();
        User user = userDao.getUserByUsername(this.username);
        if (user == null) {
            return true;
        }
        return false;
    }

    public boolean isNicknameAvailable() {
        UserDao userDao = new UserDao();
        User user = userDao.getUserByNickname(this.nickname);
        if (user == null) {
            return true;
        }
        return false;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public boolean getIsGuest() {
        return false;
    }
}