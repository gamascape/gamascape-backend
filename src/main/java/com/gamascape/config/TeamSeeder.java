package com.gamascape.config;

import com.gamascape.entity.TeamMember;
import com.gamascape.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamSeeder implements CommandLineRunner {

    private final TeamMemberRepository teamRepo;

    @Override
    public void run(String... args) {
        teamRepo.deleteAll();

        TeamMember m1 = new TeamMember();
        m1.setName("Er. Akhil Thomas George");
        m1.setRole("Co-founder, Civil Engineer");
        m1.setInit("ATG");
        m1.setBio("Co-founder & Civil Engineer. Mr. Akhil brings 10+ years of expertise in Project Management. Key role in delivering high-quality, cost-effective projects.");

        TeamMember m2 = new TeamMember();
        m2.setName("Ar. Abin Varghese George");
        m2.setRole("Co-founder, Architect");
        m2.setInit("AVG");
        m2.setBio("Co-founder & Architect. Mr. Abin is dedicated to creating spaces that blend aesthetics and sustainability, with expertise in neuroarchitecture and wellness-focused design.");

        teamRepo.saveAll(List.of(m1, m2));
    }
}
