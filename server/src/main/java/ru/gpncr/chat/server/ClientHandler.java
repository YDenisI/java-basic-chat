package ru.gpncr.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

 /*       sendMessage("Введите ваше имя:");
        this.userName = in.readUTF();
        System.out.println("New Client "+ userName+" is connect");
        sendMessage("Вы успешно зашли в чат");
*/
        new Thread(() -> {
            try {
                System.out.println("Клиент подключился ");

                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.equalsIgnoreCase("/exit")) {
                            sendMessage("/exitok");
                            break;
                        }
                        if (message.startsWith("/auth")) {
                            String[] elements = message.split(" ");
                            if (elements.length != 3) {
                                sendMessage("Неверный формат команды /auth");
                                continue;
                            }
                            if (server.getAuthenticatedProvider().authenticate(this, elements[1], elements[2])) {
                                break;
                            }
                            continue;
                        }
                        if (message.startsWith("/reg")) {
                            String[] elements = message.split(" ");
                            if (elements.length != 4) {
                                sendMessage("Неверный формат команды /reg");
                                continue;
                            }
                            if (server.getAuthenticatedProvider().registration(this, elements[1], elements[2],elements[3])) {
                                break;
                            }
                            continue;
                        }
                    }
                    sendMessage("Перед работой необходимо пройти аутентификацию командой "+
                            "/auth login password или регстрацию командой /reg");
                }
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.equalsIgnoreCase("/exit")) {
                            sendMessage("/exitok");
                            break;
                        }

                        if (message.startsWith("/w ")) {
                            List<String> parts = Arrays.asList(message.split(" ", 3));
                            if (parts.size() < 3) {
                                sendMessage("Неправильный формат команды. Используйте: /w [имя] [сообщение]");
                                continue;
                            }
                            String recipientName = parts.get(1);
                            String personalMessage = parts.get(2);
                            if(!server.sendPrivateMessage(userName, recipientName, personalMessage)){
                                sendMessage("В чате "+userName+" не состоит");
                            }
                        }
                        continue;
                    }
                    server.broadcastMessage(userName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
                System.out.println("Client "+userName+" disconnect");
            }
        }).start();
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
