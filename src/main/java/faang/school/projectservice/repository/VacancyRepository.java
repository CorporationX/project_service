package faang.school.projectservice.repository;

import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
}
