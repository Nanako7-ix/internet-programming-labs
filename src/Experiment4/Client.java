package Experiment4;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", 8080);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {

            // HTTP 请求头
            writer.println("GET / HTTP/1.1");
            writer.println("Host: localhost");
            writer.println();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println("连接服务器失败" + e.getMessage());
        }
    }
}