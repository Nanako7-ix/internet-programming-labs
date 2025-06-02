package Experiment2;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReverseServer {
    static final int PORT = 8080;
    static final int N = 10;
    static final ExecutorService pool = Executors.newFixedThreadPool(N);
    static final String ServerLog = "./ServerLog.log";
    public static void main(String[] args) {
        File dir = new File("./logs");
        dir.mkdirs();
        try (
            ServerSocket server = new ServerSocket(PORT);
        ) {
            while (true) {
                Socket client = server.accept();
                writeServerLog(
                    "[" + java.time.LocalDateTime.now() + "]\t"
                    + "  Client-" + client.getInetAddress().toString()
                    + ":" + client.getPort()
                    + "\tConnected!"
                );
                String file = "./logs/Client_"
                            + client.getInetAddress().toString().replace("/", "")
                            + "_"
                            + client.getPort()
                            + ".txt";
                pool.submit(() -> {
                    try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
                        PrintWriter writer = new PrintWriter(new FileWriter(file))
                    ) {
                        String message;
                        while ((message = in.readLine()) != null) {
                            writer.println("Client: " + message);
                            String reply = reverse(message);
                            writer.println("Server: " + reply + '\n');
                            out.println(reply);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        File f = new File(file);
                        String hash = computeSHA256(f);
                        writeSummary(f.getName(), hash);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    writeServerLog(
                        "[" + java.time.LocalDateTime.now() + "]\t"
                        + "  Client-" + client.getInetAddress().toString()
                        + ":" + client.getPort()
                        + "\tlose Connection!"
                    );
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String reverse(String s) {
       StringBuilder sb = new StringBuilder();
       for (int i = s.length() - 1; i >= 0; --i) {
           sb.append(s.charAt(i));
       }
       return sb.toString();
    }

    static String computeSHA256(File file) throws IOException {
        try (InputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            DigestInputStream dis = new DigestInputStream(fis, digest);
            byte[] buffer = new byte[4096];
            while (dis.read(buffer) != -1);
            byte[] hash = digest.digest();
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IOException("计算摘要失败", e);
        }
    }

    public static synchronized void writeSummary(String fileName, String hash) {
        try (PrintWriter summaryWriter = new PrintWriter(new FileWriter("SafeAbstract.txt", true))) {
            summaryWriter.println(fileName + " : " + hash);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeServerLog(String content) {
        try (
            PrintWriter writer = new PrintWriter(new FileWriter(ServerLog, true));
        ) {
            writer.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}