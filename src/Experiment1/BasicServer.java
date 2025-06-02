package Experiment1;

import java.io.*;
import java.net.*;

public class BasicServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server Created");

            var clientSocket = serverSocket.accept();
            System.out.println("Client connected -> IP: " + clientSocket.getInetAddress());

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                // Get messages from Client
                System.out.println("Client: " + in.readLine());
                // Sent messages to Client
                out.println("Receive messages successfully");
            } catch (IOException e) {
                System.out.println("Could not READ or WRITE to client");
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port: 8080");
        }
    }
}