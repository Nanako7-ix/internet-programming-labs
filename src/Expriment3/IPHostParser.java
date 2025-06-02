package Experiment3;
 
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
 
public class IPHostParser {
    private static boolean isIpAddress (String s) throws UnknownHostException {
        return InetAddress.getByName(s).getHostAddress().equals(s);
    }
 
    private static ArrayList<String> parse(String s) throws UnknownHostException {
        ArrayList<String> ans = new ArrayList<>();

        if (isIpAddress(s)) {
            InetAddress addr = InetAddress.getByName(s);
            ans.add(addr.getHostName());
        } else {
            for (InetAddress addr : InetAddress.getAllByName(s)) {
                ans.add(addr.getHostAddress());
            }
        }
        return ans;
    }
 
    public static void main(String[] args) throws UnknownHostException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入域名或IP地址：");
        String input = scanner.nextLine().trim();
 
        for (String s : parse(input)) {
            System.out.println(s);  
        }
        scanner.close();
    }
}
