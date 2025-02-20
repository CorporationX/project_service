package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VacancyRepository extends JpaRepository<faang.school.projectservice.model.Vacancy, UUID> {
    List<Vacancy> findByPositionAndTitleContainingIgnoreCase(TeamRole position, String title);
}
