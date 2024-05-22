package com.doidea.asm;

import java.util.stream.Stream;

public class AsmTest {
    public static void main(String[] args) {

        Stream.of(Thread.currentThread().getStackTrace()).forEach(System.out::println);

        String title = "Licenses";
        System.out.println(title);
        // title = title.trim(); // 尽可能不改动原 title
        if (title.trim().equalsIgnoreCase("Licenses") || title.trim().equalsIgnoreCase("许可证")) {
            throw new RuntimeException("Licenses dialog abort.");
        }
    }

    public static String test() {
        //String mid = "v53d4b07-g5y7-3fi9-vs34-b5t8cd21s7f4";
        //System.out.println(mid);
        return "143d4b85-a6ab-4ffb-b9cc-f2d3cd0817f4";
    }
}
