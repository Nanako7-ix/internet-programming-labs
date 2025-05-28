package Task2;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WebPageDownloader {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入网页URL: ");
        String urlString = scanner.nextLine();

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            System.out.println("请求失败，响应码: " + responseCode);
            return;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line).append("\n");
        }
        in.close();

        System.out.println("网页源码内容如下：\n");
        System.out.println(content);

        String filename = "downloaded_page.html";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        writer.write(content.toString());
        writer.close();

        System.out.println("\n网页已保存为 " + filename);
    }
}