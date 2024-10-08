package ru.gpncr.chat.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticatedProvider authenticatedProvider;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.authenticatedProvider = new InMemoryAuthenticationProvider(this);
        this.authenticatedProvider.initialize();

    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server run port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
               new ClientHandler(socket,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("В чат зашел: " + clientHandler.getUserName());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел: " + clientHandler.getUserName());
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public boolean sendPrivateMessage(String userSender, String recipientName, String message) {
        for (ClientHandler c : clients) {
            if (c.getUserName().equals(recipientName)) {
                c.sendMessage("Личное сообщение от " + userSender + ": " + message);
                return true;
            }
        }
        return false;
    }

    public boolean isUserBusy(String usename){
        for (ClientHandler c : clients) {
         if (c.getUserName().equals(usename)){
             return true;
         }
        }
        return false;
    }
}
