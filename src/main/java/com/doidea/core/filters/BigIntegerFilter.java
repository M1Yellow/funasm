package com.doidea.core.filters;

import java.math.BigInteger;

public class BigIntegerFilter {

    // TODO 从这里 https://jetbra.in/s 找到对应软件的 key 进行激活
    //GBNTK05GYE-eyJsaWNlbnNlSWQiOiJHQk5USzA1R1lFIiwibGljZW5zZWVOYW1lIjoiVGVzdCBvbmx5IiwiYXNzaWduZWVOYW1lIjoiVGVzdCBvbmx5IiwiYXNzaWduZWVFbWFpbCI6IiIsImxpY2Vuc2VSZXN0cmljdGlvbiI6IiIsImNoZWNrQ29uY3VycmVudFVzZSI6ZmFsc2UsInByb2R1Y3RzIjpbeyJjb2RlIjoiSUkiLCJmYWxsYmFja0RhdGUiOiIyMDI2LTEyLTMxIiwicGFpZFVwVG8iOiIyMDI2LTEyLTMxIn0seyJjb2RlIjoiUENXTVAiLCJmYWxsYmFja0RhdGUiOiIyMDI2LTEyLTMxIiwicGFpZFVwVG8iOiIyMDI2LTEyLTMxIn0seyJjb2RlIjoiUFNJIiwiZmFsbGJhY2tEYXRlIjoiMjAyNi0xMi0zMSIsInBhaWRVcFRvIjoiMjAyNi0xMi0zMSJ9XSwibWV0YWRhdGEiOiIwMTIwMjMwMTAyUFBBQTAxMzAwOSIsImhhc2giOiI0MTQ3Mjk2MS8wOjE1NjM2MDk0NTEiLCJncmFjZVBlcmlvZERheXMiOjcsImF1dG9Qcm9sb25nYXRlZCI6dHJ1ZSwiaXNBdXRvUHJvbG9uZ2F0ZWQiOnRydWV9-ssltaaliMLPjAA0NWo8mRPSrqa5xubVyr8ZJBNkc38tr+PhgPgcx+gG8dADD9kyIKCTX/qa9z0g6n60rpitBRQeZVE2Hf+Yb/9aVU1N6W+cQE1DTIW8e0IwNneIWOuZ1fCi+e6SoAipa6loQMggTU7xHocNA9o55sCwY1o/4MGQzgL4e8nksXXd2cJKF5zNc2Fm4psyw0D+yxhtWiHwUFW/pxigznKDVnKjC0ctvHaGDy83/RIsLZNaryfdFxLED3P4D9NBA+StLUPPOsrqUHscwAxDM8UpqIi0x5XVVXt0BruH3fbTbg969sBSPguim6dOI2iT5pBsJMpwW4tQdk1qCjPhWdUi7HqPRb9F+4L78tR3PkE/YoNd4vlmHMRZ/UVkJIy7yw3bqU+eHHTs0A4WVbppk9KdaBZsnVZK6xcj6bE1JKIvtMTT14FRJP8Ycw/7UOciY17fVdyBj0gXV5jgNnn27vqbb3j6TzyKJr3nlT7lMsZW4CN7fatEHV1UAs9q2tfomhXrPKQhICpajr0my5pCbsUGIq1eXwYhoZm48PWbYZBbO3bA+x3PgUxidr2hdgKT2SkJYzgTglMnLRHCGILDhNy6BhDs6AnWst7+Jwez8ELvPyp1dzh/dEK1dB0ACkxPouzNrd8F+fvxW4wFusp70xLrfNwIAqEcfVL4=-MIIEtTCCAp2gAwIBAgIUDyuccmylba71lZQAQic5TJiAhwwwDQYJKoZIhvcNAQELBQAwGDEWMBQGA1UEAwwNSmV0UHJvZmlsZSBDQTAeFw0yMzA5MjkxNDA2MTJaFw0zMzA5MjcxNDA2MTJaMBExDzANBgNVBAMMBk5vdmljZTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBALenqcGP2ZxGkYqmKA9c4Hzf8+YD1smvmOxKjd+bmTLrutM/hXv1cj1rW3/lqyDtdDk7K6W8/TDq1CRrEt+Do6l30DxhAiC34aH8DmGwgq77xEoLimvH5LpePxflF+tbB1RZtFgFDOIYLdSQaKFH2JDgVKxhLiV3S6jniPhkCtWWrTs+E6vq4N15Bm3NnM5AJILqjtUbOjNfaxVq6RrOoTc0R3Fqqo6yvxo/+JYa2UnHIC+r2dbKuDLMUrtgnydEUdJNX0zH9FtcdELvr48uc9mY038TWUsZUK1pnQbxA2bPyA4qnYJ9IvUgO6LtLXvGFm137YQMS1N41AHDBOrwoNI8UoDX+qI3rM96biFOFvn7Edky7rByzybt3H+zxdojfjvpL1E0NO98BT9zfufHAaAxZtlmDOu5LDJe3CGurnyRMRExbtc+Qjl1mUh6tG4lakAwdsoxry0GdG72yaYyb9it53kaFks/T/s7Z7bRJzVFzQDV1Y4bzUtk43vKm2vztBVlQkBkZY5f2Jbe5Ig3b8swQzBnOT0mrL5SPUhwmQ6IxkEWztj55OEujBMmRr92oESuq9ZYMaeLidKWVR3/++HA8BRZaRGEKtSHZCbFEFdihDxxJv9Xh6NuT/ewJ6HYp+0NQpFnUnJ72n8wV+tudpam7aKcdzVmz7cNwOhG2Ls7AgMBAAEwDQYJKoZIhvcNAQELBQADggIBAIdeaQfKni7tXtcywC3zJvGzaaj242pSWB1y40HW8jub0uHjTLsBPX27iA/5rb+rNXtUWX/f2K+DU4IgaIiiHhkDrMsw7pivazqwA9h7/uA0A5nepmTYf/HY4W6P2stbeqInNsFRZXS7Jg4Q5LgEtHKo/H8USjtVw9apmE3BCElkXRuelXMsSllpR/JEVv/8NPLmnHSY02q4KMVW2ozXtaAxSYQmZswyP1YnBcnRukoI4igobpcKQXwGoQCIUlec8LbFXYM9V2eNCwgABqd4r67m7QJq31Y/1TJysQdMH+hoPFy9rqNCxSq3ptpuzcYAk6qVf58PrrYH/6bHwiYPAayvvdzNPOhM9OCwomfcazhK3y7HyS8aBLntTQYFf7vYzZxPMDybYTvJM+ClCNnVD7Q9fttIJ6eMXFsXb8YK1uGNjQW8Y4WHk1MCHuD9ZumWu/CtAhBn6tllTQWwNMaPOQvKf1kr1Kt5etrONY+B6O+Oi75SZbDuGz7PIF9nMPy4WB/8XgKdVFtKJ7/zLIPHgY8IKgbx/VTz6uBhYo8wOf3xzzweMnn06UcfV3JGNvtMuV4vlkZNNxXeifsgzHugCvJX0nybhfBhfIqVyfK6t0eKJqrvp54XFEtJGR+lf3pBfTdcOI6QFEPKGZKoQz8Ck+BC/WBDtbjc/uYKczZ8DKZu

