package faang.school.projectservice.repository;

import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
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

    default Project getByIdOrThrow(long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Project %d not found", id))
        );
    }
}

