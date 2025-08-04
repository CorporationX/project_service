package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    default Vacancy getByIdOrThrow(long vacancyId) {
        return findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Нет вакансии с таким vacancyId " + vacancyId));
    }
}
