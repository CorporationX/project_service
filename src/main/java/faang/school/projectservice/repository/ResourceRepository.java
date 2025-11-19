package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("SELECT r FROM Resource r WHERE r.project.id = :projectId AND r.status = :status")
    Page<Resource> findByProjectIdAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") ResourceStatus status,
            Pageable pageable
    );

    @Query("SELECT r FROM Resource r WHERE r.project.id = :projectId AND r.createdBy.id = :memberId")
    List<Resource> findProjectIdAndCreatedBy(
            @Param("projectId") Long projectId,
            @Param("memberId") Long memberId
    );

    @Query("SELECT r FROM Resource r WHERE r.key = :key")
    Optional<Resource> findByKey(
            @Param("key") String key
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
