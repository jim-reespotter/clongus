package mash.pies.syncthing.engine.processors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;

public abstract class LogBase {
    
    public Logger getLogger() {
        return getLogger(getClass());
    }
    
    private static Level defaultLogLevel = Level.INFO;

    public String getLogLevel() {
        return getLogger().getLevel().toString();
    }

    public void setLogLevel(String level) {
        getLogger(this.getClass()).setLevel(getLogLevel(level));
    }

    public void trace(String msg) {getLogger().trace(msg);}
    public void debug(String msg) {getLogger().debug(msg);}
    public void info(String msg) {getLogger().info(msg);}
    public void warn(String msg) {getLogger().info(msg);}

    public static String getDefaultLogLevel() {return defaultLogLevel.toString();}
    public static void setDefaultLogLevel(String logLevel) {defaultLogLevel = getLogLevel(logLevel);}

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = (Logger)LoggerFactory.getLogger(clazz);
        logger.setLevel(defaultLogLevel);
        return logger;
    }

    private static Level getLogLevel(String logLevel) {
        Level l;

        switch (logLevel) {
            case "TRACE":
            case "ALL":
                l = Level.TRACE;
                break;
            case "DEBUG":
                l = Level.DEBUG;
                break;
            case "WARN":
                l = Level.WARN;
                break;
            case "ERROR":
                l = Level.ERROR;
                break;
            case "OFF":
                l = Level.OFF;
                break;
            case "INFO":
            default:
                l = Level.INFO;
        }
        return l;
    }
}
