package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(nativeQuery = true, value = """
            select case when count(*) > 0 then true else false end from project p
            where p.id = :projectId AND p.owner_id = :userId
            """)
    boolean checkUserIsProjectOwner(@Param("projectId") long projectId,
                                    @Param("userId") long userId);
}
