package models;

public abstract class AbstractUser {
    protected String username;
    protected String password;
    protected String nickname;

    public AbstractUser(String username, String password) { // login
        this.username = username;
        this.password = password;
    }

    public AbstractUser(String username, String password, String nickname) { // register
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public abstract boolean getIsGuest();

    public abstract boolean authenticate();

    public abstract boolean isUsernameAvailable();

    public abstract boolean isNicknameAvailable();
}