package org.gldhn;

import org.gldhn.mcsleep.McServerConfig;
import org.gldhn.mcsleep.RunJarWithArgs;
import org.gldhn.mcsleep.config;
import org.gldhn.mcstart.DisguiseServer;

import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.lang.System.exit;
import static org.gldhn.mcsleep.RunJarWithArgs.PrInput;
import static org.gldhn.mcsleep.RunJarWithArgs.Rescheduler;
import static org.gldhn.mcsleep.config.getConfig;
import static org.gldhn.mcsleep.config.setCount;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        seedREADME();
        Thread input = new Thread(Main::writeStream);
        input.start();
        new config();
        new McServerConfig();
        while (true){
            if(!RunJarWithArgs.start() || !config.getSleep()){
                exit(1);
            }
            //删除已经存在的对象RunJarWithArgs
            RunJarWithArgs.stop();

            common.Logger("进入休眠模式",3);
            Thread.sleep(5000);
            try {
                DisguiseServer ds = new DisguiseServer();
                ds.startServer(3, 5);
                ds = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Thread.sleep(5000);

        }


//        exit(0);
    }
    public static void writeStream() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ("stop".equalsIgnoreCase(line)&&PrInput == null) {
                exit(0);
            }
            if ("sasreload".equalsIgnoreCase(line)) {
                getConfig();
                setCount(-1);
                Rescheduler();
                common.Logger("配置文件重载成功",3);
                continue;
            }
            if ("sassleep".equalsIgnoreCase(line)) {
                try {
                    String input = "stop"; // 要发送的内容
                    PrInput.write((input+System.lineSeparator()).getBytes());
                    PrInput.flush();
                    common.Logger("Sent command: " + input.trim(),0);
                    RunJarWithArgs.restart = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if ("sashelp".equalsIgnoreCase(line)) {
                common.Logger("sashelp: sasreload 重载配置文件\n" +
                        "sassleep: 进入睡眠模式\n" +
                        "sashelp: 显示帮助信息\n" +
                        "stop: 关闭"
                        ,3);
                continue;
            }
//            this.getOutputStream().flush();
            if(PrInput != null){
                try {
                    PrInput.write((line+System.lineSeparator()).getBytes());
                    PrInput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        scanner.close();
        common.Logger("输入流关闭",0);
    }

    private static void seedREADME() {
        System.out.println("--------------mc服务器自动休眠程序McServerAutoSleep--------------------");
        System.out.println("        WaitingTime -> 单位分钟，每隔该时间后进行一次人数检测");
        System.out.println("       MaxZero -> 单位次，检测到多少次服务器人数为0后关闭服务器");
        System.out.println("   RunCommand -> 服务器运行指令，\\符号需要使用\\\\，如：C\\:\\\\Program");
        System.out.println("                NoOneClose -> 是否启用无人自动关闭");
        System.out.println("          Sleep -> 是否启用自动关闭后休眠（必须启用NoOneClose）");
        System.out.println("              Loglevel -> 日志等级，能正常运行的话设置2比较好");
        System.out.println("      第一次运行会生成配置文件config.properties，请修改配置文件后再运行");
        System.out.println("                    输入sashelp查看可用命令");
        System.out.println("        作者：乖漏斗 目前属于刚刚完成基本功能，遇到问题请及时反馈");
        System.out.println("---------------------------------------------------------------------");

    }
}