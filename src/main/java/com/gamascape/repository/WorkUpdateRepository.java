package com.gamascape.repository;

import com.gamascape.entity.WorkUpdate;
import com.gamascape.entity.ClientWork;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkUpdateRepository extends JpaRepository<WorkUpdate, Long> {
    List<WorkUpdate> findByClientWorkOrderByPostedAtDesc(ClientWork clientWork);
}
