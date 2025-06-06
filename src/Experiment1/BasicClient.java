package Experiment1;

import java.io.*;
import java.net.*;

public class BasicClient {
    public static void main(String[] args) {
        try (Socket serverSocket = new Socket(InetAddress.getLocalHost(), 8080)) {
            System.out.println("Server connected -> IP: " + serverSocket.getInetAddress());

            try (BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true)) {
                // Sent messages to Server
                out.println("Nanako7_ix is the best ACMer");
                // Get messages from Server
                System.out.println("Server: " + in.readLine());
            } catch (IOException e) {
                System.out.println("Could not READ or WRITE to server");
            }
        } catch (IOException e) {
            System.out.println("Could not connect to client");
        }
    }
}