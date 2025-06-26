package faang.school.projectservice.repository;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByTeamMembers(TeamMember teamMember);
}
