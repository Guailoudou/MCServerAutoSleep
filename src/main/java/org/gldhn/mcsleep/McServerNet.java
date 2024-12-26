package org.gldhn.mcsleep;
import org.gldhn.mcping.*;

import java.io.IOException;

public class McServerNet {
    //返回在线人数，不在线或错误返回-1
    public static int GetServerInfo(String ip, int port)
    {
        MinecraftPingReply data;
        try {
            data = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(ip).setPort(port));
        } catch (IOException e) {
//            throw new RuntimeException(e);
            return -1;
        }

//        System.out.println(data.getDescription().getText() + "  --  " + data.getPlayers().getOnline() + "/" + data.getPlayers().getMax());
        return data.getPlayers().getOnline();
    }
}
