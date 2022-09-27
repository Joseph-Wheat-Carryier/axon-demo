package com.axion.demo.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantLock;

public class SonyFlakeUtil {
    private static final long BIT_LEN_TIME = 39;
    private static final long BIT_LEN_SEQUENCE = 8;
    private static final long BIt_LEN_MACHINE_ID = 63 - BIT_LEN_TIME - BIT_LEN_SEQUENCE;

    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    // 产生下一个ID
    public static String getNextId() {
        try {
            long id = SonyFlake.getInstance().nextId();
            return toBase58(id);
        } catch (InterruptedException ex) {
            return "";
        }
    }

    public static Long getNextLong() {
        try {
            Long id = SonyFlake.getInstance().nextId();
            return id;
        } catch (InterruptedException ex) {
            return 0L;
        }
    }

    private static String toBase58(long id) {
        StringBuilder sb = new StringBuilder();
        try {
            while (id != 0) {
                sb.insert(0, ALPHABET[(int) (id % 58)]);
                id = id / 58;
            }
            return sb.toString();
        } catch (Exception ex) {

        }
        return "";
    }

    private static class SonyFlake {
        long startTime;
        long elapsedTime;
        long sequence;
        long machineId;

        ReentrantLock lock;

        private static SonyFlake singleton;

        private SonyFlake() {
            this.sequence = (1 << BIT_LEN_SEQUENCE) - 1;
            this.startTime = getStartSnowFlakeTime();
            this.machineId = lower16BitPrivateIp();
        }

        @SuppressWarnings("all")
        public static SonyFlake getInstance() {
            if (singleton == null) {
                synchronized (SonyFlake.class) {
                    if (singleton == null) {
                        singleton = new SonyFlake();
                    }
                }
            }
            return singleton;
        }

        public synchronized long nextId() throws InterruptedException {
            long maskSequence = (1L << BIT_LEN_SEQUENCE) - 1;
            long current = currentElapsedTime();

            if (this.elapsedTime < current) {
                this.elapsedTime = current;
                this.sequence = 0;
            } else {
                this.sequence = (this.sequence + 1) & maskSequence;
                if (this.sequence == 0) {
                    this.elapsedTime++;
                    long overtime = this.elapsedTime - current;
                    Thread.sleep(overtime);
                }
            }

            return toID();
        }

        private long toID() {
            if (this.elapsedTime >= (1L << BIT_LEN_TIME)) {
                throw new IllegalArgumentException("too long time");
            }

            return (this.elapsedTime << (BIT_LEN_SEQUENCE + BIt_LEN_MACHINE_ID)) | (this.sequence << BIt_LEN_MACHINE_ID) | (this.machineId);
        }

        private long currentElapsedTime() {
            long current = System.currentTimeMillis();
            return current - this.startTime;
        }

        private long getStartSnowFlakeTime() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date startTime = null;
            try {
                startTime = dateFormat.parse("2014-09-01 00:00:00");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return startTime.getTime();
        }

        private long lower16BitPrivateIp() {
            String localIp = null;// 本地IP，如果没有配置外网IP则返回它
            String netIp = null;// 外网IP
            Enumeration<NetworkInterface> netInterfaces;
            try {
                netInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            InetAddress ip = null;
            boolean found = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !found) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {// 外网IP
                        netIp = ip.getHostAddress();
                        found = true;
                        break;
                    } else if (ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {// 内网IP
                        localIp = ip.getHostAddress();
                    }
                }
            }
            if (netIp != null && !"".equals(netIp)) {
                return Long.parseLong(netIp.split("\\.")[3]);
            } else {
                assert localIp != null;
                return Long.parseLong(localIp.split("\\.")[3]);
            }
        }
    }
}