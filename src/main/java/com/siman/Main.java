package main.java.com.siman;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // AI名を出力
        String name = "siman";
        System.out.println(name);
        System.out.flush();

        Codevs codevs = new Codevs();
        codevs.init();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                codevs.readTurnInfo(sc);
                codevs.beforeProc();
                ActionInfo[] actions = codevs.action();
                codevs.output(actions);
            }
        }
    }
}
