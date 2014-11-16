package com.logentries.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

/**
 * Log4J2 Appender that writes to Logentries.
 * <p/>
 * Created by josh on 11/15/14.
 */
@Plugin(name = "Logentries", category = "Core", elementType = "appender", printObject = true)
public final class LogentriesAppender extends AbstractAppender
{
    private final LogentriesManager manager;

    protected LogentriesAppender(String name, Filter filter,
                                 Layout<? extends Serializable> layout, boolean ignoreExceptions,
                                 LogentriesManager manager)
    {
        super(name, filter, layout, ignoreExceptions);
        this.manager = manager;
    }

    @PluginFactory
    public static LogentriesAppender createAppender(@PluginAttribute("name") String name,
                                                    @PluginAttribute("token") String token,
                                                    @PluginAttribute("key") String key,
                                                    @PluginAttribute("location") String location,
                                                    @PluginAttribute("httpPut") boolean httpPut,
                                                    @PluginAttribute("ssl") boolean ssl,
                                                    @PluginAttribute("debug") boolean debug,
                                                    @PluginAttribute("useDataHub") boolean useDataHub,
                                                    @PluginAttribute("dataHubAddr") String dataHubAddr,
                                                    @PluginAttribute("dataHubPort") int dataHubPort,
                                                    @PluginAttribute("logHostName") boolean logHostName,
                                                    @PluginAttribute("hostName") String hostName,
                                                    @PluginAttribute("logID") String logID,
                                                    @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                                    @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                    @PluginElement("Filters") Filter filter)
    {

        if (name == null)
        {
            LOGGER.error("No name provided for LogentriesAppender");
            return null;
        }
        if (token == null)
        {
            LOGGER.error("No token provided for LogentriesAppender");
            return null;
        }
        FactoryData data = new FactoryData(token,
                key, location, httpPut, ssl, debug,
                useDataHub, dataHubAddr,
                dataHubPort, logHostName, hostName,
                logID);
        LogentriesManager manager = LogentriesManager.getManager(name, data);
        if (manager == null)
            return null;

        if (layout == null)
            layout = PatternLayout.createDefaultLayout();

        return new LogentriesAppender(name, filter, layout, ignoreExceptions, manager);
    }

    @Override
    public void append(LogEvent event)
    {
        final Layout<? extends Serializable> layout = getLayout();
        String line = new String(layout.toByteArray(event));
        manager.writeLine(line);
    }
}