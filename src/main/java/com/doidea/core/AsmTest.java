package com.doidea.core;

public class AsmTest {
    public static void main(String[] args) {
        String title = "Licenses";
        System.out.println(title);
        if (title.trim().equalsIgnoreCase("Licenses") || title.trim().equalsIgnoreCase("许可证")) {
            throw new RuntimeException("Licenses dialog abort.");
        }
    }
}
