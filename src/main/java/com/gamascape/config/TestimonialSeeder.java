package com.gamascape.config;

import com.gamascape.entity.Testimonial;
import com.gamascape.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TestimonialSeeder implements CommandLineRunner {

    private final TestimonialRepository testimonialRepository;

    @Override
    public void run(String... args) throws Exception {
        testimonialRepository.deleteAll();

        Testimonial t1 = new Testimonial();
        t1.setName("Ms. Rekha");
        t1.setContent("Our experience with Gamascape was nothing short of exceptional. From the initial consultation to the final reveal, they transformed our vision into reality with incredible attention to detail. The design not only enhanced the aesthetics of our space but also made it more functional and comfortable. The team was professional, responsive, and truly understood our style preferences. Our home now feels luxurious yet cozy, and we couldn't be happier. Highly recommended!");
        t1.setRole("Leading Serial Artist, Thiruvananthapuram");
        t1.setRating(5);
        t1.setCreatedAt(LocalDateTime.now().minusDays(10));
        t1.setActive(true);

        Testimonial t2 = new Testimonial();
        t2.setName("Mr. Shaju");
        t2.setContent("Working with Akhil and Abin was an absolute pleasure. They took care of architecture, interior, and contracting seamlessly. Extremely transparent progress!");
        t2.setRole("Homeowner, Thiruvananthapuram");
        t2.setRating(5);
        t2.setCreatedAt(LocalDateTime.now().minusDays(5));
        t2.setActive(true);

        testimonialRepository.saveAll(Arrays.asList(t1, t2));
        System.out.println("Testimonials seeded successfully.");
    }
}