    /**
     * 证书的签名密文
     */
    private static String x;
    /**
     * 证书指数 65537
     */
    private static String y;
    /**
     * 内置根证书的公钥
     */
    private static String z;
    /**
     * 替换的结果
     */
    private static String r;


    public static void setX(String x) {
        BigIntegerFilter.x = x;
    }

    public static void setY(String y) {
        BigIntegerFilter.y = y;
    }

    public static void setZ(String z) {
        BigIntegerFilter.z = z;
    }

    public static void setR(String r) {
        BigIntegerFilter.r = r;
    }

    public static BigInteger testFilter(BigInteger x, BigInteger y, BigInteger z) {

        if (!y.equals(new BigInteger(BigIntegerFilter.y))) return null;
        if (!x.toString().startsWith("55225648167853313963797564236788")) return null;
        if (!z.toString().startsWith("86010657695287910119278227887631")) return null;

        // TODO BigInteger oddModPow 调用的地方比较多，打印太多日志可能会影响正常日志查看，建议适配好之后，关掉打印
        System.out.println(">>>> oddModPow x=" + x);
        System.out.println(">>>> oddModPow y=" + y);
        System.out.println(">>>> oddModPow z=" + z);

        if (x.equals(new BigInteger(BigIntegerFilter.x))) {
            if (y.equals(new BigInteger(BigIntegerFilter.y))) {
                if (z.equals(new BigInteger(BigIntegerFilter.z))) {
                    return new BigInteger(BigIntegerFilter.r);
                }
            }
        }

        return null;
    }
}
