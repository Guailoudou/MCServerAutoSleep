package org.gldhn.mcsleep;
import org.gldhn.common;

import java.io.FileInputStream;
import java.util.Properties;
public class config {
    public config(){
        getConfig();
    }
    public static void getConfig(){
        Properties properties = new Properties();
        //判断配置文件是否存在，不存在自动创建
        if(!new java.io.File("config.properties").exists()){
            try {
                new java.io.File("config.properties").createNewFile();
                //写入默认配置
                properties.setProperty("RunCommand","java -jar server.jar");
                properties.setProperty("WaitingTime","60");
                properties.setProperty("MaxZero","3");
                properties.setProperty("NoOneClose","true");
                properties.setProperty("Sleep","true");
                properties.setProperty("LogLevel","2");
                properties.store(new java.io.FileOutputStream("config.properties"),null);
                common.Logger("配置文件创建成功",1);
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //读取配置文件
        try {
            properties.load(new FileInputStream("config.properties"));
            RunCommand = properties.getProperty("RunCommand");
            WaitingTime = Integer.parseInt(properties.getProperty("WaitingTime"));
            MaxZero = Integer.parseInt(properties.getProperty("MaxZero"));
            LogLevel = Integer.parseInt(properties.getProperty("LogLevel"));
            NoOneClose = Boolean.parseBoolean(properties.getProperty("NoOneClose"));
            Sleep = Boolean.parseBoolean(properties.getProperty("Sleep"));
            common.Logger("配置文件读取成功",3);
            common.Logger("RunCommand: " + RunCommand,3);
            common.Logger("WaitingTime: " + WaitingTime,3);
            common.Logger("MaxZero: " + MaxZero,3);
            common.Logger("NoOneClose: " + NoOneClose,3);
            common.Logger("Sleep: " + Sleep,3);
            common.Logger("LogLevel: " + LogLevel,3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //运行命令
    private static String RunCommand;
    //等待时间
    private static Integer WaitingTime;
    //连续人数为0的最高次数
    private static Integer MaxZero;
    //是否启用无人自动关闭
    private static Boolean NoOneClose = true;
    //是否自动关闭后进休眠
    private static Boolean Sleep = true;
    //计数器
    private static Integer Count = 0;
    //日志等级
    public static Integer LogLevel = 0;

    public static Integer getLogLevel() {
        return LogLevel;
    }

    public static void setLogLevel(Integer logLevel) {
        LogLevel = logLevel;
    }

    public static String getRunCommand() {
        return RunCommand;
    }

    public static void setRunCommand(String runCommand) {
        RunCommand = runCommand;
    }

    public static Integer getWaitingTime() {
        return WaitingTime;
    }

    public static void setWaitingTime(Integer waitingTime) {
        WaitingTime = waitingTime;
    }

    public static Integer getMaxZero() {
        return MaxZero;
    }

    public static void setMaxZero(Integer maxZero) {
        MaxZero = maxZero;
    }

    public static Integer getCount() {
        return Count;
    }

    public static void setCount(Integer count) {
        Count = count;
    }

    public static Boolean getNoOneClose() {
        return NoOneClose;
    }

    public static void setNoOneClose(Boolean noOneClose) {
        NoOneClose = noOneClose;
    }

    public static Boolean getSleep() {
        return Sleep;
    }

    public static void setSleep(Boolean sleep) {
        Sleep = sleep;
    }
}
