package com.github.muirandy.docs.living.log.restful;

import com.github.muirandy.docs.living.api.DiagramLogger;
import com.github.muirandy.docs.living.api.Log;
import com.github.muirandy.docs.living.api.Logs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class RestfulDiagramLoggerShould {

    private static final String PROTOCOL = "Protocol";
    private static final String SOURCE = "source";
    private static final String TARGET = "target";
    private static final String LOG_MESSAGE = "Message";
    private static final String SECOND_LOG_MESSAGE = "Second Message";
    private static final String BODY = "Body";
    private static final String SECOND_BODY = "Second Body";
    private static final String LICENSE_KEY = "CED3B2-D3E679-079630-636028-9E2E7F-V3";
    private static final String ENV_VAR_LICENSE = "LICENSE";

    private static final int RESTFUL_LOGGER_INTERNAL_PORT = 8080;

    @Container
    private final GenericContainer RESTFUL_LOGGER =
            new GenericContainer(DockerImageName.parse("ghcr.io/muirandy/simple-log:multi-5cae3a0fe2cbadb8df3ec00388636219e3516dc0"))
                    .withExposedPorts(8080)
                    .withEnv("license", license())
                    .withEnv("licenseKey", LICENSE_KEY)
                    .waitingFor(
                            Wait.forLogMessage(".*Listening on:.*", 1)
                    );

    private DiagramLogger diagramLogger;
    private Log log = new Log(PROTOCOL, SOURCE, TARGET, LOG_MESSAGE, BODY);
    private Log secondLog = new Log(PROTOCOL, TARGET, SOURCE, SECOND_LOG_MESSAGE, SECOND_BODY);
    private String restfulLoggerHost;
    private Integer restfulLoggerPort;
    private String logId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        diagramLogger = createRestfulLogger();
    }

    private RestfulDiagramLogger createRestfulLogger() {
        restfulLoggerHost = RESTFUL_LOGGER.getHost();
        restfulLoggerPort = RESTFUL_LOGGER.getMappedPort(RESTFUL_LOGGER_INTERNAL_PORT);
        return new RestfulDiagramLogger(restfulLoggerHost, restfulLoggerPort);
    }

    @Test
    void returnEmptyLogs() {
        Logs logs = diagramLogger.read();

        assertThat(logs.getLogs()).isEmpty();
    }

    @Test
    void retrieveLogs() {
        diagramLogger.log(log);

        Logs logs = diagramLogger.read();

        assertThat(logs.getLogs()).containsExactly(log);
    }

    @Test
    void shareLogsWithOtherProcesses() {
        diagramLogger.log(log);

        Logs logs = createRestfulLogger().read();

        assertThat(logs.getLogs()).containsExactly(log);
    }

    @Test
    void retrieveLogsWrittenByAnotherProcess() {
        createRestfulLogger().log(log);

        Logs logs = diagramLogger.read();

        assertThat(logs.getLogs()).containsExactly(log);
    }

    @Test
    void retrieveEmptyLogsForGivenId() {
        diagramLogger.markEnd(logId);

        Logs logs = diagramLogger.read(logId);

        assertThat(logs.getLogs()).isEmpty();
    }

    @Test
    void retrieveSingleLogForGivenId() {
        diagramLogger.log(log);
        diagramLogger.markEnd(logId);

        Logs logs = diagramLogger.read(logId);

        assertThat(logs.getLogs()).containsExactly(log);
    }

    @Test
    void retrieveGoldenMasterLogs() {
        diagramLogger.log(log);
        diagramLogger.markEnd(logId);
        diagramLogger.markGoldenMaster(logId);

        List<Logs> goldenMasterLogs = diagramLogger.readGoldenMaster(logId);

        assertThat(goldenMasterLogs.size()).isEqualTo(1);
        assertThat(goldenMasterLogs.get(0).getLogs()).containsExactly(log);
    }

    @Test
    void excludePriorLogs() {
        diagramLogger.log(log);
        diagramLogger.markEnd("Previous Id");
        diagramLogger.log(secondLog);
        diagramLogger.markEnd(logId);

        Logs logs = diagramLogger.read(logId);

        assertThat(logs.getLogs()).containsExactly(secondLog);
    }

    private String license() {
        String envVarLicenseContent = readEnvVar(ENV_VAR_LICENSE);
        if (null == envVarLicenseContent)
            return readLicenseFromEnvFile();
        return envVarLicenseContent;
    }

    private String readEnvVar(String envVarName) {
        return System.getenv(envVarName);
    }

    private String readLicenseFromEnvFile() {
        String envContent = "No License File Found in .env! Its not in git!";
        try {
            envContent = Files.readString(Path.of(".env"));
            envContent = envContent.replaceAll("license=", "");
            return envContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
