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
package org.goodload.goodload.plugin.datasink.sqlite.data;

import jakarta.annotation.Resource;
import org.goodload.goodload.plugin.datasink.sqlite.models.ActionReportEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository bean for interacting with iteration reports
 *
 * @author divsgaur
 * @since 1.0
 */
@Repository
public class IterationReportRegistry {

    @Resource
    private IterationReportRepository iterationReportRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void insertAll(Iterable<ActionReportEntity> reports) {
        iterationReportRepository.saveAll(reports);
    }
}
