package com.ruiteixeira.chatapp;

import com.ruiteixeira.chatapp.user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);

        while(true) {
            Socket socket = serverSocket.accept();
            new Thread(new User(socket)).start();
        }
    }
}
