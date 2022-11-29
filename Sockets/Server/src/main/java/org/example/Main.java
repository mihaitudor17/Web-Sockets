package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    static final int  PORT=8080;

    public static void main(String args[]) throws IOException {

        ServerSocket serverSocket = null;

        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {

                socket = serverSocket.accept();

                System.out.println("Client connected");

                System.out.println("Client socket: " + socket);

            } catch (IOException e) {

                System.out.println("I/O error: " + e);

                break;
            }
            new EchoThread(socket).start();

        }
    }

}