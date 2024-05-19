package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacancyJpaRepository extends JpaRepository<Vacancy, Long> {
}
