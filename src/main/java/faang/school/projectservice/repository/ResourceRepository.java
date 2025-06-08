package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<Resource> findByKey(String key);

    Optional<Resource> findByNameAndProjectId(String fileName, long projectId);
}
