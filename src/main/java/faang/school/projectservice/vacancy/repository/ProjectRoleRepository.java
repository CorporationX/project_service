package faang.school.projectservice.vacancy.repository;

import faang.school.projectservice.vacancy.entity.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRole, UUID> {
    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
}
