package org.goodload.goodload.reporting.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationTreeRepository extends JpaRepository<SimulationTree, Integer> {

}
