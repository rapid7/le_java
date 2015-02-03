package com.logentries.log4j2;

/**
 * Factory Data.
 */
class FactoryData
{

    /** Destination Token. */
    private final String token;
    /** Account Key. */
    private final String key;
    /** Account Log Location. */
    private final String location;
    /** HttpPut flag. */
    private final boolean httpPut;
    /** SSL/TLS flag. */
    private final boolean ssl;
    /** Debug flag. */
    private final boolean debug;
    /** UseDataHub flag. */
    private final boolean useDataHub;
    /** DataHubAddr - address of the server where DataHub instance resides. */
    private final String dataHubAddr;
    /** DataHubPort - port on which DataHub instance waits for messages. */
    private final int dataHubPort;
    /** LogHostName - switch that determines whether HostName should be appended to the log message. */
    private final boolean logHostName;
    /** HostName - value, that should be appended to the log message if logHostName is set to true. */
    private final String hostName;
    /** LogID - user-defined ID string that is appended to the log message if non-empty. */
    private final String logID;

    FactoryData(String token, String key, String location, boolean httpPut, boolean ssl, boolean debug,
                boolean useDataHub, String dataHubAddr, int dataHubPort, boolean logHostName,
                String hostName, String logID)
    {
        this.token = token;
        this.key = key;
        this.location = location;
        this.httpPut = httpPut;
        this.ssl = ssl;
        this.debug = debug;
        this.useDataHub = useDataHub;
        this.dataHubAddr = dataHubAddr;
        this.dataHubPort = dataHubPort;
        this.logHostName = logHostName;
        this.hostName = hostName;
        this.logID = logID;
    }

    public String getToken()
    {
        return token;
    }

    public String getKey()
    {
        return key;
    }

    public String getLocation()
    {
        return location;
    }

    public boolean isHttpPut()
    {
        return httpPut;
    }

    public boolean isSsl()
    {
        return ssl;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public boolean isUseDataHub()
    {
        return useDataHub;
    }

    public String getDataHubAddr()
    {
        return dataHubAddr;
    }

    public int getDataHubPort()
    {
        return dataHubPort;
    }

    public boolean isLogHostName()
    {
        return logHostName;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getLogID()
    {
        return logID;
    }
}
