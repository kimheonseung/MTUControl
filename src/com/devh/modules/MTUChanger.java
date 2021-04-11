package com.devh.modules;

import com.devh.modules.util.CommandExecutor;
import com.devh.modules.util.NetworkInterfaceUtils;
import com.devh.modules.util.OSHelper;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * <pre>
 * Description :
 *     MTU값을 조절하는 클래스
 * ===============================================
 * Member fields :
 *
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2021-02-09
 * </pre>
 */
public class MTUChanger {
    /* 초기 MTU값 */
    private int mtu = 1420;
    private String ip;
    private String MTU_CHANGE_COMMAND = null;

    public MTUChanger() {
        this.ip = NetworkInterfaceUtils.getInstance().getIPv4HostAddressByInterfaceName("eth1");
        this.MTU_CHANGE_COMMAND = getMTUChangeCommand(OSHelper.getInstance().isWindows());
    }

    /* Singleton */
    private static MTUChanger instance;
    public static MTUChanger getInstance() {
        if(instance == null)
            instance = new MTUChanger();
        return instance;
    }

    public void changeMTU() {

        if(MTU_CHANGE_COMMAND == null)
            MTU_CHANGE_COMMAND = getMTUChangeCommand(OSHelper.getInstance().isWindows());

        updateMTU();

        final String finalMTUChangeCommand = MTU_CHANGE_COMMAND + this.mtu;

        try (
                CommandExecutor mtuChangeCommandExecutor = new CommandExecutor(finalMTUChangeCommand);
                BufferedReader mtuChangeBufferedReader = mtuChangeCommandExecutor.getInputStream();
                BufferedReader mtuChangeBufferedErrorReader = mtuChangeCommandExecutor.getErrorStream()
        ) {

            System.out.println(finalMTUChangeCommand);

            String line;
            StringBuilder stringBuffer = new StringBuilder();
            while ( (line = mtuChangeBufferedReader.readLine()) != null ) {
                if(line.trim().length() == 0)
                    continue;
                stringBuffer.append(System.lineSeparator()).append(line);
            }
            if(!"".equals(stringBuffer.toString().trim()))
                System.out.println(stringBuffer.toString().trim());

            checkError(mtuChangeBufferedErrorReader);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * <pre>
     * Description
     *     MTU 변경 명령어를 반환하는 메소드.
     *     1차적으로 network interface를 구분할 수 있는 고유 인터페이스명을 조회하고,
     *     해당 인터페이스의 MTU를 변경하는 운영체제에 맞는 명령어 반환
     *
     *     Windows
     *      인터페이스 2: 이더넷 4
     *
     *      주소 유형  DAD 상태   유효한 수명 기본 설정 수명 주소
     *      ---------  ----------- ---------- ---------- ------------------------
     *       DHCP       기본 설정           56m14s     56m14s 192.168.12.4
     *
     *      Linux
     *       wlp1s0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
     *          inet 192.168.12.4  netmask 255.255.255.0  broadcast 192.168.1.255
     *          inet6 ae30::zg1b:zk6:u2k3:n102  prefixlen 64  scopeid 0x20 < link >
     *          ether c1:3h:21:24:a9:11  txqueuelen 1000  (Ethernet)
     *          RX packets 35642094  bytes 48516044562 (48.5 GB)
     *          RX errors 0  dropped 0  overruns 0  frame 0
     *          TX packets 26410172  bytes 20386893285 (20.3 GB)
     *          TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
     * ===============================================
     * Parameters
     *
     * Returns
     *
     * Throws
     *
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-02-11
     * </pre>
     */
    private String getMTUChangeCommand(boolean isWindows) {

        String result = null;

        String networkInterfaceCommand = isWindows
                ? "netsh interface ipv4 show ipaddresses"
                : "ifconfig";

        try (
                CommandExecutor networkInterfaceCommandExecutor = new CommandExecutor(networkInterfaceCommand);
                BufferedReader networkInterfaceBufferedReader = networkInterfaceCommandExecutor.getInputStream();
                BufferedReader networkInterfaceBufferedErrorReader = networkInterfaceCommandExecutor.getErrorStream()
        ) {

            System.out.println(networkInterfaceCommand);
            String networkInterfaceId = null;
            String line;

            boolean isFind = false;

            while ( (line = networkInterfaceBufferedReader.readLine()) != null ) {
                if("".equals(line))
                    continue;
                /* for Windwos */
                String INTERFACE_KOR = "인터페이스";
                if(line.startsWith(INTERFACE_KOR))
                    networkInterfaceId = line.split(" ")[1].replace(",", "");
                /* for Linux */
                String MTU = "mtu";
                if(line.contains(MTU) && line.contains(","))
                    networkInterfaceId = line.split(",")[0];
                if(line.contains(this.ip)) {
                    isFind = true;
                    break;
                }
            }

            checkError(networkInterfaceBufferedErrorReader);

            if(isFind && networkInterfaceId != null)
                result = isWindows
                        ? "netsh interface ipv4 set subinterface \"" + networkInterfaceId + "\" mtu="
                        : "ifconfig " + networkInterfaceId + " mtu ";
            else
                System.out.println("Network Interface Not Found.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * <pre>
     * Description
     *     MTU 값을 가장 최저로 변경
     * ===============================================
     * Parameters
     *
     * Returns
     *
     * Throws
     *
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-02-11
     * </pre>
     */
    public void resetMTU() {
        if(OSHelper.getInstance().isWindows())
            this.mtu = 576;
        else
            this.mtu = 220;

        System.out.println("resetMTU : " + this.mtu);
    }

    /**
     * <pre>
     * Description
     *     MTU 값을 10 증가. 1500을 넘으면 최저로 변경
     * ===============================================
     * Parameters
     *
     * Returns
     *
     * Throws
     *
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-02-11
     * </pre>
     */
    public void updateMTU() {
        if((this.mtu += 10) >= 1500)
            resetMTU();
        System.out.println("updateMTU " + this.mtu);
    }

    public int getMTU() {
        return this.mtu;
    }

    private void checkError(BufferedReader bufferedReader) throws IOException {
        String errorLine;
        StringBuilder errorStringBuffer = new StringBuilder();
        while ( (errorLine = bufferedReader.readLine()) != null ) {
            if(errorLine.trim().length() == 0)
                continue;
            errorStringBuffer.append(System.lineSeparator()).append(errorLine);
        }
        if(!"".equals(errorStringBuffer.toString().trim()))
            System.out.println(errorStringBuffer.toString().trim());
    }
}
