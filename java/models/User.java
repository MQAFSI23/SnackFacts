package models;

import dao.UserDao;

public class User extends AbstractUser {
    public User(String username, String password) {
        super(username, password);
    }

    public User(String username, String password, String nickname) {
        super(username, password, nickname);
    }

    @Override
    public boolean authenticate() {
        UserDao userDao = new UserDao();
        User user = userDao.getUserByUsername(this.username);
        if (user != null && user.getPassword().equals(this.password)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsernameAvailable() {
        UserDao userDao = new UserDao();
        User user = userDao.getUserByUsername(this.username);
        if (user == null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNicknameAvailable() {
        UserDao userDao = new UserDao();
        User user = userDao.getUserByNickname(this.nickname);
        if (user == null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean getIsGuest() {
        return false;
    }
}
