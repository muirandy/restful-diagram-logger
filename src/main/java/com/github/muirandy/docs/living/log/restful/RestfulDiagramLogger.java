package com.github.muirandy.docs.living.log.restful;

import com.github.muirandy.docs.living.api.DiagramLogger;
import com.github.muirandy.docs.living.api.Log;
import com.github.muirandy.docs.living.api.Logs;

import static groovy.json.JsonOutput.toJson;
import static io.restassured.RestAssured.given;

public class RestfulDiagramLogger implements DiagramLogger {
    private String restfulLoggerHost;
    private Integer restfulLoggerPort;

    public RestfulDiagramLogger(String restfulLoggerHost, Integer restfulLoggerPort) {
        this.restfulLoggerHost = restfulLoggerHost;
        this.restfulLoggerPort = restfulLoggerPort;
    }

    @Override
    public void log(Log log) {
        given()
                .baseUri("http://" + restfulLoggerHost)
                .port(restfulLoggerPort)
                .header("Content-Type", "application/json")
                .when()
                .body(toJson(log))
                .post("/log");
    }

    @Override
    public Logs read() {
        return given()
                .baseUri("http://" + restfulLoggerHost)
                .port(restfulLoggerPort)
                .when()
                .get("/log/read")
                .getBody().as(Logs.class);
    }

    @Override
    public void markEnd(String logId) {
        given()
                .baseUri("http://" + restfulLoggerHost)
                .port(restfulLoggerPort)
                .when().post("/log/markEnd/" + logId);
    }

    @Override
    public Logs read(String logId) {
        return given()
                .baseUri("http://" + restfulLoggerHost)
                .port(restfulLoggerPort)
                .when()
                .get("/log/read/" + logId)
                .getBody().as(Logs.class);
    }
}
