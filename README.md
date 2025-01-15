## 介绍
MC服务端无人自动休眠核心，用于在指定时间次数检测服务器人数为0后自动关闭服务器，并可进入休眠模式，在玩家尝试连接服务器后自动唤醒服务器。

## 使用方法
将文件放入MC服务器根目录，修改启动命令为：
```
java -jar MCSAS-1.2-SNAPSHOT.jar
```
启动后，会在服务器根目录生成一个config.properties文件，修改里面的参数，再启动服务器即可。

## 参数说明
| 参数          | 类型      | 默认值                    | 说明                                     |
|-------------|---------|------------------------|----------------------------------------|
| WaitingTime | Int     | 60                     | 单位分钟，每隔该时间后进行一次人数检测                    |
| MaxZero     | Int     | 3                      | 单位次，检测到多少次服务器人数为0后关闭服务器                |
| RunCommand  | String  | `java -jar server.jar` | 服务器运行指令，`\ `符号需要使用`\\`，如：`C:\\Program` |
| NoOneClose  | Boolean | true                   | 是否启用无人自动关闭                             |
| Sleep       | Boolean | true                   | 是否启用自动关闭后休眠（必须启用NoOneClose）            |
| Loglevel    | Int     | 2                      | 日志等级，能正常运行的话设置2比较好                     |

第一次运行会生成配置文件config.properties，请修改配置文件后再运行。

## 命令
命令仅可在控制台使用，请勿在游戏内使用。
| 命令 | 说明 |
| --- | --- |
|sashelp| sasreload 重载配置文件|
|sassleep| 进入睡眠模式(Sleep为false时为关闭服务器)|
|sashelp| 显示帮助信息|
|stop| 关闭|

## 构建
使用JDK17
```
mvn clean package -f pom.xml
```