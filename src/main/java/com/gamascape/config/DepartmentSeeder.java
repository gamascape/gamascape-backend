package com.gamascape.config;

import com.gamascape.entity.Department;
import com.gamascape.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DepartmentSeeder implements CommandLineRunner {

    private final DepartmentRepository deptRepo;

    @Override
    public void run(String... args) {
        if (deptRepo.count() == 0) {
            Department d1 = new Department(); d1.setName("Architecture");
            Department d2 = new Department(); d2.setName("Urban Planning");
            Department d3 = new Department(); d3.setName("Finance");
            Department d4 = new Department(); d4.setName("Execution");
            deptRepo.saveAll(List.of(d1, d2, d3, d4));
        }
    }
}
