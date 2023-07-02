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

CREATE TABLE action_metadata
(
    step_id        varchar(255) not null,
    parent_step_id varchar(255),
    simulation_id  varchar(255) not null,
    step_name      varchar(255) not null,
    primary key (step_id)
);
CREATE TABLE action_report
(
    id                        varchar(255) not null,
    end_timestamp_in_millis   bigint       not null,
    ended_normally            boolean      not null,
    iteration_index           integer      not null,
    start_timestamp_in_millis bigint       not null,
    step_id                   varchar(255) not null,
    primary key (id)
);
CREATE TABLE simulation_metadata
(
    simulation_id   varchar(255) not null,
    simulation_name varchar(255) not null,
    primary key (simulation_id)
);