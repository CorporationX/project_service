package faang.school.projectservice.repository;

import faang.school.projectservice.exception.vacancy.EntityNotFoundException;
import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
            select r from Resource r
            where r.key = :key
            """)
    Optional<Resource> findByKey(String key);

    default Resource getByIdOrThrow(long resourceId) {
        return findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Resource %d not found", resourceId))
        );
    }
}
