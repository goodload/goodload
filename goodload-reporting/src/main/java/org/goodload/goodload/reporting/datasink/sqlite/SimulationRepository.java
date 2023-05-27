package org.goodload.goodload.reporting.datasink.sqlite;

import org.goodload.goodload.reporting.datasink.sqlite.models.SimulationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends JpaRepository<SimulationEntity, Integer> {

}
