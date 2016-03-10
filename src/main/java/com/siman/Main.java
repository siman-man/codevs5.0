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
                CommandList commandList = new CommandList();
                codevs.readTurnInfo(sc);
                codevs.beforeProc(commandList);
                commandList.actions = codevs.action(commandList);
                codevs.output(commandList);
            }
        }
    }
}
