package ru.gpncr.chat.server;

public class ServerApplication {
    private static final int PORT = 8189;

    public static void main(String[] args) {
        new Server(PORT).start();

   }
}
