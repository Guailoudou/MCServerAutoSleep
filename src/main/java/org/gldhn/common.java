package org.gldhn;

import org.gldhn.mcsleep.config;

public class common {
    public static void Logger(String text,Integer level)
    {
        if (config.LogLevel > level) {
            return;
        }
        switch (level) {
            case 1 -> System.out.println("[WARN] " + text);
            case 2 -> System.out.println("[ERROR] " + text);
            case 3 -> System.out.println("[IMP-INFO] " + text);
            default -> System.out.println("[INFO] " + text);
        }
    }
}
