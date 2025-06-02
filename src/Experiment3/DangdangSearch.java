package Experiment3;

import java.io.*;
import java.net.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DangdangSearch {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入搜索关键词：");
        String keyword = scanner.nextLine().trim();
        scanner.close();

        try {
            String baseURL = "https://search.dangdang.com/";
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String fullURL = baseURL + "?key=" + encodedKeyword;

            System.out.println("请求 URL：" + fullURL);

            URL url = new URL(fullURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (conn.getResponseCode() != 200) {
                System.out.println("请求失败，响应码: " + responseCode);
                return;
            }

            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GB2312"));
                BufferedWriter out = new BufferedWriter(new FileWriter("SearchResult.html"))
            ) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line); // 控制台显示
                    out.write(line);
                    out.newLine();
                }
            } catch (IOException e) {
                System.out.println("无法写入文件或搜索失败" + e.getMessage());
            }

            System.out.println("搜索结果已保存为 productResult.html");

        } catch (Exception e) {
            System.out.println("搜索失败: " + e.getMessage());
        }
    }
}