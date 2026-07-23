package com.gamascape.repository;

import com.gamascape.entity.ClientWork;
import com.gamascape.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClientWorkRepository extends JpaRepository<ClientWork, Long> {
    List<ClientWork> findByClient(User client);
    List<ClientWork> findByAssignedTo(User assignedTo);
    Optional<ClientWork> findByWorkCode(String workCode);
}
