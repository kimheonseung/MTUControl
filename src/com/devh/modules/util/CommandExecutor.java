package com.devh.modules.util;

import java.io.*;
import java.util.ArrayList;

/**
 * <pre>
 * Description :
 *     명령어를 실행 객체
 * ===============================================
 * Member fields :
 *     Process mProcess
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2019-12-19
 * </pre>
 */
public class CommandExecutor implements Closeable {
    private Process mProcess;

    public CommandExecutor(String command)  {
        try {
            execute(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * Description
     *     명령어 실행 메소드
     * ===============================================
     * Parameters
     *     String command 실행할 명령어 문자열
     * Returns
     *
     * Throws
     *     지원하지 않는 운영체제인 경우 예외 발생 (현재 윈도우, 유닉스 계열만 테스트)
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2019-12-19
     * </pre>
     */
    private void execute(String command) throws Exception {
        ArrayList<String> commandList = new ArrayList<>();

        if(OSHelper.getInstance().isWindows()) {
            commandList.add("cmd");
            commandList.add("/c");
        } else if(OSHelper.getInstance().isUnix()) {
            commandList.add("/bin/sh");
            commandList.add("-c");
        } else {
            throw new RuntimeException("Unsupported operating system");
        }

        commandList.add(command);

        this.mProcess = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
    }

    /**
     * <pre>
     * Description
     *     명령어 모두 실행 후 하위 Process까지 종료
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
     * Date   : 2019-12-19
     * </pre>
     */
    public void destroy() {
        this.mProcess.destroy();
    }

    /**
     * <pre>
     * Description
     *     명령어 실행 후 관련 스트림 처리
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
     * Date   : 2019-12-19
     * </pre>
     */
    public void closeStream() {
        try {
            this.mProcess.getErrorStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.mProcess.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.mProcess.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * Description
     *     명령어 실행 결과 스트림
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
     * Date   : 2019-12-19
     * </pre>
     */
    public BufferedReader getInputStream() throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(this.mProcess.getInputStream(), "EUC-KR"));
    }

    /**
     * <pre>
     * Description
     *     명령어 실행 결과 에러 스트림
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
     * Date   : 2019-12-19
     * </pre>
     */
    public BufferedReader getErrorStream() throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(this.mProcess.getErrorStream(), "EUC-KR"));
    }

    @Override
    public void close() throws IOException {
        closeStream();
        destroy();
    }
}
