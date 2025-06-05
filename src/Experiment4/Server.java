package Experiment4;

import java.io.*;
import java.net.*;
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
                    try (
                        OutputStream out = client.getOutputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
                    ) {
                        Map<String, String> header = parseHeader(in);
                        String body = null;
                        if (header.get("Method").equalsIgnoreCase("POST")) {
                            int length = Integer.parseInt(header.getOrDefault("Content-Length", "0"));
                            char[] bodyChars = new char[length];
                            int read = in.read(bodyChars);
                            if (read != length) {
                                System.out.println("读取的 body 长度不足，可能是请求未完整发送");
                            }
                            body = new String(bodyChars);
                        }

                        String response = """
                                HTTP/1.1 200 OK\r
                                Content-Type: text/html\r
                                \r
                                <html><body><h1>Hello from Java HTTP Server</h1></body></html>
                                """;

                        out.write(response.getBytes());
                        out.flush();
                    } catch (IOException e) {
                        System.out.println("输出或读取失败" + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("无法启动服务器" + e.getMessage());
        }
    }
}
