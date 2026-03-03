package org.gldhn.mcstart;

import org.gldhn.common;
import org.gldhn.mcsleep.config;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DisguiseServerHandler {

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
        common.Logger("version =" + version, 0);
        if (state == 1) {
            // Status state - handle server list ping
            common.Logger("伪装服务器收到了一次ping: " + Arrays.toString(recvData), 0);
            common.Logger("pingInfo: " + version +":"+ip +":"+ port, 0);

            // 构建MOTD响应
            String motdResponse = "{" +
                "\"version\": {" +
                    "\"name\": \"sleep\","
                    + "\"protocol\": "+version
                + "},"
                + "\"players\": {"
                    + "\"max\": 0,"
                    + "\"online\": 0,"
                    + "\"sample\": []"
                + "},"
                + "\"description\": {"
                    + "\"text\": \"" + config.getMOTD().replace("\"", "\\\"") + "\""
                + "}"
            + "}";

            // 发送Status Response数据包
            sendStatusResponse(clientSocket, motdResponse);

            // 处理Ping Request数据包
            handlePing(clientSocket);

            return "ping_received";
        } else if (state == 2) {
            // Login state - handle connection request
            common.Logger("伪装服务器收到了一次连接请求: " + Arrays.toString(recvData), 0);
            // 构建启动提示响应
            String startResponse = "{\"text\": \"" + config.getStartMessage().replace("\"", "\\\"") + "\"}";

            // 发送Login Disconnect数据包
            sendLoginDisconnect(clientSocket, startResponse);

            return "connection_request";
        }
        return "";
    }

    private static void handlePing(Socket socket) throws IOException {
        try {
            InputStream in = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(in);
            OutputStream out = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            
            // 设置读取超时，避免一直阻塞
            socket.setSoTimeout(1000);
            
            // 尝试读取Ping Request数据包
            try {
                // 读取时间戳
                long timestamp = dataInputStream.readLong();

                // 发送Ping Response数据包
                sendPingResponse(dataOutputStream, timestamp);
                common.Logger("发送Ping Response数据包", 0);
            } catch (IOException e) {
                // 读取超时或其他IO错误，不处理，等待连接关闭
                common.Logger("读取Ping Request超时，等待连接关闭", 0);
            }
        } catch (Exception e) {
            common.Logger("处理Ping Request时出错: " + e.getMessage(), 0);
        }
    }

    private static void sendStatusResponse(Socket socket, String jsonResponse) throws IOException {
        OutputStream out = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        
        // 构建数据包
        byte[] jsonBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        
        // 计算长度
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(b);
        packet.writeByte(0x00); // 数据包ID
        writeVarInt(packet, jsonBytes.length); // JSON长度
        packet.write(jsonBytes); // JSON数据
        
        // 写入总长度
        writeVarInt(dataOutputStream, b.size());
        // 写入数据包
        dataOutputStream.write(b.toByteArray());
        dataOutputStream.flush();
    }

    private static void sendLoginDisconnect(Socket socket, String jsonMessage) throws IOException {
        OutputStream out = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        
        // 构建数据包
        byte[] jsonBytes = jsonMessage.getBytes(StandardCharsets.UTF_8);
        
        // 计算长度
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(b);
        packet.writeByte(0x00); // 数据包ID
        writeVarInt(packet, jsonBytes.length); // JSON长度
        packet.write(jsonBytes); // JSON数据
        
        // 写入总长度
        writeVarInt(dataOutputStream, b.size());
        // 写入数据包
        dataOutputStream.write(b.toByteArray());
        dataOutputStream.flush();
    }

    private static void sendPingResponse(DataOutputStream out, long timestamp) throws IOException {
        // 构建数据包
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(b);
        packet.writeByte(0x01); // 数据包ID
        packet.writeLong(timestamp); // 时间戳
        
        // 写入总长度
        writeVarInt(out, b.size());
        // 写入数据包
        out.write(b.toByteArray());
        out.flush();
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

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while (true) {
            if ((value & 0xFFFFFF80) == 0) {
                out.writeByte(value);
                return;
            }

            out.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }

    private static void writeVarInt(OutputStream out, int value) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        writeVarInt(dataOutputStream, value);
    }

    public static int readVarInt(DataInputStream in) throws IOException, VarIntException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new VarIntException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
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
