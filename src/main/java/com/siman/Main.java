package main.java.com.siman;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // AI名を出力
        String name = "siman";
        System.out.println(name);
        System.out.flush();

        Codevs codevs = new Codevs();

        while (true) {
            try (Scanner sc = new Scanner(System.in)) {
                codevs.readTurnInfo(sc);
            }
        }
    }
}
