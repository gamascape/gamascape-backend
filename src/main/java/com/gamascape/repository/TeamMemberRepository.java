package com.gamascape.repository;

import com.gamascape.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByPubliclyVisibleTrue();
}
