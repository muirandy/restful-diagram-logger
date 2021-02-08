package com.github.muirandy.docs.living.log.restful;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.muirandy.docs.living.api.DiagramLogger;
import com.github.muirandy.docs.living.api.Log;
import com.github.muirandy.docs.living.api.Logs;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class RestfulDiagramLogger implements DiagramLogger {
    private LoggerClient proxy;

    public RestfulDiagramLogger(String restfulLoggerHost, Integer restfulLoggerPort) {
        String path = "http://" + restfulLoggerHost + ":" + restfulLoggerPort;
        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        client.register(JacksonJaxbJsonProvider.class);
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(path));
        proxy = target.proxy(LoggerClient.class);
    }

    @Override
    public void log(Log log) {
        Response loggerResponse = proxy.log(log);
        loggerResponse.close();
    }

    @Override
    public Logs read() {
        return proxy.readAll();
    }

    @Override
    public void markEnd(String logId) {
        proxy.markEnd(logId);
    }

    @Override
    public Logs read(String logId) {
        return proxy.read(logId);
    }
}