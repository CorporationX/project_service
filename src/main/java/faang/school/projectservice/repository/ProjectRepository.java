package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Modifying
    @Query(value = """
            update project set cover_image_id = :coverImageKey where id = :projectId
            """,
            nativeQuery = true
    )
    void updateCoverImage(@Param("projectId") Long projectId, @Param("coverImageKey") String coverImageKey);

    @Query(value = """
            select p.coverImageId from Project p where p.id = :projectId
            """)
    Optional<String> findCoverImageIdById(@Param("projectId") Long projectId);
}

