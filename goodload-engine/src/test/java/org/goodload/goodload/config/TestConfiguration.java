/*
 * Copyright (C) 2021 Divyansh Shekhar Gaur
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
package org.goodload.goodload.config;

import org.goodload.goodload.reporting.ReportAggregator;
import org.springframework.context.annotation.Bean;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
//@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    @Bean
    public ReportAggregator reportAggregator() {
        return new ReportAggregator();
    }
}
