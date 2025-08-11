package faang.school.projectservice.repository;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long>, JpaSpecificationExecutor<Internship> {

    default Internship getRequiredById(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found"));
    }

    List<Internship> findAllByProjectId(Long projectId);
}
