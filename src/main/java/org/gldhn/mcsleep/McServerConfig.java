package org.gldhn.mcsleep;

import org.gldhn.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class McServerConfig {
    public String ip = "localhost";
    public static int port;
    public McServerConfig(){
        try{
            GetConfig();
        }catch(Exception e){
            common.Logger("读取配置文件失败" + e,2);
        }

    }

    public void GetConfig(){
        Properties prop = new Properties();
        FileInputStream input = null;

        String propFileName = "server.properties";
        try {
            input = new FileInputStream(propFileName);
            prop.load(input);
            port = Integer.parseInt(prop.getProperty("server-port"));
        } catch (Exception e) {
            common.Logger("Could not find " + propFileName,2);
        }finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    common.Logger("关闭文件读取错误 " + e.getMessage(),2);
                }
            }

        }

    }
    public String getIp(){
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
