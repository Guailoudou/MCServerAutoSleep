package org.gldhn.mcsleep;
import org.gldhn.common;

import java.io.*;
import java.util.Properties;
public class config {
    private static Properties properties;
    public config(){
        properties = new Properties();
        getConfig();
    }
    public static void getConfig(){
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
                properties.setProperty("MOTD","§a服务器正在休眠中...§r\n§e点击进入以启动服务器§r");
                properties.setProperty("StartMessage","§a服务器正在启动中，请稍候...§r");
                properties.store(new java.io.FileOutputStream("config.properties"),"MCSAS config");
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
            MOTD = properties.getProperty("MOTD", "§a服务器正在休眠中...§r\n§e点击进入以启动服务器§r");
            StartMessage = properties.getProperty("StartMessage", "§a服务器正在启动中，请稍候...§r");
            common.Logger("配置文件读取成功",3);
            common.Logger("RunCommand: " + RunCommand,3);
            common.Logger("WaitingTime: " + WaitingTime,3);
            common.Logger("MaxZero: " + MaxZero,3);
            common.Logger("NoOneClose: " + NoOneClose,3);
            common.Logger("Sleep: " + Sleep,3);
            common.Logger("LogLevel: " + LogLevel,3);
            common.Logger("MOTD: " + MOTD,3);
            common.Logger("StartMessage: " + StartMessage,3);
            saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void saveConfig() throws IOException {
        properties.setProperty("RunCommand",RunCommand);
        properties.setProperty("WaitingTime", String.valueOf(WaitingTime));
        properties.setProperty("MaxZero", String.valueOf(MaxZero));
        properties.setProperty("NoOneClose", String.valueOf(NoOneClose));
        properties.setProperty("Sleep", String.valueOf(Sleep));
        properties.setProperty("LogLevel", String.valueOf(LogLevel));
        properties.setProperty("MOTD",MOTD);
        properties.setProperty("StartMessage",StartMessage);
        properties.store(new java.io.FileOutputStream("config.properties"),null);
        common.Logger("配置文件保存成功",1);
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
    //服务器MOTD信息
    private static String MOTD;
    //服务器启动提示信息
    private static String StartMessage;

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

    public static String getMOTD() {
        return MOTD;
    }

    public static void setMOTD(String MOTD) {
        config.MOTD = MOTD;
    }

    public static String getStartMessage() {
        return StartMessage;
    }

    public static void setStartMessage(String startMessage) {
        StartMessage = startMessage;
    }
}
