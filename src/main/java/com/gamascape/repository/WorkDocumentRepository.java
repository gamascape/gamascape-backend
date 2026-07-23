package com.gamascape.repository;

import com.gamascape.entity.WorkDocument;
import com.gamascape.entity.ClientWork;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkDocumentRepository extends JpaRepository<WorkDocument, Long> {
    List<WorkDocument> findByClientWork(ClientWork clientWork);
}
