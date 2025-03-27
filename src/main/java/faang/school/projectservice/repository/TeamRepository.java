package faang.school.projectservice.repository;

import faang.school.projectservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
