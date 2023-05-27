package org.goodload.goodload.reporting.datasink.sqlite;

import org.goodload.goodload.reporting.datasink.Sink;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.goodload.goodload.reporting.config.ReportingConfigurationProperties.PREFIX;

@Configuration
//@ConditionalOnProperty(prefix = PREFIX, name = "sink-type", havingValue = "SQLite")
@EnableConfigurationProperties(SQLiteSinkConfigurationProperties.class)
public class SQLiteSinkConfiguration {

    @Bean
    public Sink sink(
            IterationReportRepository iterationReportRepository,
            SimulationRepository simulationRepository,
            SQLiteSinkConfigurationProperties sqLiteSinkConfigurationProperties) {
        return new SQLiteSink(iterationReportRepository, simulationRepository, sqLiteSinkConfigurationProperties);
    }

    @Bean
    public DataSource dataSource() throws IOException {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        var dbDirectoryPath = Path.of(System.getProperty("java.io.tmpdir"), "goodload");
        if (!Files.exists(dbDirectoryPath)) {
            Files.createDirectories(dbDirectoryPath);
        }
        var dbFilePath = dbDirectoryPath.resolve(UUID.randomUUID() + ".db");

        final var dbName = "goodload";

//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource
        dataSource.setSchema("sink/sqlite/schema.sql");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + dbName + "?createDatabaseIfNotExist=true&useUnicode=yes" +
                "&characterEncoding=UTF-8&logger=com.mysql.cj.log.Slf4JLogger&profileSQL=true");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        return dataSource;
    }
}
