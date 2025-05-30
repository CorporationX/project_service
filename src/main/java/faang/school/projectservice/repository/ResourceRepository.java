package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query(nativeQuery = true, value = "select * from project_resource where key = :key;")
    Optional<Resource> findByKey(String key);

    @Query(nativeQuery = true, value = "select * from project_resource where name = :fileName and project_id = :projectId;")
    Optional<Resource> findByNameAndProjectId(String fileName, long projectId);
}
