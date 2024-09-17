package ru.gpncr.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClienApplication {

    public static void main(String[] args) throws IOException{
        new Client();
    }
}
