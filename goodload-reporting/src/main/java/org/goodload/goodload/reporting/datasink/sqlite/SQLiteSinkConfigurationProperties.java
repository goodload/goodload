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

import lombok.Data;
import org.goodload.goodload.reporting.config.ReportingConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@ConfigurationProperties(prefix = SQLiteSinkConfigurationProperties.PREFIX)
@Data
public class SQLiteSinkConfigurationProperties {
    public static final String PREFIX = ReportingConfigurationProperties.PREFIX + ".sink.sqlite.batch-size";

    private int batchSize = 1000;
}
