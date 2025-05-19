package faang.school.projectservice.repository;

import faang.school.projectservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByProject_Id(Long projectId);
}
