package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.InternshipJpaRepository;
import faang.school.projectservice.model.Internship;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InternshipRepository {

    private final InternshipJpaRepository internshipJpaRepository;

    public boolean existsByName(String name) {
        return internshipJpaRepository.findByName(name).isPresent();
    }

    public Internship findById(Long id) {
        return internshipJpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Internship doesn't exist by id: %s", id)));
    }
}
