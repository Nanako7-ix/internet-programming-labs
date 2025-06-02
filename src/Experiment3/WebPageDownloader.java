package Experiment3;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WebPageDownloader {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入网页URL: ");
        String urlString = scanner.nextLine();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (connection.getResponseCode() != 200) {
                System.out.println("请求失败，响应码: " + responseCode);
                scanner.close();
                return;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException e) {
                System.out.println("无法读取网页源码内容" + e.getMessage());
            }

            System.out.println("网页源码内容如下：\n");
            System.out.println(content);

            String filename = "download.html";
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
                out.write(content.toString());
            } catch (IOException e) {
                System.out.println("无法写入文件" + e.getMessage());
            }
            System.out.println("\n网页已保存为 " + filename);

        } catch (MalformedURLException e) {
            System.out.println("URL格式错误" + e.getMessage());
        } catch (IOException e) {
            System.out.println("无法建立连接" + e.getMessage());
        }
        scanner.close();
    }
}