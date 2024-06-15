package com.doidea.core.filters;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DNSFilter {

    private static List<String> DNSList;

    public static void setDNSList(List<String> DNSList) {
        DNSFilter.DNSList = DNSList;
    }

    public static String testQuery(String host) throws UnknownHostException {
        if (null == host || host.trim().isEmpty() || null == DNSList || DNSList.isEmpty())
            return null;

        System.out.println(">>>> DNSFilter testQuery host: " + host);
        for (String dnsHost : DNSList) {
            if (host.toLowerCase().contains(dnsHost)) throw new java.net.UnknownHostException();
        }
        return host;
    }

    public static Object testReachable(InetAddress n) {
        if (null == n || null == n.getHostName() || n.getHostName().trim().isEmpty()
                || null == DNSList || DNSList.isEmpty())
            return null;
        for (String dnsHost : DNSList) {
            if (n.getHostName().toLowerCase().contains(dnsHost)) return Boolean.valueOf(false);
        }
        return null;
    }
}
