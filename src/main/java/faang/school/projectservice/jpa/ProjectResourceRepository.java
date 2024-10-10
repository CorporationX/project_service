package faang.school.projectservice.jpa;

import faang.school.projectservice.model.ProjectResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectResourceRepository extends JpaRepository<ProjectResource, Long> {

    @Query("SELECT r FROM ProjectResource r JOIN FETCH r.project p WHERE r.id = :resourceId AND p.id = :projectId")
    Optional<ProjectResource> findResourceWithProject(@Param("resourceId") Long resourceId, @Param("projectId") Long projectId);
}
