package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByProjectId(Long projectId);

    List<Resource> findAllByProjectIdAndStatus(Long projectId, ResourceStatus status);
    Optional<Resource> findByKey(String key);
}
