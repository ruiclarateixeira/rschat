package com.ruiteixeira.chatapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket clientSocket = new Socket("localhost", 9000);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        while(true) {
            outToServer.writeBytes("Hello World" + '\n');
            Thread.sleep(1000);
        }
    }
}
