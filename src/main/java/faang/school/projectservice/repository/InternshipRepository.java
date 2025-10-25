package faang.school.projectservice.repository;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    default Internship getByIdOrThrow(long internshipId) {
        return findById(internshipId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Internship %d not found", internshipId))
        );
    }
}
