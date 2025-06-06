package Experiment4;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class Handler {
    private final OutputStream out;

    public Handler(OutputStream out) {
        this.out = out;
    }

    // 400 Bad Request
    public void BadRequest() {
        String response = """
                HTTP/1.1 400 Bad Request\r
                Content-Type: text/plain\r
                Content-Length: 15\r
                \r
                400 Bad Request
                """;
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            System.out.println("无法发送响应报文" + e.getMessage());
        }
    }
    // 500 读取请求报文失败
    public void InternalServerError() {
        String response = """
                HTTP/1.1 500 Internal Server Error\r
                Content-Type: text/plain\r
                Content-Length: 25\r
                \r
                500 Internal Server Error
                """;
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            System.out.println("无法发送响应报文" + e.getMessage());
        }
    }
    // 404 Not Found
    private void NotFound() {
        String response = """
                HTTP/1.1 404 Not Found\r
                Content-Type: text/plain\r
                Content-Length: 13\r
                \r
                404 Not Found
                """;
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            System.out.println("无法发送响应报文" + e.getMessage());
        }
    }
    // 405 Method Not Allowed
    private void MethodNotAllowed() {
        final String response = """
                HTTP/1.1 405 Method Not Allowed\r
                Content-Type: text/plain\r
                Allow: GET, POST, HEAD\r
                \r
                405 Method Not Allowed
                """;
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            System.out.println("无法发送响应报文" + e.getMessage());
        }
    }
    // 正常读取请求报文, 尝试处理 GET, POST, HEAD 请求
    public void Respond (Map<String, String> header, String body) {
        if (header == null || header.get("Method") == null) {
            BadRequest();
            return;
        }
        switch (header.get("Method").toUpperCase()) {
            case "GET" -> GET(header);
            case "POST" -> POST(header, body);
            case "HEAD" -> HEAD(header);
            default -> MethodNotAllowed();
        }
    }

    private String guessMimeType(String path) {
        if (path.endsWith(".html") || path.endsWith(".htm")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    private void GET(Map<String, String> header) {
        String path = header.get("Path");
        if (path.equals("/")) path = "/index.html";
        path = "./static" + path;
        File file = new File(path);

        // 404 Not Found
        if (!file.exists() || file.isDirectory()) {
            NotFound();
            return;
        }

        try {
            byte[] content;
            try {
                content = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                InternalServerError();
                return;
            }
            String mime = guessMimeType(path);
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mime + "\r\n" +
                    "Content-Length: " + content.length + "\r\n" +
                    "\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.write(content);
        } catch (IOException e) {
            System.out.println("无法发送响应报文" + e.getMessage());
        }
    }

    private void HEAD(Map<String, String> header) {
        String path = header.get("Path");
        if (path.equals("/")) path = "/index.html";
        path = "./static" + path;
        File file = new File(path);

        // 404 Not Found
        if (!file.exists() || file.isDirectory()) {
            NotFound();
            return;
        }

        try {
            String mime = guessMimeType(path);
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mime + "\r\n" +
                    "Content-Length: " + file.length() + "\r\n" +
                    "\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("无法发送响应报文" + e.getMessage());
        }
    }

    private void POST(Map<String, String> header, String body) {
        String path = header.get("Path");

        // 404 Not Found
        if (path == null || !path.equals("/echo")) {
            NotFound();
            return;
        }

        String contentType = header.getOrDefault("Content-Type", "text/plain");
        byte[] content = body.getBytes(StandardCharsets.UTF_8);

        System.out.println("Client Post: " + new String(content));

        try {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + content.length + "\r\n" +
                    "\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.write(content);
            out.flush();
        } catch (IOException e) {
            System.out.println("无法发送响应报文" + e.getMessage());
        }
    }
}