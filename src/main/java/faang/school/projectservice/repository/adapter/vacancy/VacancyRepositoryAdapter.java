package faang.school.projectservice.repository.adapter.vacancy;

import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyRepositoryAdapter {
    private final VacancyRepository vacancyRepository;

    public Vacancy getVacancyOrThrow(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException(id));
    }

    public void save(Vacancy vacancy) {
        vacancyRepository.save(vacancy);
    }
}
