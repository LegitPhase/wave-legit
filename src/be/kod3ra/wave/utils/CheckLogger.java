/*
 * Decompiled with CFR 0.152.
 */
package be.kod3ra.wave.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CheckLogger {
    private static final Logger logger = Logger.getLogger("CheckLogger");
    private static FileHandler fileHandler;

    static {
        try {
            File logsFolder = new File("plugins/Wave/logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }
            String logFileName = CheckLogger.generateLogFileName();
            fileHandler = new FileHandler("plugins/Wave/logs/" + logFileName, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String playerName, String checkName, String details) {
        logger.log(Level.WARNING, String.format("[%s] Check '%s' triggered for player '%s' - %s", CheckLogger.getCurrentTimestamp(), checkName, playerName, details));
    }

    private static String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private static String generateLogFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH'h'-mm'm'");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}

