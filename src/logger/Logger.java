package logger;

public class Logger
{
    private String className;

    public Logger(Class clazz)
    {
        this.className = clazz.getName();
    }

    public void info(String log)
    {
        log("INFO", log);
    }

    public void error(String log)
    {
        log("ERROR", log);
    }

    public void error(String log, Exception e)
    {
        error(log);
        e.printStackTrace();
    }

    private void log(String logType, String log)
    {
        System.out.printf("%s: %s -- %s\n", logType, this.className, log);
    }
}
