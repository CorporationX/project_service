package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    default Internship findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Internship with ID %d was not found".formatted(id)));
    }
}