package org.gldhn.mcstart;

import org.gldhn.common;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.*;
import static org.gldhn.mcsleep.McServerConfig.port;
import static org.gldhn.mcstart.DisguiseServerHandler.readVarInt;

public class DisguiseServer {
    private static final Logger logger = Logger.getLogger(DisguiseServer.class.getName());
    private InetAddress fs_ip = InetAddress.getByName("0.0.0.0");
    private int fs_port = 25565;
    private boolean close_request = false;
    private String result = "";
    private ServerSocket serverSocket;

    public DisguiseServer() throws UnknownHostException {
        fs_port = port;
    }

    // Constructor and other methods...

    public void startServer(int maxRetries, int retryDelay) throws IOException {
        int retryCount = 0;
        while (retryCount < maxRetries && !close_request) {
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(fs_ip, fs_port), 5);
                //serverSocket.setSoTimeout(); // 10 seconds timeout

                common.Logger("服务器正在监听 " + fs_ip.getHostAddress() + ":" + fs_port,3);
                break; // 如果成功启动，跳出循环
            } catch (IOException e) {
                common.Logger("服务器启动失败: " + e.getMessage() + "，重试中...",1);
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
                retryCount++;
                try {
                    Thread.sleep(retryDelay * 1000); // 等待时间转换为毫秒
                    retryDelay *= 2; // 指数退避算法
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Thread was interrupted during sleep", ie);
                }
            }
        }

        if (!close_request) {
            listenForConnections();
        }
    }

    private void listenForConnections() {
        while (!"connection_request".equals(result) && !close_request) {
            try {
                Socket clientSocket = serverSocket.accept();
                common.Logger("收到来自" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "的连接",0);
                InputStream input = clientSocket.getInputStream();
//                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                // 创建一个缓冲区来存放接收到的数据
                byte[] buffer = new byte[1024];

                // 读取数据到缓冲区
                int bytesRead = input.read(buffer, 0, buffer.length);
                byte[] recvData = new byte[0];
                if (bytesRead > 0) {
                    recvData = new byte[bytesRead];
                    System.arraycopy(buffer, 0, recvData, 0, bytesRead);
                } else {
                    common.Logger("No more data or connection closed.",1);
                }
                DisguiseServerHandler.ResultWithIndex resultWithIndex = readVarInt(recvData, 0);
                DisguiseServerHandler.ResultWithIndex resultWithIndex2 = readVarInt(recvData, resultWithIndex.newIndex);
                int packetID = resultWithIndex2.result;
                common.Logger("收到来自" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "的数据包，packetID: " + resultWithIndex.result + ", length: " + resultWithIndex2.result,0);
                if(packetID==0){
                    result = DisguiseServerHandler.handlePing(clientSocket, recvData, resultWithIndex2.newIndex);
                    if (result.equals("connection_request")) {
                        close_request = true;
                        serverSocket.close();
                    }
                } else if (packetID==1) {
                    serverSocket.close();
                    break;
                }

                //                if ("some condition for packetID == 0".equals(recvData)) {
//                    result = handlePing(clientSocket, recvData);
//                } else if ("some condition for packetID == 1".equals(recvData)) {
//                    handlePong(clientSocket, recvData);
//                } else {
//                    logger.warning("收到了意外的数据包");
//                }
            } catch (SocketTimeoutException ste) {
                common.Logger("连接超时",1);
//                closeServerSocket();
//                return;
            } catch (IOException e) {
                common.Logger("发生错误: " + e.getMessage(),1);
//                closeServerSocket();
//                return;
            } catch (Exception e) {
                common.Logger("发生未知错误: " + e.getMessage() + e.getLocalizedMessage(),1);
//                closeServerSocket();
//                return;
            }
        }
    }

    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.severe("关闭服务器套接字时出错: " + e.getMessage());
            }
        }
    }

    // Implement handlePing, handlePong, and read_varint methods as needed...

}

