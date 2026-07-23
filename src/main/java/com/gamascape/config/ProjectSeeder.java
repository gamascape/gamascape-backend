package com.gamascape.config;

import com.gamascape.entity.Project;
import com.gamascape.entity.Phase;
import com.gamascape.entity.User;
import com.gamascape.entity.ClientProject;
import com.gamascape.repository.ProjectRepository;
import com.gamascape.repository.UserRepository;
import com.gamascape.repository.ClientProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectSeeder implements CommandLineRunner {

    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
    private final ClientProjectRepository clientProjectRepo;

    @Override
    public void run(String... args) {
        // Seed default users if they don't exist
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder = 
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        // Clean up or migrate old admin email if it exists
        userRepo.findByEmail("admin@gamascape.com").ifPresent(oldAdmin -> {
            userRepo.findByEmail("gamascape@gmail.com").ifPresentOrElse(
                existingNewUser -> {
                    userRepo.delete(oldAdmin);
                    existingNewUser.setRole(User.Role.ADMIN);
                    existingNewUser.setPassword(passwordEncoder.encode("Joj5547r@"));
                    userRepo.save(existingNewUser);
                },
                () -> {
                    oldAdmin.setEmail("gamascape@gmail.com");
                    oldAdmin.setPassword(passwordEncoder.encode("Joj5547r@"));
                    userRepo.save(oldAdmin);
                }
            );
        });

        User adminUser = userRepo.findByEmail("gamascape@gmail.com").orElseGet(() -> {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("gamascape@gmail.com");
            admin.setPassword(passwordEncoder.encode("Joj5547r@"));
            admin.setRole(User.Role.ADMIN);
            return userRepo.save(admin);
        });

        if (!"Admin".equals(adminUser.getName())) {
            adminUser.setName("Admin");
            userRepo.save(adminUser);
        }

        if (!passwordEncoder.matches("Joj5547r@", adminUser.getPassword())) {
            adminUser.setPassword(passwordEncoder.encode("Joj5547r@"));
            userRepo.save(adminUser);
        }


        User teamUser = userRepo.findByEmail("team@gamascape.com").orElseGet(() -> {
            User team = new User();
            team.setName("Team");
            team.setEmail("team@gamascape.com");
            team.setPassword(passwordEncoder.encode("password"));
            team.setRole(User.Role.TEAM);
            return userRepo.save(team);
        });

        if (!"Team".equals(teamUser.getName())) {
            teamUser.setName("Team");
            userRepo.save(teamUser);
        }

        User clientUser = userRepo.findByEmail("client@gamascape.com").orElseGet(() -> {
            User client = new User();
            client.setName("Client");
            client.setEmail("client@gamascape.com");
            client.setPassword(passwordEncoder.encode("password"));
            client.setRole(User.Role.CLIENT);
            return userRepo.save(client);
        });

        if (!"Client".equals(clientUser.getName())) {
            clientUser.setName("Client");
            userRepo.save(clientUser);
        }

        clientProjectRepo.deleteAll();
        projectRepo.deleteAll();

        Project p1 = new Project();
        p1.setName("space foyer");
        p1.setProjectCode("PRJ-001");
        p1.setLocation("Bangalore");
        p1.setDescription("Commercial | Luxury Sitting Room & Lounge Design | Completed");
        p1.setProgress(100);
        p1.setStatus("COMPLETED");
        p1.setImageEmoji("🛋️");
        p1.setBackImageUrl("project-lounge-1.webp,project-lounge-2.webp,project-lounge-3.webp");
        p1.setPubliclyVisible(true);
        p1.setDeadline(LocalDateTime.now().minusMonths(2));
        p1.setEstimateFileName("space foyer.pdf");
        p1.setEstimateFilePath("space foyer.pdf");

        List<Phase> phases1 = new ArrayList<>();
        phases1.add(createPhase("Space Planning & Concept", true, false, 1, p1));
        phases1.add(createPhase("Material Spec & Custom Screen", true, false, 2, p1));
        phases1.add(createPhase("Site Installation & Handover", true, true, 3, p1));
        p1.setPhases(phases1);

        Project p2 = new Project();
        p2.setName("Kitchen render");
        p2.setProjectCode("PRJ-002");
        p2.setLocation("Cochin");
        p2.setDescription("Residential | Modern Kitchen Suite with Integrated Laundry | Completed");
        p2.setProgress(100);
        p2.setStatus("COMPLETED");
        p2.setImageEmoji("🍳");
        p2.setBackImageUrl("project-kitchen-1.webp,project-kitchen-2.webp,project-kitchen-3.webp,project-kitchen-4.webp,project-kitchen-5.webp");
        p2.setPubliclyVisible(true);
        p2.setDeadline(LocalDateTime.now().minusMonths(1));
        p2.setEstimateFileName("Kitchen render.pdf");
        p2.setEstimateFilePath("Kitchen render.pdf");

        List<Phase> phases2 = new ArrayList<>();
        phases2.add(createPhase("Client Brief & Layout Draft", true, false, 1, p2));
        phases2.add(createPhase("Cabinet Fabrication & Finishing", true, false, 2, p2));
        phases2.add(createPhase("Appliance Installation & Handover", true, true, 3, p2));
        p2.setPhases(phases2);

        Project p3 = new Project();
        p3.setName("Mr.Vinu Ground floor");
        p3.setProjectCode("PRJ-003");
        p3.setLocation("Trivandrum");
        p3.setDescription("Residential | Complete Architectural & Interior Design for a 4-Bedroom Villa | Ongoing");
        p3.setProgress(75);
        p3.setStatus("ACTIVE");
        p3.setImageEmoji("🏡");
        p3.setBackImageUrl("project-house-1.webp,project-house-2.webp,project-house-3.webp,project-house-4.webp,project-house-5.webp,project-house-6.webp,project-house-7.webp,project-house-8.webp,project-house-9.webp,project-house-10.webp,project-house-11.webp,project-house-12.webp,project-house-13.webp,project-house-14.webp,project-house-15.webp,project-house-16.webp,project-house-17.webp");
        p3.setPubliclyVisible(true);
        p3.setDeadline(LocalDateTime.now().plusMonths(6));
        p3.setEstimateFileName("Mr.Vinu Ground floor.pdf");
        p3.setEstimateFilePath("Mr.Vinu Ground floor.pdf");

        List<Phase> phases3 = new ArrayList<>();
        phases3.add(createPhase("Architectural Layout & Approvals", true, false, 1, p3));
        phases3.add(createPhase("Structural Framing & Brickwork", true, false, 2, p3));
        phases3.add(createPhase("Interior Customization & Electrical Layout", false, true, 3, p3));
        p3.setPhases(phases3);

        projectRepo.saveAll(List.of(p1, p2, p3));

        // Assign all three projects to the client user
        ClientProject cp1 = new ClientProject();
        cp1.setUser(clientUser);
        cp1.setProject(p1);
        cp1.setUnitLabel("Unit 4A");

        ClientProject cp2 = new ClientProject();
        cp2.setUser(clientUser);
        cp2.setProject(p2);
        cp2.setUnitLabel("Kitchen Unit");

        ClientProject cp3 = new ClientProject();
        cp3.setUser(clientUser);
        cp3.setProject(p3);
        cp3.setUnitLabel("Emerald Block");

        clientProjectRepo.saveAll(List.of(cp1, cp2, cp3));
    }

    private Phase createPhase(String name, boolean done, boolean current, int order, Project p) {
        Phase ph = new Phase();
        ph.setPhaseName(name);
        ph.setDone(done);
        ph.setCurrent(current);
        ph.setDisplayOrder(order);
        ph.setProject(p);
        ph.setPhaseDate("July 2026");
        return ph;
    }
}
