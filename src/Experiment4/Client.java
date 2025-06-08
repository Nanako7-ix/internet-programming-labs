package Experiment4;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static String Cookie = null;
    private static Scanner scanner;
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        while (true) {
            System.out.println("输入请求方法或者exit以退出");
            String s = scanner.nextLine();
            if (s.equals("exit")) break;
            try (
                Socket server = new Socket("localhost", 12345);
                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()))
            ) {
                try {
                    switch (s) {
                        case "HEAD" -> HEAD(out);
                        case "GET" -> GET(out);
                        case "POST" -> POST(out);
                        default -> {
                            System.out.println("请求方法错误");
                            continue;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("无法发送请求报文: " + e.getMessage());
                }
                try {
                    String response;
                    int contentLength = 0;
                    while ((response = in.readLine()) != null) {
                        if (response.isEmpty()) break;
                        System.out.println(response);

                        if (response.startsWith("Content-Length:")) {
                            contentLength = Integer.parseInt(response.substring("Content-Length:".length()).trim());
                        }
                        if (response.startsWith("Set-Cookie:")) {
                            Cookie = response.substring("Set-Cookie:".length()).trim().split(";", 2)[0];
                        }
                    }
                    if (!s.equals("HEAD") && contentLength > 0) {
                        System.out.println();
                        char[] body = new char[contentLength];
                        int read = in.read(body, 0, contentLength);
                        System.out.println(URLDecoder.decode(new String(body, 0, read)));
                    }
                } catch (IOException e) {
                    System.out.println("无法读取响应报文: " + e.getMessage());
                }
                System.out.println();
            } catch (IOException e) {
                System.out.println("连接服务器失败" + e.getMessage());
            }
        }
        scanner.close();
    }
    private static void GET(PrintWriter out) throws IOException {
        System.out.println("输入路径");
        String path = scanner.nextLine();
        StringBuilder builder = new StringBuilder();
        builder.append("GET ").append(path).append(" HTTP/1.1\r\n");
        builder.append("Host: localhost\r\n");
        if (Cookie != null) {
            builder.append("Cookie: ").append(Cookie).append("\r\n");
        }
        builder.append("Connection: close\r\n");
        out.print(builder.append("\r\n"));
        out.flush();
    }
    private static void HEAD(PrintWriter out) throws IOException {
        System.out.println("输入路径");
        String path = scanner.nextLine();
        StringBuilder builder = new StringBuilder();
        builder.append("HEAD ").append(path).append(" HTTP/1.1\r\n");
        builder.append("Host: localhost\r\n");
        if (Cookie != null) {
            builder.append("Cookie: ").append(Cookie).append("\r\n");
        }
        builder.append("Connection: close\r\n");
        out.print(builder.append("\r\n"));
        out.flush();
    }
    private static void POST(PrintWriter out) throws IOException {
        System.out.println("输入路径");
        String path = scanner.nextLine();
        System.out.println("输入POST内容");
        String body = scanner.nextLine();
        body = URLEncoder.encode(body, StandardCharsets.UTF_8);

        StringBuilder builder = new StringBuilder();
        builder.append("POST ").append(path).append(" HTTP/1.1\r\n");
        builder.append("Host: localhost\r\n");
        builder.append("Content-Type: text/plain\r\n");
        builder.append("Content-Length: ").append(body.length()).append("\r\n");
        if (Cookie != null) {
            builder.append("Cookie: ").append(Cookie).append("\r\n");
        }
        builder.append("Connection: close\r\n");
        out.print(builder.append("\r\n").append(body));
        out.flush();
    }
}