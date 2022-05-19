package io.github.scafer.prices.crawler.content.util;

import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.Defaults;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.packageresolver.Command;
import de.flapdoodle.embed.process.config.RuntimeConfig;
import de.flapdoodle.embed.process.config.process.ImmutableProcessOutput;
import de.flapdoodle.embed.process.config.process.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.Slf4jLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

@Log4j2
@Getter
public class EmbeddedMongoDBServer {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedMongoDBServer.class);
    private final Version.Main version = Version.Main.PRODUCTION;
    private MongodProcess mongodProcess;
    private String host = "localhost";
    private int port = 27017;
    private boolean active;

    public static EmbeddedMongoDBServer create() {
        return new EmbeddedMongoDBServer();
    }

    public EmbeddedMongoDBServer withHost(String host) {
        this.host = host;
        return this;
    }

    public EmbeddedMongoDBServer withPort(int port) {
        this.port = port;
        return this;
    }

    public EmbeddedMongoDBServer start() {
        if (!this.active && !inUse(this.port)) {
            try {
                ImmutableProcessOutput processOutput = ProcessOutput.builder()
                        .error(Processors.logTo(LOG, Slf4jLevel.ERROR))
                        .commands(Processors.logTo(LOG, Slf4jLevel.DEBUG))
                        .output(Processors.logTo(LOG, Slf4jLevel.DEBUG))
                        .build();

                var command = Command.MongoD;
                RuntimeConfig runtimeConfig = Defaults.runtimeConfigFor(command)
                        .processOutput(processOutput)
                        .build();

                this.mongodProcess = MongodStarter.getInstance(runtimeConfig).prepare(MongodConfig.builder()
                                .version(this.version)
                                .net(new Net(this.host, this.port, false))
                                .build())
                        .start();

                this.active = true;
            } catch (final IOException e) {
                log.error("EmbeddedMongoDBServer failed to start - {}:{}", this.host, this.port, e);
            }
        } else {
            log.warn("EmbeddedMongoDBServer failed to start.");
        }

        return this;
    }

    public void stop() {
        if (this.active) {
            this.mongodProcess.stop();
            this.active = false;
        }
    }

    private boolean inUse(int port) {
        try (var serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host))) {
            return serverSocket == null;
        } catch (IOException e) {
            log.warn("EmbeddedMongoDBServer - port is already in use - {}:{}", this.host, this.port, e);
            return true;
        }
    }
}