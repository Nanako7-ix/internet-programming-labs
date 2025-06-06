package Experiment4;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final ExecutorService pool = Executors.newFixedThreadPool(100);

    private static Map<String, String> parseHeader (BufferedReader in) throws IOException {
        Map<String, String> header = new HashMap<>();

        String line = in.readLine();
        if (line == null || line.isEmpty()) {
            return header;
        }

        StringBuilder builder = new StringBuilder(line).append("\r\n");
        String[] firstLine = line.split(" ");
        if (firstLine.length != 3) {
            throw new IOException("无效的请求行: " + line);
        }
        header.put("Method", firstLine[0].trim());
        header.put("Path", firstLine[1].trim());
        header.put("Version", firstLine[2].trim());

        while ((line = in.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                header.put(headerParts[0].trim(), headerParts[1].trim());
            }
            builder.append(line).append("\r\n");
        }

        header.put("FullHeader", builder.toString());
        return header;
    }

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(12345)) {
            while (true) {
                Socket client = server.accept();
                pool.submit(() -> {
                    Map<String, String> header = new HashMap<>();
                    String body = "";
                    // 读取请求报文
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                        header = parseHeader(in);
                        if (header.get("Method").equalsIgnoreCase("POST")) {
                            int length = Integer.parseInt(header.getOrDefault("Content-Length", "0"));
                            char[] bodyChars = new char[length];
                            int read = in.read(bodyChars);
                            if (read != length) {
                                System.out.println("读取的 body 长度不足，可能是请求未完整发送");
                            }
                            body = new String(bodyChars);
                        }
                    // 发送响应报文
                        try (OutputStream out = client.getOutputStream()) {
                            Handler.Respond(header, body, out);
                        } catch (IOException e) {
                            System.out.println("无法发送响应报文" + e.getMessage());
                        }
                    } catch (IOException e) {
                        //
                        System.out.println("无法读取请求报文" + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("无法启动服务器" + e.getMessage());
        }
    }

    static String GET(Map<String, String> header) {
        String path = header.get("Path");
        if (path.equals("/")) path = "/index.html";
        path = "./static/" + path;
        File file = new File(path);

        // 404 Not Found
        if (!file.exists() || file.isDirectory()) {
            return """
                   HTTP/1.1 404 Not Found\r
                   Content-Type: text/plain\r
                   \r
                   404 Not Found
                   """;
        }

        try {
            byte[] content = Files.readAllBytes(file.toPath());
            String mime = guessMimeType(path);
            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mime + "\r\n" +
                    "Content-Length: " + content.length + "\r\n" +
                    "\r\n" +
                    new String(content);  // TODO: 后续需要用字节流处理
        } catch (IOException e) {
            return """
               HTTP/1.1 500 Internal Server Error\r
               Content-Type: text/plain\r
               \r
               500 Internal Server Error
               """;
        }
    }

    static void MethodNotAllowed(OutputStream out) throws IOException {
        final String respond = """
               HTTP/1.1 405 Method Not Allowed\r
               Content-Type: text/plain\r
               Allow: GET, POST, HEAD\r
               \r
               405 Method Not Allowed
               """;
        out.write(respond.getBytes());
        out.flush();
    }
}