package org.gldhn.mcstart;



import com.google.gson.Gson;
import org.gldhn.common;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DisguiseServerHandler {

    private static final String fs_motd = null;
    private static final List<String> fs_samples = null;
    private static final String fs_kick_message = null;


//    public DisguiseServerHandler(String fs_motd, List<String> fs_samples, String fs_icon, String fs_kick_message) {
//        this.fs_motd = fs_motd;
//        this.fs_samples = fs_samples;
//        this.fs_icon = fs_icon;
//        this.fs_kick_message = fs_kick_message;
//    }

    public static String handlePing(Socket clientSocket, byte[] recvData, int startIndex) throws IOException, VarIntException {
        int i = startIndex;
        ResultWithIndex versionResult = readVarInt(recvData, i);
        int version = versionResult.result;
        i = versionResult.newIndex;

        String[] ipResult = readUTF(recvData, i);
        String ip = ipResult[0].replace('\0', ' ').replace("\r", "\\r").replace("\t", "\\t").replace("\n", "\\n");
        i = Integer.parseInt(ipResult[1]);

        boolean isUsingFML = ip.endsWith("FML");
        if (isUsingFML) {
            ip = ip.substring(0, ip.length() - 3);
        }

        ResultWithIndex portResult = readUShort(recvData, i);
        int port = portResult.result;
        i = portResult.newIndex;

        ResultWithIndex stateResult = readVarInt(recvData, i);
        int state = stateResult.result;
        i = stateResult.newIndex;

        if (state == 1) {
            common.Logger("伪装服务器收到了一次ping: " + Arrays.toString(recvData),0);

            writeResponse(clientSocket, "");
            return "ping_received";
        } else if (state == 2) {
            common.Logger("伪装服务器收到了一次连接请求: " + Arrays.toString(recvData),0);
            writeResponse(clientSocket, "");
//            stop(server);
            common.Logger("启动服务器",0);
            //server.start();
            return "connection_request";
        }
        return "";
    }

    public static class ResultWithIndex {
        public final int result;
        public final int newIndex;

        public ResultWithIndex(int result, int newIndex) {
            this.result = result;
            this.newIndex = newIndex;
        }
    }

    public static String[] readUTF(byte[] bytes, int i) throws VarIntException {
        // 调用 readVarInt 来获取长度和新的索引位置
        ResultWithIndex result = readVarInt(bytes, i);
        int length = result.result;
        i = result.newIndex;

        // 提取出UTF-8编码的字符串部分，并解码
        String ip = new String(bytes, i, length, StandardCharsets.UTF_8);
        i += length;

        // 返回解码后的字符串和更新后的索引
        return new String[]{ip, String.valueOf(i)};
    }

    private static ResultWithIndex readUShort(byte[] byteArray, int startIndex) throws IOException {
        if (startIndex + 2 > byteArray.length) {
            throw new IOException("读取无符号短整型时超出范围");
        }

        int result = ((byteArray[startIndex] & 0xFF) << 8) | (byteArray[startIndex + 1] & 0xFF);
        return new ResultWithIndex(result, startIndex + 2);
    }

    private static void writeResponse(Socket socket, String response) throws IOException {
        try (OutputStream out = socket.getOutputStream()) {
            byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
            writeVarInt(out, responseData.length);
            out.write(responseData);
            out.flush();
        }
    }

    private static void writeVarInt(OutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0L) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value & 0x7F);
    }

    public static ResultWithIndex readVarInt(byte[] byteArray, int startIndex) throws VarIntException {
        int result = 0;
        int currentIndex = startIndex;
        int byteCount = 0;

        while (true) {
            if (currentIndex >= byteArray.length) {
                throw new VarIntException("Index out of range");
            }

            byte byteIn = byteArray[currentIndex];
            currentIndex++;
            result |= (byteIn & 0x7F) << (byteCount * 7);
            byteCount++;

            if (byteCount > 5) { // According to Minecraft protocol, VarInts can be at most 5 bytes long
                throw new VarIntException("VarInt is too long!");
            }

            if ((byteIn & 0x80) == 0) {
                return new ResultWithIndex(result, currentIndex);
            }
        }
    }
    public static class VarIntException extends Exception {
        public VarIntException(String message) {
            super(message);
        }
    }

}
