package com.ke.bella.openapi.config;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络工具类 — 替代 spring-cloud-commons 的 InetUtils，减少外部依赖
 */
public final class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * 获取本机第一个非回环 IPv4 地址，异常时 fallback 到 127.0.0.1
     */
    public static String getFirstNonLoopbackIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("NetworkUtils.getFirstNonLoopbackIp failed: " + e.getMessage());
        }
        return "127.0.0.1";
    }
}
