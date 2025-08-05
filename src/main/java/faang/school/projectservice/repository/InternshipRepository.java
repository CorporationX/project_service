package faang.school.projectservice.repository;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    default Internship getRequiredById(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found"));
    }

    List<Internship> findAllProjectsId(Long projectId);
}
