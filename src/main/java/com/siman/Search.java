package main.java.com.siman;

/**
 * Created by siman on 3/5/16.
 */
public class Search {
    // 通常時
    public static String[] LIST = {
            // 0マス (1パターン)
            "N",

            // 1マス移動 (4パターン)
            "U",
            "R",
            "D",
            "L",

            // 2マス移動  (12パターン)
            "UU",
            "UR",
            "UL",

            "RU",
            "RR",
            "RD",

            "DR",
            "DD",
            "DL",

            "LU",
            "LD",
            "LL",

            // 3マス移動
            "UUU",
            "UUL",
            "UUR",
    };
}
