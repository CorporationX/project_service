package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query(nativeQuery = true, value = "SELECT r.* FROM Resource r " +
        "JOIN project p ON r.key = p.cover_image_id " +
        "WHERE p.id = ?1")
Optional<Resource> findResourceByProjectId(Long id);
}
