package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END 
            FROM Project p 
            WHERE p.ownerId = :ownerId AND p.name = :name
            """
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    default Project findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with ID %d was not found".formatted(id)));
    }
}

