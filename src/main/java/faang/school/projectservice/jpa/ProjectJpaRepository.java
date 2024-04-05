package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(nativeQuery = true, value = """                    
            SELECT DISTINCT p.*
            FROM project p
                LEFT JOIN team t ON p.id = t.project_id
                LEFT JOIN team_member tm ON t.id = tm.team_id
            WHERE p.visibility = 'PUBLIC'
                OR (p.visibility = 'PRIVATE'
                    AND tm.user_id = :requestUserId);                                   
            """
    )
    List<Project> findProjectByOwnerIdAndTeamMember(long requestUserId);

    @Query(nativeQuery = true, value = """                    
            SELECT DISTINCT p.* FROM project p
            JOIN team t ON p.id = t.project_id
            JOIN team_member tm ON t.id = tm.team_id
            WHERE tm.user_id = :requestUserId
            """
    )
    List<Project> findProjectByTeamMember(long requestUserId);


}

