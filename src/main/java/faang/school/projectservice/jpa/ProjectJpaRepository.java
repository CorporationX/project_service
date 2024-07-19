package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Modifying
    @Query(
            value = "WITH RECURSIVE branching AS ( " +
                    "SELECT id, status, 1 as level " +
                    "FROM Project " +
                    "WHERE id = :projectId " +
                    "UNION ALL " +
                    "SELECT p.id, p.status, b.level + 1 " +
                    "FROM Project p " +
                    "INNER JOIN branching b on p.parent_project_id = b.id " +
                    ") " +
                    "UPDATE Project " +
                    "SET status = 'COMPLETED' " +
                    "WHERE id IN (SELECT id FROM branching) ",
            nativeQuery = true
    )
    void completeProjectSubprojects(Long projectId);

    @Modifying
    @Query(
            value = "WITH RECURSIVE branching AS ( " +
                    "SELECT id, status, 1 as level " +
                    "FROM Project " +
                    "WHERE id = :projectId " +
                    "UNION ALL " +
                    "SELECT p.id, p.status, b.level + 1 " +
                    "FROM Project p " +
                    "INNER JOIN branching b on p.parent_project_id = b.id " +
                    ") " +
                    "UPDATE Project " +
                    "SET visibility = 'PRIVATE' " +
                    "WHERE id IN (SELECT id FROM branching) ",
            nativeQuery = true
    )
    void makeSubprojectsPrivate(Long projectId);
}


