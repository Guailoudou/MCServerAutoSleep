package org.gldhn.mcsleep;

import org.gldhn.Main;
import org.gldhn.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gldhn.Main.main;
import static org.gldhn.Main.writeStream;
import static org.gldhn.mcsleep.McServerConfig.port;
import static org.gldhn.mcsleep.McServerNet.GetServerInfo;
import static org.gldhn.mcsleep.config.getWaitingTime;

public class RunJarWithArgs {

    RunJarWithArgs() {
    }
    static boolean runJar = false;
    public static OutputStream PrInput=null;
    public static boolean restart = false;
    private static Process process;

    private static ScheduledExecutorService scheduler;
    public static boolean start() {
        runJar = true;
        restart = false;
        // 包含所有启动参数的字符串
        String commandString = config.getRunCommand();

        // 将命令字符串分割成列表
        List<String> commandList = parseCommand(commandString);;

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
//        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        try {
            // 启动进程
            process = processBuilder.start();
            PrInput = process.getOutputStream();
            // 处理子进程的标准输出
            Thread outputThread = new Thread(() -> readStream(process.getInputStream(), "[SERVER]"));
            outputThread.start();

            // 写入到子进程的标准输入
//            Thread inputThread = new Thread(() -> writeStream(process.getOutputStream()));
//            inputThread.start();


            // 处理子进程的标准错误
            Thread errorThread = new Thread(() -> readStream(process.getErrorStream(), "[ERROR]"));
            errorThread.start();

            // 定时任务线程池
            scheduler = Executors.newScheduledThreadPool(1);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (process.isAlive()) {
                    process.destroy();
                    try {
                        if (!process.waitFor(5, TimeUnit.SECONDS)) {
                            process.destroyForcibly();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }));

            // 定期向子进程发送输入
            Runnable task = () -> sendInputToProcess(process.getOutputStream());
            scheduler.scheduleAtFixedRate(task, 0, getWaitingTime()*60, TimeUnit.SECONDS);

            // 等待进程完成
            int exitCode = process.waitFor();
            common.Logger("子进程退出，退出码：" + exitCode,0);
            runJar = false;
            PrInput = null;
            // 关闭调度器
            scheduler.shutdown();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return restart;
    }
    public static void Rescheduler(){
        if(runJar){
            scheduler.shutdown();
            scheduler = Executors.newScheduledThreadPool(1);
            Runnable task = () -> sendInputToProcess(process.getOutputStream());
            scheduler.scheduleAtFixedRate(task, 0, getWaitingTime()*60, TimeUnit.SECONDS);
        }
    }
    public static void stop() {
        try {
            if (process != null && process.isAlive())
                process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        runJar = false;
        common.Logger("停止子进程",0);
    }
    private static void readStream(InputStream inputStream, String streamType) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null && runJar) {
                common.Logger(streamType + ": " + line,3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendInputToProcess(OutputStream outputStream) {
        int onlinePlayer = GetServerInfo("127.0.0.1", port);
        if(onlinePlayer==0){
            config.setCount(config.getCount()+1);
            common.Logger("onlinePlayer==0 -> count:"+ config.getCount(),3);
        }else{
            config.setCount(0);
        }
        if(config.getCount()< config.getMaxZero()) {
            return;
        }
        try {
            String input = "stop"; // 要发送的内容
            outputStream.write((input+System.lineSeparator()).getBytes());
            outputStream.flush();
            common.Logger("Sent command: " + input.trim(),3);
            restart = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<String> parseCommand(String commandString) {
        List<String> result = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|\\S+").matcher(commandString);
        while (matcher.find()) {
            // 如果是引号内的匹配，则取第一组，否则整个匹配即为所需的项
            String match = matcher.group(1) != null ? matcher.group(1) : matcher.group(0);
            result.add(match);
        }
        return result;
    }
}



