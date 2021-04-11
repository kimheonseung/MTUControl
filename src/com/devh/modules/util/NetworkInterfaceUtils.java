package com.devh.modules.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * <pre>
 * Description :
 *     네트워크 인터페이스 관련 유틸 클래스
 * ==============================
 * Memberfields :
 *
 * ==============================
 *
 * Author : HeonSeung Kim
 * Date   : 2021. 3. 29.
 * </pre>
 */
public class NetworkInterfaceUtils {
    /* Singleton */
    private static NetworkInterfaceUtils instance;
    public static NetworkInterfaceUtils getInstance() {
        if(instance == null)
            instance = new NetworkInterfaceUtils();
        return instance;
    }

    /**
     * <pre>
     * Description :
     *     네트워크 인터페이스 목록 출력
     * ==============================
     * Parameters :
     *
     * Returns :
     *
     * Throws :
     *
     * ==============================
     *
     * Author : HeonSeung Kim
     * Date   : 2021. 3. 29.
     * </pre>
     */
    public void printNetworkInterfaces() {
        try {
            System.out.println("Printing all network interfaces... This may take a few seconds.");
            Enumeration<NetworkInterface> niEnums = NetworkInterface.getNetworkInterfaces();

            StringBuffer sbResult = new StringBuffer(System.lineSeparator());

            while(niEnums.hasMoreElements()) {
                NetworkInterface ni = niEnums.nextElement();
                Enumeration<InetAddress> iaEnums = ni.getInetAddresses();

                boolean isInetAddressExists = iaEnums.hasMoreElements();

                if(!isInetAddressExists)
                    continue;

                sbResult
                        .append(String.format("%-15s : %s", "Interface Name", ni.getName())).append(System.lineSeparator())
                        .append(String.format("%-15s : %s", "Display Name", ni.getDisplayName())).append(System.lineSeparator());

                while(iaEnums.hasMoreElements()) {
                    InetAddress inetAddress = iaEnums.nextElement();

                    sbResult
                            .append(String.format("%-15s : %s", "Host Name", inetAddress.getHostName())).append(System.lineSeparator())
                            .append(String.format("%-15s : %s", "Host Address", inetAddress.getHostAddress())).append(System.lineSeparator());
                }

                sbResult
                        .append("====================================================================").append(System.lineSeparator());

            }

            System.out.println(sbResult.toString());

        } catch (SocketException e) {
            System.out.println("Exception caught while getting network interfaces. " + e.getMessage());
        }

    }

    /**
     * <pre>
     * Description :
     *     인터페이스명에 해당하는 IPv4 주소 형식 반환
     * ==============================
     * Parameters :
     *     String interfaceName
     * Returns :
     *     String (IPv4 Host Address)
     *     가져오지 못하는 경우 null 반환
     * Throws :
     *
     * ==============================
     *
     * Author : HeonSeung Kim
     * Date   : 2021. 3. 29.
     * </pre>
     */
    public String getIPv4HostAddressByInterfaceName(String interfaceName) {
        String hostAddress = null;

        try {
            NetworkInterface ni = NetworkInterface.getByName(interfaceName);
            Enumeration<InetAddress> iaEnums = ni.getInetAddresses();

            while(iaEnums.hasMoreElements()) {
                InetAddress inetAddress = iaEnums.nextElement();

                final boolean isTarget =
                        !inetAddress.isLoopbackAddress() &&
                                !inetAddress.isLinkLocalAddress() &&
                                inetAddress.isSiteLocalAddress();

                if(isTarget)
                    hostAddress = inetAddress.getHostAddress();
            }
        } catch (SocketException e) {
            System.out.println("Exception caught while getting network interfaces. " + e.getMessage());
        }

        return hostAddress;
    }

    /**
     * <pre>
     * Description :
     *     인터페이스명에 해당하는 MAC 주소 형식 반환
     * ==============================
     * Parameters :
     *     String interfaceName
     * Returns :
     *     String (-가 포함된 MAC 주소)
     * Throws :
     *
     * ==============================
     *
     * Author : HeonSeung Kim
     * Date   : 2021. 3. 29.
     * </pre>
     */
    public String getMacAddressByInterfaceName(String interfaceName) {
        String macAddress = "";

        try {
            NetworkInterface ni = NetworkInterface.getByName(interfaceName);
            byte[] macByteArray = ni.getHardwareAddress();
            int macByteArrayLength = macByteArray.length;
            for(int i = 0 ; i < macByteArrayLength ; ++i)
                macAddress += String.format("%02X%s", macByteArray[i], (i < macByteArrayLength - 1) ? "-" : "");

        } catch (SocketException e) {
            System.out.println("Exception caught while getting network interfaces. " + e.getMessage());
        }

        return macAddress;
    }


    public static void main(String[] args) {
        NetworkInterfaceUtils.getInstance().printNetworkInterfaces();

        System.out.println(NetworkInterfaceUtils.getInstance().getIPv4HostAddressByInterfaceName("eth1"));
        System.out.println(NetworkInterfaceUtils.getInstance().getMacAddressByInterfaceName("eth1"));

        /*
         * Printing all network interfaces... This may take a few seconds.
         *
         * Interface Name  : lo
         * Display Name    : Software Loopback Interface 1
         * Host Name       : 127.0.0.1
         * Host Address    : 127.0.0.1
         * Host Name       : 0:0:0:0:0:0:0:1
         * Host Address    : 0:0:0:0:0:0:0:1
         * ====================================================================
         * Interface Name  : eth4
         * Display Name    : Realtek PCIe GbE Family Controller
         * Host Name       : HEONSEUNG-1
         * Host Address    : 1.2.3.4
         * Host Name       : HEONSEUNG-1
         * Host Address    : ab12:0:0:0:c345:678:9d01:1234%eth4
         * ====================================================================
         *
         * 172.16.100.102
         * 1C-1B-0D-B4-B9-9D
         */

    }
}
