package ru.gpncr.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client() throws IOException{
        Scanner scanner = new Scanner(System.in);
        socket = new Socket("localhost", 8189);
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message.equalsIgnoreCase("/exitok")){
                        break;
                    }

                    if (message.equalsIgnoreCase("/exit")){
                        disconnect();
                        System.exit(0);
                    }

                    if (message.startsWith("/authok")){
                        System.out.println("Аутетификация прошла успешно с именем пользователя: "+message.split(" ")[1]);
                    }
                    if (message.startsWith("/regok")){
                        System.out.println("Регистрация прошла успешно с именем пользователя: "+message.split(" ")[1]);
                    }
                    System.out.println(message);
                }
            } catch (IOException  e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();

        while (true) {
            String message = scanner.nextLine();
            out.writeUTF(message);
            out.flush();
            if (message.equalsIgnoreCase("/exit")) {
                break;
            }
        }
    }

    public void disconnect() {

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
