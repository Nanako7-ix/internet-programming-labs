package Experiment4;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final ExecutorService pool = Executors.newFixedThreadPool(100);
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(12345)) {
            while (true) {
                Socket client = server.accept();
                pool.submit(() -> {
                    System.out.println("\nClient Socket: " + client.getInetAddress() + ":" + client.getPort());
                    try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        OutputStream out = client.getOutputStream()
                    ) {
                        Handler handler = new Handler(out);
                        Map<String, String> header = new HashMap<>();
                        String body = "";
                        try {
                            String line = in.readLine();
                            if (line == null || line.isEmpty()) {
                                return;
                            }
                            System.out.println(line);
                            String[] firstLine = line.split(" ");
                            if (firstLine.length != 3) {
                                handler.BadRequest();
                                return;
                            }
                            header.put("Method", firstLine[0].trim());
                            header.put("Path", firstLine[1].trim());
                            header.put("Version", firstLine[2].trim());
                            while ((line = in.readLine()) != null && !line.isEmpty()) {
                                String[] headerParts = line.split(":", 2);
                                if (headerParts.length == 2) {
                                    header.put(headerParts[0].trim(), headerParts[1].trim());
                                } else {
                                    handler.BadRequest();
                                    return;
                                }
                            }
                            if (header.get("Method").equalsIgnoreCase("POST")) {
                                int length;
                                try {
                                    length = Integer.parseInt(header.getOrDefault("Content-Length", "0"));
                                } catch (NumberFormatException e) {
                                    handler.BadRequest();
                                    return;
                                }
                                if (length < 0) {
                                    handler.BadRequest();
                                    return;
                                }
                                char[] bodyChars = new char[length];
                                int read = in.read(bodyChars);
                                if (read != length) {
                                    handler.BadRequest();
                                    return;
                                }
                                body = new String(bodyChars);
                            }
                            handler.Respond(header, body);
                        } catch (IOException e) {
                            handler.InternalServerError();
                            System.out.println("无法读取请求报文" + e.getMessage());
                        }
                    } catch (IOException e) {
                        System.out.println("无法获取输入输出流: " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("无法启动服务器" + e.getMessage());
        }
    }
}