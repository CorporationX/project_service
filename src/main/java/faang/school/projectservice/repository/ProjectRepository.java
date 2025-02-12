package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import feign.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(
            "SELECT DISTINCT tm.userId " +
                    "FROM TeamMember tm " +
                    "JOIN tm.team t " +
                    "WHERE t.project.id IN :projectIds"
    )
    List<Long> getUserIdsByProjectIds(List<Long> projectIds);

    @Query("SELECT tm.id FROM TeamMember tm " +
            "JOIN tm.team t " +
            "JOIN t.project p " +
            "WHERE p.id = :projectId")
    List<Long> findAllTeamMemberIdsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT p FROM Project p WHERE p.parentProject.id = :parentProjectId")
    Page<Project> findByParentProjectId(Long parentProjectId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.googleCalendarId = :googleCalendarId")
    Project findByGoogleCalendarId(String googleCalendarId);
}

