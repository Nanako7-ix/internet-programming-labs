package Experiment2;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;


public class ReverseClient {
    static final int PORT = 8080;
    static final String file = "./input.txt";
    public static void main(String[] args) {
        File f = new File(file);
        System.out.println(f.getAbsoluteFile());
        try (
            Socket socket = new Socket(InetAddress.getLocalHost(), PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new FileReader(file))
        ) {
            System.out.println("Connected to Server");
            String input;
            while ((input = reader.readLine()) != null) {
                out.println(input);
                System.out.println(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}