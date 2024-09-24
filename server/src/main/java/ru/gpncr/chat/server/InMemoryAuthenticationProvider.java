package ru.gpncr.chat.server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticatedProvider{

    private class User{
        private String login;
        private String password;
        private String username;

        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    private List<User> users;
    private Server server;

    public InMemoryAuthenticationProvider(Server server) {
        this.server = server;
        this.users = new ArrayList<>();
        this.users.add(new User("login1", "password1", "username1"));
        this.users.add(new User("login2", "password2", "username2"));
        this.users.add(new User("login3", "password2", "username3"));
    }

    @Override
    public void initialize() {
        System.out.println("Сервис аутентификации запущен: In memory режим");
    }

    @Override
    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String authName = getUsernameByLoginAndPassword(login,password);

        if (authName == null){
            clientHandler.sendMessage("Некорретный логин/пароль");
            return false;
        }
        if(server.isUserBusy(authName)){
            clientHandler.sendMessage("Учетная запись уже занята");
            return false;
        }
        clientHandler.setUserName(authName);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/authok "+authName);
        return true;
    }

    private String getUsernameByLoginAndPassword(String login, String password){
        for (User user: users){
            if(user.login.equals(login) && password.equals(password)){
                return user.username;
            }
        }
        return null;
    }

    private boolean isLoginAlreadyExist(String login){
        for (User user : users) {
            if(user.login.equals(login)){
                return true;
            }
        }
        return false;
    }

    private boolean isUsernameAlreadyExist(String username){
        for (User user : users) {
            if(user.username.equals(username)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username) {

        if(login.trim().length() <= 3 || password.trim().length() <= 6 || username.trim().length() <= 2){
            clientHandler.sendMessage("Требования логин 3+ символов, пароль 6+ символов, имя пользователя 2+ символа не выполнены");
            return false;
        }
        if(isLoginAlreadyExist(login)){
            clientHandler.sendMessage("Логин занят");
            return false;
        }
        if(isUsernameAlreadyExist(username)){
            clientHandler.sendMessage("Имя пользователя занято");
            return false;
        }

        users.add(new User(login,password,username));
        clientHandler.setUserName(username);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/regok "+username);

        return false;
    }
}
