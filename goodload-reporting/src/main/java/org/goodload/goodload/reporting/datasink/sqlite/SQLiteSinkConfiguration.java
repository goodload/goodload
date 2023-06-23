/*
 * Copyright (C) 2023 Divyansh Shekhar Gaur
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.goodload.goodload.reporting.datasink.sqlite;

import org.goodload.goodload.reporting.datasink.Sink;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(SQLiteSinkConfigurationProperties.class)
@EnableTransactionManagement
public class SQLiteSinkConfiguration {
    @Bean
    public Sink sink(
            IterationReportRegistry iterationReportRegistry,
            SimulationRepository simulationRepository,
            SQLiteSinkConfigurationProperties sqLiteSinkConfigurationProperties) {
        return new SQLiteSink(iterationReportRegistry, simulationRepository, sqLiteSinkConfigurationProperties);
    }

    @Bean
    public DataSource dataSource() throws IOException {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        var dbDirectoryPath = Path.of(System.getProperty("java.io.tmpdir"), "goodload", "reports");
        if (!Files.exists(dbDirectoryPath)) {
            Files.createDirectories(dbDirectoryPath);
        }
        var dbFilePath =
                dbDirectoryPath.resolve(
                        String.format(
                                "goodload--%s.db",
                                UUID.randomUUID())).toAbsolutePath().toString().replace('\\', '/');

        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:" + dbFilePath);
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
