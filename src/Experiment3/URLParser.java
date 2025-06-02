package Experiment3;

import java.net.*;
import java.util.Scanner;

public class URLParser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入URL地址：");
        String urlString = scanner.nextLine().trim();

        try {
            URL url = new URL(urlString);

            System.out.println("协议 (Protocol): " + url.getProtocol());
            System.out.println("主机 (Host): " + url.getHost());
            System.out.println("端口 (Port): " + (url.getPort() == -1 ? "默认" : url.getPort()));
            System.out.println("路径 (Path): " + url.getPath());
            System.out.println("查询字符串 (Query): " + (url.getQuery() == null ? "无" : url.getQuery()));
            System.out.println("文件名 (File): " + url.getFile());
            System.out.println("引用 (Ref): " + (url.getRef() == null ? "无" : url.getRef()));

        } catch (MalformedURLException e) {
            System.out.println("URL格式错误: " + e.getMessage());
        }
        scanner.close();
    }
}