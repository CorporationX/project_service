package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Page<Resource> findByProjectIdAndStatus(
            Long projectId,
            ResourceStatus status,
            Pageable pageable
    );

    List<Resource> findByProjectIdAndCreatedById(Long projectId, Long memberId);

    Optional<Resource> findByKey(String key);

    Optional<Resource> findByIdAndProjectId(
            Long resourceId,
            Long projectId
    );

    @Query("SELECT SUM(r.size) FROM Resource r WHERE r.project.id = :projectId AND r.status = 'ACTIVE'")
    Long calculateProjectStorageSize(@Param("projectId") Long projectId);

    @Modifying
    @Query("UPDATE Resource r SET r.status = :status WHERE r.id = :id")
    int updateStatus(
            @Param("id") Long id,
            @Param("status") ResourceStatus status
    );
}
