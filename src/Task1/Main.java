package Task1;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    private static ArrayList<String> parse(String s) throws UnknownHostException {
        ArrayList<String> ans = new ArrayList<>();
        InetAddress addr = InetAddress.getByName(s);

        if (addr.getHostAddress().equals(s)) {
            ans.add(addr.getHostName());
        } else {
            InetAddress[] addresses = InetAddress.getAllByName(s);
            for (InetAddress a : addresses) {
                ans.add(a.getHostAddress());
            }
        }
        return ans;
    }

    public static void main(String[] args) throws UnknownHostException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入域名或IP地址：");
        String input = scanner.nextLine().trim();

        ArrayList<String> result = parse(input);
        for (String s : result) {
            System.out.println(s);
        }
        scanner.close();
    }
}
