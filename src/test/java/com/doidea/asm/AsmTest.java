package com.doidea.asm;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

public class AsmTest {
    public static void main(String[] args) {

        //Stream.of(Thread.currentThread().getStackTrace()).forEach(System.out::println);
        //new RuntimeException(">>>> Print stacktrace: \n").printStackTrace();

        //System.out.println(UUID.randomUUID().toString()); // 1d1d4ae4-6718-442f-80a0-9006d48dc3f7

        test02("xxxx".getBytes(StandardCharsets.UTF_8), 10000L);

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

    public static byte[] test02(byte[] bArr, long j) {
        System.out.println(new String(bArr));
        System.out.println(j);
        // 每月 14 号变换一次
        LocalDateTime now = LocalDateTime.now();
        //now = LocalDateTime.of(2024, 6, 1, 23, 59, 59, 11);
        int y = now.getYear();
        int m = now.getMonthValue();
        int d = now.getDayOfMonth();
        String arg1 = "MAC-" + y + m + 14;
        if (d >= 14) {
            arg1 = "MAC-" + y + m + 28;
        }
        bArr = arg1.getBytes(StandardCharsets.UTF_8);
        System.out.println(new String(bArr));

        return bArr;
    }

    public static String testMid(int r8, int r9) {
        System.out.println(r8);
        System.out.println(r9);
        r8 += 111;
        r9 += 11111;

        String machineId = UUID.randomUUID().toString();
        return machineId;
    }
}
