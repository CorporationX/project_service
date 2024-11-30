package faang.school.projectservice.repository;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t WHERE t.project.id = :projectId")
    List<Team> findByProjectId(Long projectId);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id IN :teamIds")
    List<TeamMember> findByTeamIds(List<Long> teamIds);
}
