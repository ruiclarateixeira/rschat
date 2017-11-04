package com.ruiteixeira.chatapp.user;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *

 */
public class User implements Runnable {
    private Socket socket;
    private String name;
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9]*$";

    public User(Socket socket) throws IOException {
        this.socket = socket;
        sendToClient("Welcome!\n");
    }

    public void run() {
        BufferedReader inFromClient;
        try {
            while(true) {
                inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                String input = inFromClient.readLine();

                if(input == null) {
                    System.out.println("Client disconnected");
                    break;
                }

                handleInput(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the raw input received from the client
     * @param input as it was sent from the client
     * @throws IOException if it fails to connect back with the client
     */
    private void handleInput(String input) throws IOException {
        if(input.startsWith("LOGIN "))
            login(input.substring(6));
        else if(this.name == null)
            sendToClient("ERROR Not currently logged in");
        else if(input.startsWith("MESSAGE "))
            sendMessage(input.substring(8));
        else
            sendToClient("ERROR Unsupported command");
    }

    /**
     * Handle a send message command from the client
     * @param input to the message command
     * @throws IOException if it fails to connect back with the client
     */
    private void sendMessage(String input) throws IOException {
        String[] splits = input.split(" ");
        User target;

        if(splits.length < 2) {
            sendToClient("ERROR Expected a target and a non empty message");
            return;
        }

        target = UserManager.getUser(splits[0]);
        if(target == null) {
            sendToClient(String.format("ERROR The user %s is not logged in.", splits[0]));
            return;
        }

        target.sendToClient(String.format("MESSAGE %s %s", this.name, input.substring(target.name.length() + 1)));
    }

    /**
     * Try to login - It will fail if already logged in or if the username fails validations
     * @param usernane username to log in with
     * @throws IOException if it fails to communicate with the client
     */
    private void login(String usernane) throws IOException {
        if(this.name != null) {
            sendToClient(String.format("ERROR Already logged in as %s", this.name));
        }
        else if(!usernane.matches(USERNAME_REGEX)) {
            sendToClient(String.format("ERROR Username %s should be alphanumeric!", usernane));
        }
        else if(UserManager.getUser(usernane) != null) {
            sendToClient(String.format("ERROR User %s is already taken", usernane));
        }
        else {
            this.name = usernane;
            UserManager.addUser(this);
            sendToClient("SUCCESS Logged In!");
        }
    }

    /**
     * Send a message to the client associated with this user
     * @param message message to be sent
     * @throws IOException if it fails to send the message
     */
    private void sendToClient(String message) throws IOException {
        DataOutputStream outToClient = new DataOutputStream(this.socket.getOutputStream());
        message = message.endsWith("\n") ? message : message + "\n";
        outToClient.writeBytes(message);
    }

    String getName() {
        return name;
    }
}
